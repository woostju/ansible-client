package com.github.woostju.ansible.command;

import java.util.List;

import org.assertj.core.util.Lists;

import com.github.woostju.ansible.Module;

/**
 * 
 * Runs a local script on a remote node after transferring it
 * <p>Get more information from <a href="https://docs.ansible.com/ansible/latest/modules/script_module.html">script module</a>.
 * @author jameswu
 *
 */
public class ScriptCommand extends Command{

	
	public ScriptCommand(List<String> hosts, List<String> module_args, List<String> options) {
		super(hosts, Module.script.toString(), module_args, options);
	}
	
	/**
	 * 
	 * @param hosts target hosts
	 * @param scriptFilePath Path to the script on server to run followed by optional arguments.
	 * @param executablePath Name or path of a executable to invoke the script with.
	 */
	public ScriptCommand(List<String> hosts, String scriptFilePath, String executablePath) {
		this(hosts, Lists.newArrayList(scriptFilePath, "executable="+executablePath), null);
	}

}
