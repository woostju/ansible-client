package com.github.woostju.ansible.command;

import java.util.List;

import org.assertj.core.util.Lists;

import com.github.woostju.ansible.Module;

public class CmdModuleCommand extends AdhocCommand{

	public CmdModuleCommand(List<String> hosts, String command, List<String> options) {
		super(hosts, Module.command, Lists.newArrayList(command), options);
	}
}
