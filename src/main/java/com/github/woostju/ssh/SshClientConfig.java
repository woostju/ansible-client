package com.github.woostju.ssh;

/**
 * 
 * Configuration used by {@link SshClient} to connect to remote server instance
 * 
 * @author jameswu
 * 
 */
public class SshClientConfig {
	private String host;
	private int port;
	private String username;
	private String password;
	private String privateKeyPath;
	private String id;

	/**
	 * 
	 * @return host address
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host host address, usually the ip address of remote server
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * 
	 * @return ssh port of the remote server
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port ssh port of the remote server
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * 
	 * @return ssh username of the remote server
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * 
	 * @param username ssh username of the remote server
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * 
	 * @return ssh password of the remote server
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * 
	 * @param password ssh password of the remote server
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return ssh local key file path of the remote server
	 */
	public String getPrivateKeyPath() {
		return privateKeyPath;
	}

	/**
	 * @param privateKeyPath local key file path of the remote server
	 */
	public void setPrivateKeyPath(String privateKeyPath) {
		this.privateKeyPath = privateKeyPath;
	}

	/**
	 * 
	 * @return id of the config
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param host           server host address
	 * @param port           server ssh port
	 * @param username       server ssh username
	 * @param password       server ssh password
	 * @param privateKeyPath local security key used to connect to server
	 */
	public SshClientConfig(String host, int port, String username, String password, String privateKeyPath) {
		this.id = host + port + username;
		if (null != password && password.length() > 0) {
			this.id += password;
		}
		if (privateKeyPath != null) {
			this.id += privateKeyPath;
		}
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.privateKeyPath = privateKeyPath;
	}

	public SshClientConfig() {

	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SshClientConfig) {
			return id.equals(((SshClientConfig) obj).getId());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return this.id;
	}
}