package com.github.woostju.ansible.command;

import java.util.List;

import org.assertj.core.util.Lists;

import com.github.woostju.ansible.Module;

public class ScriptCommand extends Command{

	
	public ScriptCommand(List<String> hosts, List<String> module_args, List<String> options) {
		super(hosts, Module.script.toString(), module_args==null?Lists.newArrayList():module_args, options);
	}
	
	public ScriptCommand(List<String> hosts, String scriptFilePath, String executablePath, List<String> options) {
		this(hosts, Lists.newArrayList(scriptFilePath, "executable="+executablePath), options);
	}

}
