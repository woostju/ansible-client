package com.github.woostju.ansible.command;

import java.util.List;

import org.assertj.core.util.Lists;

import com.github.woostju.ansible.Module;

public class FileModuleCommand extends AdhocCommand{
	
	public FileModuleCommand(List<String> hosts, String path, List<String> module_args, List<String> options) {
		super();
		this.setHosts(hosts);
		this.setModule(Module.file);
		if (null==module_args) {
			this.setModule_args(Lists.newArrayList());
		}
		this.getModule_args().add(0, "path="+path);
	}

}
