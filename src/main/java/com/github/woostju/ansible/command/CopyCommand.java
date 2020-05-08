package com.github.woostju.ansible.command;

import java.util.List;

import org.assertj.core.util.Lists;

import com.github.woostju.ansible.Module;

/**
 * 
 * Copies a file from the local or remote machine to a location on the remote machine.
 * <p>Get more information from <a href="https://docs.ansible.com/ansible/latest/modules/copy_module.html">copy module</a>.
 * @author jameswu
 *
 */
public class CopyCommand extends Command{

	public CopyCommand(List<String> hosts, List<String> module_args, List<String> options) {
		super(hosts, Module.copy.toString(),  module_args, options);
	}
	
	
	/**
	 * @param hosts the destination
	 * @param srcPath  Path on server to a file to copy to the remote server.
	 * @param destPath Remote absolute path where the file should be copied to.
	 * @param force Influence whether the remote file must always be replaced.
	 * @param owner Name of the user that should own the file/directory, as would be fed to chown.
	 * @param mode The permissions of the destination file or directory.
	 */
	public CopyCommand(List<String> hosts, String srcPath, String destPath, Boolean force, String owner, String mode) {
		this(hosts, Lists.newArrayList(), null);
		this.getModuleArgs().add("src="+srcPath);
		this.getModuleArgs().add("dest="+destPath);
		if (mode!=null) {
			this.getModuleArgs().add("mode="+mode);
		}
		if (owner!=null) {
			this.getModuleArgs().add("owner="+owner);
		}
		if (force!=null) {
			this.getModuleArgs().add("force="+ (force?"yes":"no"));
		}
	}
}


