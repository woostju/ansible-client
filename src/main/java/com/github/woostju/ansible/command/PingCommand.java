package com.github.woostju.ansible.command;

import java.util.List;

import com.github.woostju.ansible.Module;

public class PingCommand extends AdhocCommand{

	
	public PingCommand(List<String> hosts) {
		this(hosts, null);
	}
	
	
	public PingCommand(List<String> hosts, List<String> options) {
		super(hosts, Module.ping, null, options);
	}
}
