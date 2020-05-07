package com.github.woostju.ansible;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.woostju.ansible.command.Command;
import com.github.woostju.ansible.util.SystemCommandExecutor;
import com.github.woostju.ssh.SshClient;
import com.github.woostju.ssh.SshClientConfig;
import com.github.woostju.ssh.SshClientFactory;
import com.github.woostju.ssh.SshResponse;
import com.github.woostju.ssh.exception.SshException;
import com.github.woostju.ssh.pool.SshClientPoolConfig;
import com.github.woostju.ssh.pool.SshClientWrapper;
import com.github.woostju.ssh.pool.SshClientsPool;

/**
 * 
 * @author jameswu
 * 
 * 
 */

public class AnsibleClient {
	
	private final static Logger logger = LoggerFactory.getLogger(AnsibleClient.class);
	
	private SshClientsPool sshClientsPool;
	
	/**
	 * ansible client to local machine
	 */
	public AnsibleClient(){
		
	}
	
	/**
	 * ansible client to remote machine
	 * @param config ssh config of remote machine
	 */
	public AnsibleClient(SshClientConfig config){
		this.hostSshConfig = config;
	}
	
	/**
	 * ansible client to remote machine
	 * @param config ssh config of remote machine
	 */
	public AnsibleClient(SshClientConfig config, SshClientsPool sshClientsPool){
		this.hostSshConfig = config;
		this.sshClientsPool = sshClientsPool;
	}
	
	private SshClientConfig hostSshConfig;
	
	private String ansibleRootPath = "/usr/bin/";
	
	private String inventoryPath = "/etc/ansible/hosts";
	
	
	/**
	 * set custom inventory for ansible
	 * @param inventoryPath
	 * @return
	 */
	public AnsibleClient setInventoryPath(String inventoryPath){
		this.inventoryPath = inventoryPath;
		return this;
	}
	
	
	public AnsibleClient setAnsibleRootPath(String ansibleRootPath) {
		this.ansibleRootPath = ansibleRootPath;
		return this;
	}
	
	
	public String getAnsibleRootPath() {
		return ansibleRootPath;
	}

	public String getInventoryPath() {
		return inventoryPath;
	}
	
	public SshClientConfig getHostSshConfig() {
		return hostSshConfig;
	}

	/**
	 * 
	 * @param ips
	 * @param timeoutInSeconds
	 * @param module
	 * @param a_params
	 * @return
	 * 
	 * run ansible modules
	 */
	public Map<String, ReturnValue> execute(Command command, int timeoutInSeconds){
		List<String> commands = command.createAnsibleCommands(this, command);
		logger.info("execute commands "+ commands+" in "+timeoutInSeconds);
		Exception exception = null;
		Map<String, ReturnValue> responses = new HashMap<>();
		if (this.hostSshConfig==null) {
			try {
				List<String> stdout = SystemCommandExecutor.newExecutor().executeCommand(commands, timeoutInSeconds);
				responses = command.parseCommandReturnValues(stdout);
			} catch (Exception e) {
				exception = e;
			}
			
		}else{
			String commandStr = commands.stream().collect(Collectors.joining(" "));
			try {
				SshResponse sshResponse;
				if (this.sshClientsPool==null) {
					SshClientWrapper wrapper = new SshClientWrapper(this.hostSshConfig, new SshClientPoolConfig());
					try {
						wrapper.connect(timeoutInSeconds).auth().startSession();
						sshResponse = wrapper.executeCommand(commandStr, timeoutInSeconds);
					} catch (SshException e) {
						throw new RuntimeException("create ssh client fail");
					}finally {
						wrapper.disconnect();
					}
				}else {
					SshClientWrapper client = this.sshClientsPool.client(this.hostSshConfig);
					sshResponse = client.executeCommand(commandStr, timeoutInSeconds);
				}
				if(sshResponse.getCode()==0) {
					// use module output parser to parse the console log
					try{
						responses = command.parseCommandReturnValues(sshResponse.getStdout());
					}catch(Exception e){
						logger.error("parse ansible output fail", e);
					}
				}
				exception = sshResponse.getException();
			}catch(Exception e) {
				logger.error("exec command fail.",e);
				exception = e;
			}
		}
		for(String ip:command.getHosts()){
			if(!responses.containsKey(ip)){
				ReturnValue response = new ReturnValue();
				response.setResult(ReturnValue.Result.failed);
				if(exception!=null) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					exception.printStackTrace(pw);
					response.setStdout(Lists.newArrayList(sw.toString()));
				}
			}
		}
		return responses;
	}
	
}
