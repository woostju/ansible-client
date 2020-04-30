package com.github.woostju.ansible.command;

import java.util.List;

import org.assertj.core.util.Lists;

import com.github.woostju.ansible.Module;

public class ScriptModuleCommand extends AdhocCommand{

	public ScriptModuleCommand(List<String> hosts, String scriptFilePath, String executablePath, List<String> options) {
		super(hosts, Module.script, Lists.newArrayList(scriptFilePath, "executable="+executablePath), options);
	}

}
