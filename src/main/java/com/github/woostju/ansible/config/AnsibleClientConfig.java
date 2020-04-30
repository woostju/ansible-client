package com.github.woostju.ansible.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class AnsibleClientConfig {
	private String executable_path;
	private String playbook_executable_path;
	private String ansible_inventory_executable_path;
	private String inventory_path;
}
