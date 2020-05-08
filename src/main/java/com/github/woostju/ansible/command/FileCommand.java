package com.github.woostju.ansible.command;

import java.util.List;

import org.assertj.core.util.Lists;

import com.github.woostju.ansible.Module;

/**
 * Manage files and file properties
 * <p>Get more information from <a href="https://docs.ansible.com/ansible/latest/modules/file_module.html">file module</a>.
 * @author jameswu
 *
 */
public class FileCommand extends Command{
	
	public FileCommand(List<String> hosts, List<String> module_args, List<String> options) {
		super(hosts, Module.file.toString(), module_args, options);
	}
	
	/**
	 * 
	 * @param hosts target hosts
	 * @param path Path to the file being managed.
	 * @param state Choices: absent directory file hard link touch
	 */
	public FileCommand(List<String> hosts, String path, FileCommandState state) {
		this(hosts, Lists.newArrayList(), null);
		this.getModuleArgs().add("path="+path);
		this.getModuleArgs().add("state="+state.toString());
	}
	
	public static enum FileCommandState{
		absent, 
		directory, 
		file, 
		hard, 
		link, 
		touch
	}

}
