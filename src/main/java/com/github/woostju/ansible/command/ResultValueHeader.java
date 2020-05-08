package com.github.woostju.ansible.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.woostju.ansible.ReturnValue;

/**
 * 
 *extract header info for each host, the info contains ip，result，rc
 * @author jameswu
 */
public class ResultValueHeader {
	// 42.159.4.34 | CHANGED | rc=0 >>
	static Pattern head_type1_pattern = Pattern.compile("^\\d+\\.\\d+\\.\\d+\\.\\d+( \\| ).*( \\| ).*>>$");
	
	static Pattern head_type1_rc_pattern = Pattern.compile("rc=\\d");
	
	static Pattern head_type1_result_pattern = Pattern.compile("( \\| ).*( \\| )");
	// 42.11.11.11 | UNREACHABLE! => {
	static Pattern head_type2_pattern = Pattern.compile("^\\d+\\.\\d+\\.\\d+\\.\\d+( \\| ).+=>.*\\{$");
	
	static Pattern head_type2_result_pattern = Pattern.compile("( \\| ).*( =>)");
	// the host is not in inventory
	static Pattern head_inventory_no_host_pattern = Pattern.compile(".*(Could not match supplied host pattern).*");
	
	static Pattern head_ip_pattern = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");
	
	private String ip;
	
	private ReturnValue.Result result;
	
	private Integer rc = -1;
	
	public String getIp(){
		return ip;
	}
	
	public Integer getRc(){
		return rc;
	}
	
	public ReturnValue.Result getResult(){
		return result;
	}
	
	enum HeaderType{
		type1, 
		type2, 
		type_not_in_inventory
	}

	/**
	 * 
	 * @param headerLine header line
	 * @return a {@link ResultValueHeader}, null if line is not a header
	 * 
	 */
	public static ResultValueHeader createHeader(String headerLine){
		ResultValueHeader header = new ResultValueHeader();
		HeaderType headerType;
		if(head_type1_pattern.matcher(headerLine).matches()){
			headerType = HeaderType.type1;
		}else if(head_type2_pattern.matcher(headerLine).matches()){
			headerType = HeaderType.type2;
		}else if(head_inventory_no_host_pattern.matcher(headerLine).matches()){
			headerType = HeaderType.type_not_in_inventory;
		}else{
			return null;
		}
		header.parseHeader(headerType, headerLine);
		return header;
	}
	
	private void parseHeader(HeaderType headerType, String headerLine){
		// parse ip
		Matcher matchIp = head_ip_pattern.matcher(headerLine);
		if (matchIp.find()) {
			this.ip = matchIp.group();
		}
		switch (headerType) {
			case type1:
			{
				// 42.159.4.34 | CHANGED | rc=0 >>
				// parse rc code
				Matcher matchRc = head_type1_rc_pattern.matcher(headerLine);
				if(matchRc.find()){
					String rc = matchRc.group();
					int code = Integer.valueOf(rc.replaceAll("rc=", ""));
					this.rc = code;
				}
				// parse result
				Matcher matchResult = head_type1_result_pattern.matcher(headerLine);
				if(matchResult.find()){
					String rawResult = matchResult.group();
					this.result = this.transferResult(rawResult.replaceAll("\\|", "").replaceAll(" ", ""));
				}
			}
			break;
			case type2:{
				// 42.11.11.11 | UNREACHABLE! => {
				Matcher matchResult = head_type2_result_pattern.matcher(headerLine);
				if(matchResult.find()){
					String rawResult = matchResult.group();
					this.result = this.transferResult(rawResult.replaceAll("\\|", "").replaceAll(" ", "").replaceAll("=>",""));
				}
			}
			break;
			case type_not_in_inventory:
			{
				// [WARNING]: Could not match supplied host pattern, ignoring: 12.12.12.12
				this.result = ReturnValue.Result.unmanaged;
			}
			break;
		default:
			break;
		}
	}
	
	private ReturnValue.Result transferResult(String result){
		if("CHANGED".equals(result)) {
			return ReturnValue.Result.changed;
		}
		if("SUCCESS".equals(result)) {
			return ReturnValue.Result.success;
		}
		if("UNREACHABLE!".equals(result)) {
			return ReturnValue.Result.unreachable;
		}
		if("FAILED!".equals(result)) {
			return ReturnValue.Result.failed;
		}
		return ReturnValue.Result.unknown;
	}
	
	@Override
	public String toString() {
		return this.ip + " " + this.rc + " " + this.result;
	}
}
