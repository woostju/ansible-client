package com.github.woostju.ansible.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.assertj.core.util.Lists;

import com.github.woostju.ansible.Module;
import com.github.woostju.ansible.ReturnValue;

public class PlaybookCommand extends AdhocCommand{

	public PlaybookCommand(List<String> hosts, String playbookPath, List<String> options) {
		super(Lists.newArrayList(playbookPath), Module.playbook, 
				null, 
				options);
	}

	@Override
	public String getExecutable() {
		return "ansible-playbook";
	}
	
	static Pattern play_recap_pattern = Pattern.compile("PLAY RECAP (\\*)*");
	static Pattern unreachable_pattern = Pattern.compile("unreachable=\\d");
	static Pattern failed_pattern = Pattern.compile("failed=\\d");
	static Pattern changed_pattern = Pattern.compile("changed=\\d");
	static Pattern ok_pattern = Pattern.compile("ok=\\d");
	static Pattern digital_pattern = Pattern.compile("\\d");
	
	@Override
	public Map<String, ReturnValue> parseCommandReturnValues(List<String> rawOutput) {
		/**
		 * 先寻找[WARNING]: Could not match supplied host pattern: 192.168.0.3
		 * 再寻找 PLAY RECAP *********************************************************************
		 * 获取每个ip对应的结果
		 * 输出结果将给到所有的ip
		 */
		
		Pattern ip_pattern = ResultValueHeader.head_ip_pattern;
		boolean recap = false;
		Map<String, ReturnValue> responses = new HashMap<>();
		for(String line : rawOutput){
			if(recap){
				Matcher matchIp =  ip_pattern.matcher(line);
				if(matchIp.find()){
					String ip = matchIp.group();
					responses.put(ip, new ReturnValue());
					responses.get(ip).setStdout(rawOutput);
					Matcher match =  failed_pattern.matcher(line);
					if(match.find()){
						if(Integer.valueOf(digital_pattern.matcher(line).group())>0) {
							responses.get(ip).setResult(ReturnValue.Result.failed);
							continue;
						}
					}
					match = unreachable_pattern.matcher(line);
					if(match.find()){
						if(Integer.valueOf(digital_pattern.matcher(line).group())>0) {
							responses.get(ip).setResult(ReturnValue.Result.unreachable);
							continue;
						}
					}
					match = changed_pattern.matcher(line);
					if(match.find()){
						if(Integer.valueOf(digital_pattern.matcher(line).group())>0) {
							responses.get(ip).setResult(ReturnValue.Result.changed);
							continue;
						}
					}
					match = ok_pattern.matcher(line);
					if(match.find()){
						if(Integer.valueOf(digital_pattern.matcher(line).group())>0) {
							responses.get(ip).setResult(ReturnValue.Result.success);
							continue;
						}
					}
				}
			}else{
				// find any hosts not in inventory
				if(line.contains("[WARNING]")){
					Matcher matchNotInInventory = ResultValueHeader.head_inventory_no_host_pattern.matcher(line);
					if(matchNotInInventory.matches()){
						Matcher matchIp = ResultValueHeader.head_ip_pattern.matcher(line);
						if(matchIp.find()){
							String ip = matchIp.group();
							responses.put(ip, new ReturnValue());
							responses.get(ip).setResult(ReturnValue.Result.unmanaged);
							responses.get(ip).setStdout(rawOutput);
						}
					}
				}else if(line.startsWith("PLAY")){
					// find the PLAY RECAP *********************
					if(play_recap_pattern.matcher(line).matches()){
						recap = true;
						continue;
					}
				}
			}
		}
		
		return responses;
	}
}
/**
 * 
 * ansible-playbook -i ../inventory playbook.yml 执行结果样例： 
 [WARNING]: Could not match supplied host pattern, ignoring: 192.168.0.3

 [WARNING]: Could not match supplied host pattern, ignoring: 192.168.0.2


PLAY [192.168.0.3 192.168.0.2 42.159.95.138 42.159.4.34 192.168.0.1] ****************************************************************************************************************

TASK [Gathering Facts] **************************************************************************************************************************************************************
ok: [42.159.4.34]
ok: [42.159.95.138]
fatal: [192.168.0.1]: UNREACHABLE! => {"changed": false, "msg": "Failed to connect to the host via ssh: ssh: connect to host 192.168.0.1 port 22: Connection timed out\r\n", "unreachable": true}

TASK [copyfile] *********************************************************************************************************************************************************************
ok: [42.159.4.34]
fatal: [42.159.95.138]: FAILED! => {"changed": false, "checksum": "b20f884c68872692fca1b3b6b4343cfbc15cb17e", "msg": "Destination directory /etc/anchora/cmp/temp does not exist"}

TASK [restart apache] ***************************************************************************************************************************************************************
fatal: [42.159.4.34]: FAILED! => {"changed": false, "msg": "Could not find the requested service httpd: host"}
	to retry, use: --limit @/etc/anchora/cmp/ansible/playbooks/playbook.retry

PLAY RECAP **************************************************************************************************************************************************************************
192.168.0.1                : ok=0    changed=0    unreachable=1    failed=0   
42.159.4.34                : ok=2    changed=0    unreachable=0    failed=1   
42.159.95.138              : ok=1    changed=0    unreachable=0    failed=1  
 * 
 */
