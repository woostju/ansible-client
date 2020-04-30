package com.github.woostju.ansible.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.woostju.ansible.Module;
import com.github.woostju.ansible.ReturnValue;
import com.github.woostju.ansible.util.JsonUtil;

public abstract class AdhocCommand{
	private List<String> hosts;
	private List<String> module_args;
	private Module module;
	private List<String> options;
	
	public AdhocCommand() {
		
	}
	
	public String getExecutable() {
		return "ansible";
	}
	
	public Map<String, ReturnValue> parseCommandReturnValues(List<String> rawOutput){
		Map<String, ReturnValue> returnValues = new HashMap<>(); 
		String currentKey = null;
		for(String line:rawOutput){
			// 检测输出所属ip，header中包含了ip地址、result等信息
			ResultValueHeader header = ResultValueHeader.createHeader(line);
			if(header!=null){
				// 这是一条包含头信息的日志
				ReturnValue resultValue = new ReturnValue();
				resultValue.setRc(header.getRc());
				resultValue.setResult(header.getResult());
				returnValues.put(header.getIp(), resultValue);
				currentKey = header.getIp();
			}else if(currentKey!=null){
				// 这是一条ansible针对某主机的输出
				returnValues.get(currentKey).getStdout().add(line);
			}
		}
		// all the return values are parsed, then, let command subclass to parse returnValue
		for(String key:returnValues.keySet()){
			// 处理每个ip的输出内容, 从log中抽取错误信息，日志信息
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
	
	public String toString() {
		List<String> commands = new ArrayList<String>();
		commands.add(this.hosts.stream().collect(Collectors.joining(":")));
		if (module != Module.playbook) {
			commands.add("-m "+module.toString());
		}
		if (null!= module_args && module_args.size()>0) {
			commands.add("-a '"+module_args.stream().collect(Collectors.joining(" "))+"'");
		}
		if (null!= module_args && module_args.size()>0) {
			commands.add(options.stream().collect(Collectors.joining(" ")));
		}
		return commands.stream().collect(Collectors.joining(" "));
	}
	
	public AdhocCommand(List<String> hosts, Module module, List<String> module_args, List<String> options){
		this.hosts = hosts;
		this.module = module;
		this.module_args = module_args;
		this.options = options;
	}

	public List<String> getHosts() {
		return hosts;
	}

	public void setHosts(List<String> hosts) {
		this.hosts = hosts;
	}

	public List<String> getModule_args() {
		return module_args;
	}

	public void setModule_args(List<String> module_args) {
		this.module_args = module_args;
	}

	public Module getModule() {
		return module;
	}

	public void setModule(Module module) {
		this.module = module;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	
	
}