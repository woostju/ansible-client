package com.github.woostju.ansible.command;

import java.util.List;

import org.assertj.core.util.Lists;

import com.github.woostju.ansible.Module;

public class FileCommand extends Command{
	
	public FileCommand(List<String> hosts, List<String> module_args, List<String> options) {
		super(hosts, Module.file.toString(), module_args, options);
	}
	
	public FileCommand(List<String> hosts, String path, FileCommandState state, List<String> module_args, List<String> options) {
		this(hosts, module_args==null?Lists.newArrayList():module_args, options);
		this.getModule_args().add("path="+path);
		this.getModule_args().add("state="+state.toString());
	}
	
	public static enum FileCommandState{
		absent, directory, file, hard, link, touch
	}

}
