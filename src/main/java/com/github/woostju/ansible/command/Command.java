package com.github.woostju.ansible.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.woostju.ansible.AnsibleClient;
import com.github.woostju.ansible.ReturnValue;
import com.github.woostju.ansible.util.JsonUtil;

/**
 * All the Ansible command should extends this command
 * @author jameswu
 *
 */
public abstract class Command{
	
	private List<String> hosts;
	
	private List<String> moduleArgs;
	
	private String module;
	
	private List<String> options;
	
	public Command() {
		
	}
	
	/**
	 * executable to run command
	 * @return default return ansible
	 */
	public String getExecutable() {
		return "ansible";
	}
	
	/**
	 * create Ansible command line to be sent
	 * @param client AnsibleClient
	 * @param command Command
	 * @return Ansible command line
	 */
	public List<String> createAnsibleCommands(AnsibleClient client, Command command) {
		List<String> commands = new ArrayList<>();
		commands.add(client.getAnsibleRootPath() + command.getExecutable());
		if(client.getInventoryPath()!=null){
			commands.add("-i");
			commands.add(client.getInventoryPath());
		}
		commands.add(command.getHosts().stream().collect(Collectors.joining(":")));
		if (command.getModule()!=null) {
			commands.add("-m "+command.getModule().toString());
		}
		if (null!= command.getModuleArgs() && command.getModuleArgs().size()>0) {
			if (client.getHostSshConfig()!=null) {
				commands.add("-a '"+command.getModuleArgs().stream().collect(Collectors.joining(" "))+"'");
			}else {
				commands.add("-a "+command.getModuleArgs().stream().collect(Collectors.joining(" ")));
			}
		}
		if (null!= command.getOptions() && command.getOptions().size()>0) {
			commands.add(command.getOptions().stream().collect(Collectors.joining(" ")));
		}
		return commands;
	}
	
	/**
	 * Parse the Ansible output
	 * @param rawOutput the Ansible output
	 * @return return value of output, key:ip address, value: {@link ReturnValue}
	 */
	public Map<String, ReturnValue> parseCommandReturnValues(List<String> rawOutput){
		Map<String, ReturnValue> returnValues = new HashMap<>(); 
		String currentKey = null;
		for(String line:rawOutput){
			// the line with ip is ResultValueHeader
			ResultValueHeader header = ResultValueHeader.createHeader(line);
			if(header!=null){
				// a ReturnValue object created for ip
				ReturnValue resultValue = new ReturnValue();
				resultValue.setRc(header.getRc());
				resultValue.setResult(header.getResult());
				returnValues.put(header.getIp(), resultValue);
				currentKey = header.getIp();
			}else if(currentKey!=null){
				// the line should put into stdout
				returnValues.get(currentKey).getStdout().add(line);
			}
		}
		// parse the ReturnValue.stdout to ReturnValue.value
		for(String key:returnValues.keySet()){
			ReturnValue returnValue = returnValues.get(key);
			if(returnValue.getResult() == ReturnValue.Result.unmanaged){
				returnValue.getStdout().add("[WARNING]: Could not match supplied host pattern, ignoring:"+key);
			} else{
				try {
					String outputJson = returnValue.getStdout().stream().collect(Collectors.joining());
					returnValue.setValue(JsonUtil.toHashMap("{"+outputJson, String.class, Object.class));	
				}catch(Exception e) {
					// sometime, the stdout is not a json object, and we ignore it 
				}
			}
		}
		return returnValues;
	}
	
	/**
	 * @param hosts target hosts
	 * @param module module name
	 * @param moduleArgs arguments of module
	 * @param options options for Ansible
	 */
	public Command(List<String> hosts, String module, List<String> moduleArgs, List<String> options){
		this.hosts = hosts;
		this.module = module;
		this.moduleArgs = moduleArgs;
		this.options = options;
	}

	public List<String> getHosts() {
		return hosts;
	}

	public void setHosts(List<String> hosts) {
		this.hosts = hosts;
	}

	public List<String> getModuleArgs() {
		return moduleArgs;
	}

	public void setModuleArgs(List<String> moduleArgs) {
		this.moduleArgs = moduleArgs;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}
}