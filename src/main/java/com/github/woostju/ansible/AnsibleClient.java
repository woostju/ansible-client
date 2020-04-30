package com.github.woostju.ansible;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.woostju.ansible.command.AdhocCommand;
import com.github.woostju.ansible.command.PingModuleCommand;
import com.github.woostju.ssh.SshClientConfig;
import com.github.woostju.ssh.SshResponse;
import com.github.woostju.ssh.pool.SshClientWrapper;
import com.github.woostju.ssh.pool.SshClientsPool;

/**
 * 
 * @author jameswu
 * 
 * Ansible client 
 * sample:
 * AnsibleClient client = 
 * 	AnsibleClient.newInstance("192.168.0.1", 22).configAuth("root", "paass", "/").setInventoryPath("/etc/anchora/cmp/ansible/inventory");
 *
 */

public class AnsibleClient {
	
	private final static Logger logger = LoggerFactory.getLogger(AnsibleClient.class);
	
	private static SshClientsPool sshClientsPool = new SshClientsPool();
	// forbidden creating object outside
	static {
		
	}
	
	private SshClientsPool getSshClientsPool() {
		return sshClientsPool;
	}
	
	private AnsibleClient(){
		
	}
	
	public AnsibleClient(SshClientConfig config){
		this.hostSshConfig = config;
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
	public Map<String, ReturnValue> execute(AdhocCommand command, int timeoutInSeconds){
		boolean local = false;
		List<String> commands = new ArrayList<>();
		commands.add(this.ansibleRootPath + command.getExecutable());
		if(this.getInventoryPath()!=null){
			commands.add("-i");
			commands.add(this.getInventoryPath());
		}
		commands.add(command.toString());
		Exception exception = null;
		Map<String, ReturnValue> responses = new HashMap<>();
		if(local){
//			SystemCommandExecutor executor = SystemCommandExecutor.newExecutor().executeCommand(commands, timeoutInSeconds, line->{
//				output.addAll(line);
//			});
//			if(executor.getResult() == RemoteResult.SUCCESS){
//				responses = this.outputParser(module).parse(output);
//			}
//			errorMessage = executor.getErrorMessage();
		}else{
			String commandStr = commands.stream().collect(Collectors.joining(" "));
			try {
				SshClientWrapper client = getSshClientsPool().client(this.hostSshConfig);
				SshResponse sshResponse = client.executeCommand(commandStr, timeoutInSeconds);
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
	
	public static void main(String[] args) {
		SshClientConfig config = new SshClientConfig("54.223.170.242", 22, "centos", null, "/Users/jameswu/Documents/workspace/ssh-client-pool/src/test/java/id_rsa");
		Map<String, ReturnValue> response = new AnsibleClient(config).execute(new PingModuleCommand(Lists.newArrayList("127.0.0.1"), null), 100);
		System.out.println(response);
	}
	
}
