package com.github.woostju.ansible.command;

import java.util.List;

import com.github.woostju.ansible.Module;

public class PingModuleCommand extends AdhocCommand{

	public PingModuleCommand(List<String> hosts, List<String> options) {
		super(hosts, Module.ping, null, options);
	}
}
