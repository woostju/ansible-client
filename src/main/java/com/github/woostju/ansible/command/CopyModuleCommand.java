package com.github.woostju.ansible.command;

import java.util.List;

import org.assertj.core.util.Lists;

import com.github.woostju.ansible.Module;

public class CopyModuleCommand extends AdhocCommand{

	public CopyModuleCommand(List<String> hosts, String srcPath, String destPath, Boolean force, String owner, String mode, List<String> options) {
		super(hosts, Module.copy, 
				Lists.newArrayList("src="+srcPath, 
						"dest="+destPath,
						mode==null?"":("force="+ (force?"yes":"no")),
						mode==null?"":("mode="+mode),
						owner==null?"":("owner="+owner)), 
				options);
	}
}


