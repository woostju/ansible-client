package com.github.woostju.ansible.command;

import java.util.List;

import org.assertj.core.util.Lists;
import com.github.woostju.ansible.Module;

/**
 * 
 * The command will be executed on hosts.
 * <p>Get more information from <a href="https://docs.ansible.com/ansible/latest/modules/command_module.html">command module</a> 
 * @author jameswu
 *
 */
public class CmdCommand extends Command{

	
	public CmdCommand(List<String> hosts, List<String> moduleArgs,List<String> options) {
		super(hosts,  Module.command.toString(), moduleArgs, options);
	}
	
	/**
	 * Execute commands on targets
	 * @param hosts target hosts
	 * @param command The command to run.
	 */
	public CmdCommand(List<String> hosts, String command) {
		this(hosts, Lists.newArrayList(command), null);
	}
}
