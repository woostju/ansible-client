package com.github.woostju.ansible.command;

import java.util.List;

import org.assertj.core.util.Lists;

import com.github.woostju.ansible.Module;

public class CopyCommand extends Command{

	public CopyCommand(List<String> hosts, List<String> module_args, List<String> options) {
		super(hosts, Module.copy.toString(),  module_args, options);
	}
	
	
	public CopyCommand(List<String> hosts, String srcPath, String destPath, Boolean force, String owner, String mode, List<String> options) {
		this(hosts, Lists.newArrayList(), options);
		this.getModule_args().add("src="+srcPath);
		this.getModule_args().add("dest="+destPath);
		if (mode!=null) {
			this.getModule_args().add("mode="+mode);
		}
		if (owner!=null) {
			this.getModule_args().add("owner="+owner);
		}
		if (force!=null) {
			this.getModule_args().add("force="+ (force?"yes":"no"));
			
		}
	}
}


