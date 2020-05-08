package com.github.woostju.ansible.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.woostju.ansible.AnsibleClient;
import com.github.woostju.ansible.ReturnValue;

/**
 * Run playbook with ansible-playbook executable
 * <p> sample: ansible-playbook -i inventory playbook.yml
 * <p> Get more info from <a href="https://docs.ansible.com/ansible/latest/user_guide/playbooks.html">playbook docs</a> 
 * @author jameswu
 *
 */
public class PlaybookCommand extends Command{

	private String playbookPath;
	
	/**
	 * @param hosts target hosts
	 * @param playbookPath Playbook file path on ansible server
	 * @param options options
	 */
	public PlaybookCommand(List<String> hosts, String playbookPath, List<String> options) {
		super(hosts, null, 
				null, 
				options);
		this.playbookPath = playbookPath;
	}
	
	@Override
	public List<String> createAnsibleCommands(AnsibleClient client, Command command) {
		List<String> commands = new ArrayList<>();
		commands.add(client.getAnsibleRootPath() + command.getExecutable());
		if(client.getInventoryPath()!=null){
			commands.add("-i");
			commands.add(client.getInventoryPath());
		}
		commands.add(this.playbookPath);
		if (null!= command.getOptions() && command.getOptions().size()>0) {
			commands.add(command.getOptions().stream().collect(Collectors.joining(" ")));
		}
		return commands;
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
						Matcher match2 = digital_pattern.matcher(match.group());
						if(match2.find()) {
							if (Integer.valueOf(match2.group())>0) {
								responses.get(ip).setResult(ReturnValue.Result.failed);
								continue;
							}
						}
					}
					match = unreachable_pattern.matcher(line);
					if(match.find()){
						Matcher match2 = digital_pattern.matcher(match.group());
						if(match2.find()) {
							if (Integer.valueOf(match2.group())>0) {
								responses.get(ip).setResult(ReturnValue.Result.unreachable);
								continue;
							}
						}
					}
					match = ok_pattern.matcher(line);
					if(match.find()){
						Matcher match2 = digital_pattern.matcher(match.group());
						if(match2.find()) {
							if (Integer.valueOf(match2.group())>0) {
								responses.get(ip).setResult(ReturnValue.Result.success);
								continue;
							}
						}
					}
					match = changed_pattern.matcher(line);
					if(match.find()){
						Matcher match2 = digital_pattern.matcher(match.group());
						if(match2.find()) {
							if (Integer.valueOf(match2.group())>0) {
								responses.get(ip).setResult(ReturnValue.Result.changed);
								continue;							}
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
