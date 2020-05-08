package com.github.woostju.ansible;

/**
 * 
 * ansible module type
 * 
 * @author jameswu
 */
public enum Module {
	script, 
	command, 
	ping, 
	copy, 
	playbook, 
	git, 
	ansible_inventory, 
	file, 
	get_url, 
	setup
}
