package com.github.woostju.ansible;

public class AnsibleInventoryHost {
	
	private String host_name;
	
	private String ansible_port;
	
	private String ansible_user;
	
	private String ansible_ssh_private_key_file;
	
	private String ansible_ssh_pass;
	
	private String ansible_become_password;
	
	private String ansible_become;

	public String getHost_name() {
		return host_name;
	}

	public void setHost_name(String host_name) {
		this.host_name = host_name;
	}

	public String getAnsible_port() {
		return ansible_port;
	}

	public void setAnsible_port(String ansible_port) {
		this.ansible_port = ansible_port;
	}

	public String getAnsible_user() {
		return ansible_user;
	}

	public void setAnsible_user(String ansible_user) {
		this.ansible_user = ansible_user;
	}

	public String getAnsible_ssh_private_key_file() {
		return ansible_ssh_private_key_file;
	}

	public void setAnsible_ssh_private_key_file(String ansible_ssh_private_key_file) {
		this.ansible_ssh_private_key_file = ansible_ssh_private_key_file;
	}

	public String getAnsible_ssh_pass() {
		return ansible_ssh_pass;
	}

	public void setAnsible_ssh_pass(String ansible_ssh_pass) {
		this.ansible_ssh_pass = ansible_ssh_pass;
	}

	public String getAnsible_become_password() {
		return ansible_become_password;
	}

	public void setAnsible_become_password(String ansible_become_password) {
		this.ansible_become_password = ansible_become_password;
	}

	public String getAnsible_become() {
		return ansible_become;
	}

	public void setAnsible_become(String ansible_become) {
		this.ansible_become = ansible_become;
	}

}
