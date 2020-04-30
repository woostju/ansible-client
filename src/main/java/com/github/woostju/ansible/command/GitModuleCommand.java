package com.github.woostju.ansible.command;

import java.util.List;

import org.assertj.core.util.Lists;

import com.github.woostju.ansible.Module;

public class GitModuleCommand extends AdhocCommand{
	
	public GitModuleCommand(List<String> hosts, String gitRepo, String destDirectory, String gitSshKeyFilePath, List<String> module_args, List<String> options) {
		super();
		this.setHosts(hosts);
		this.setModule(Module.git);
		if (null==module_args) {
			this.setModule_args(Lists.newArrayList());
		}
		this.getModule_args().add(0, "force=yes");
		this.getModule_args().add(0, "key_file="+gitSshKeyFilePath);
		this.getModule_args().add(0, "dest="+gitSshKeyFilePath);
		this.getModule_args().add(0, "repo="+gitSshKeyFilePath);
	}

}
