package com.github.test;

import com.github.woostju.ssh.SshClientSSHJ;
import com.github.woostju.ssh.SshResponse;

public class CustomerSshClient extends SshClientSSHJ{
	
	@Override
	public SshResponse executeCommand(String command, int timeoutInSeconds) {
		SshResponse repsonse = super.executeCommand(command, timeoutInSeconds);
		repsonse.getStdout().add("success");
		return repsonse;
	}
}