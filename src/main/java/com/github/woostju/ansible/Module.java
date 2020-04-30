package com.github.woostju.ansible;

/**
 * 
 * @author jameswu
 *
 *	ansible module类型
 */
public enum Module {
	script, command, ping, copy, playbook, git, ansible_inventory, file, get_url, setup
}
