package com.github.woostju.ansible.command;

import java.util.List;

import org.assertj.core.util.Lists;

import com.github.woostju.ansible.Module;

public class GitCommand extends AdhocCommand{
	
	public GitCommand(List<String> hosts,  List<String> module_args, List<String> options) {
		super(hosts, Module.git, module_args, options);
	}
	
	public GitCommand(List<String> hosts, String gitRepo, String destDirectory, String gitSshKeyFilePath, List<String> module_args, List<String> options) {
		this(hosts, module_args, options);
		if (null==module_args) {
			this.setModule_args(Lists.newArrayList());
		}
		this.getModule_args().add("force=yes");
		if (gitSshKeyFilePath!=null) {
			this.getModule_args().add("key_file="+gitSshKeyFilePath);
		}
		if(gitRepo!=null) {
			this.getModule_args().add("repo="+gitRepo);
		}
		if (destDirectory!=null) {
			this.getModule_args().add("dest="+destDirectory);
		}
	}
}
