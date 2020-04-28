package com.github.woostju.ssh;

import com.github.woostju.ssh.config.SshClientPoolConfig;

/**
 * 
 * Factory of {@link SshClient} implementation
 * <p> Create a new instance of {@link SshClientSSHJ} with {@link #newInstance(SshClientConfig)}
 * <p> Create a custom implementation of {@link SshClient} with {@link #newInstance(SshClientConfig, SshClientPoolConfig)}
 * 
 * @author jameswu
 *
 */
public class SshClientFactory {
	
	/**
	 * Create a new instance of {@link SshClientSSHJ}
	 * @param config ssh connection configuration of the remote server 
	 * @return SshClient in inited state
	 */
	public static SshClient newInstance(SshClientConfig config){
		return newInstance(config, new SshClientPoolConfig());
	} 
	
	/**
	 * Create a custom implementation of {@link SshClient}
	 * @param config ssh connection configuration of the remote server 
	 * @param poolConfig customized configuration
	 * @return SshClient in inited state
	 * @throws RuntimeException if SshClientImplClass in {@code poolConfig} is invalid
	 */
	public static SshClient newInstance(SshClientConfig config, SshClientPoolConfig poolConfig){
		if (poolConfig.getSshClientImplClass()==null) {
			poolConfig.setSshClientImplClass(SshClientSSHJ.class);
		}
		try {
			SshClient client = (SshClient)poolConfig.getSshClientImplClass().newInstance();
			client.init(config);
			if(client instanceof SshClientSSHJ && poolConfig.getServerCommandPromotRegex()!=null) {
				((SshClientSSHJ)client).setCommandPromotRegexStr(poolConfig.getServerCommandPromotRegex());
			}
			return client;
		} catch (InstantiationException e) {
			throw new RuntimeException("new instance failed", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("new instance failed", e);
		}
	} 
	
}
