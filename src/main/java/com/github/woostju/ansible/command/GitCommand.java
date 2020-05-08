package com.github.woostju.ansible.command;

import java.util.List;

import org.assertj.core.util.Lists;

import com.github.woostju.ansible.Module;

/**
 * Deploy software (or files) from git checkouts
 * <p>Get more information from <a href="https://docs.ansible.com/ansible/latest/modules/git_module.html">git module</a>.
 * @author jameswu
 *
 */
public class GitCommand extends Command{
	
	public GitCommand(List<String> hosts,  List<String> module_args, List<String> options) {
		super(hosts, Module.git.toString(), module_args, options);
	}
	
	/**
	 * 
	 * @param hosts target hosts
	 * @param gitRepo git, SSH, or HTTP(S) protocol address of the git repository.
	 * @param destDirectory The path of where the repository should be checked out. 
	 * @param gitSshKeyFilePath Key file of ssh repository
	 */
	public GitCommand(List<String> hosts, String gitRepo, String destDirectory, String gitSshKeyFilePath) {
		this(hosts, Lists.newArrayList(), null);
		this.getModuleArgs().add("force=yes");
		if (gitSshKeyFilePath!=null) {
			this.getModuleArgs().add("key_file="+gitSshKeyFilePath);
		}
		if(gitRepo!=null) {
			this.getModuleArgs().add("repo="+gitRepo);
		}
		if (destDirectory!=null) {
			this.getModuleArgs().add("dest="+destDirectory);
		}
	}
}
