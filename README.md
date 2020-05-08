# ansible-client
A java implemented client connect to Ansible servers through ssh, and run Ansible command.

## usage

ansible-client is available from **Maven Central**

```xml
<dependency>
  <groupId>com.github.woostju</groupId>
  <artifactId>ansible-client</artifactId>
  <version>1.0.0-RELEASE</version>
</dependency>
```

### Who is this for?

Anyone who want to execute commands on your Ansible server in java code, instead of logging into server and execute manually.

With this, you can build an automation tool yourself working with Ansible in java.

### How do I use this?

Use it in your class:

```java

import com.github.woostju.ssh.SshClientConfig;
import com.github.woostju.ansible.AnsibleClient;
import com.github.woostju.ansible.ReturnValue;
import com.github.woostju.ansible.ReturnValue.Result;

//---------------------------------------

public void execute() {
	AnsibleClient client = new AnsibleClient(new SshClientConfig("hostIp", "sshPort", "username", "password", null));
	Map<String, ReturnValue> result =client.execute(new PingCommand(Lists.newArrayList(host_inner_ip)), 1000);
}
```

It is recommended to use AnsibleClient with [SshClientPool](https://github.com/woostju/ssh-client-pool), so that ansibleClient borrows sshClient from the pool to execute command, to avoid create ssh connection each time:

```java
import com.github.woostju.ssh.SshClientConfig;
import com.github.woostju.ansible.AnsibleClient;
import com.github.woostju.ansible.ReturnValue;
import com.github.woostju.ansible.ReturnValue.Result;
import com.github.woostju.ssh.pool.SshClientsPool;

//---------------------------------------

// inject the auto-configured one
@Autowired
SshClientsPool pool;

//---------------------------------------
public void execute() {
	AnsibleClient client = new AnsibleClient(new SshClientConfig("hostIp", "sshPort", "username", "password", null), pool);
	Map<String, ReturnValue> result =client.execute(new PingCommand(Lists.newArrayList(host_inner_ip)), 1000);
}

```

To get more usage, please refer to the unit test code and java docs.

### How does it work?

AnsibleClient connects to Ansible server with ssh, and sends an Ansible adhoc command to server, and parses the output into ReturnValue.

### How many Ansible module does ansible-client support?

Until this release, ansible-client supports modules below:

| module name | module class | description | official link |
| :----:| :----: | :----: | :----: |
| command |  CmdCommand | The command will be executed on hosts | [link](https://docs.ansible.com/ansible/latest/modules/command_module.html) |
| copy | CopyCommand | Copies a file from the local or remote machine to a location on the remote machine | [link](https://docs.ansible.com/ansible/latest/modules/copy_module.html) | 
| file | FileCommand | Manage files and file properties | [link](https://docs.ansible.com/ansible/latest/modules/file_module.html) | 
| git | GitCommand | Deploy software (or files) from git checkouts | [link](https://docs.ansible.com/ansible/latest/modules/git_module.html) | 
| ping | PingCommand | Try to connect to host, verify a usable python and return pong on success |[link](https://docs.ansible.com/ansible/latest/modules/ping_module.html) | 
| playbook | PlaybookCommand |  Run playbook with ansible-playbook executable | [link](https://docs.ansible.com/ansible/latest/user_guide/playbooks.html) |
| script | ScriptCommand | Runs a local script on a remote node after transferring it | [link](https://docs.ansible.com/ansible/latest/modules/script_module.html) | 

Since Ansible itself has dozens of modules, you can also define your custom command class to work with AnsibleClient.
  
```java
import java.util.List;

import com.github.woostju.ansible.Module;

/**
* Add or remove MSSQL databases from a remote host.
*/
public class Mssql_dbCommand extends Command{

	/**
	 * @param hosts target hosts
	 */
	public Mssql_dbCommand(List<String> hosts, String login_host, String login_password, int login_port, String login_user, String name, String target, String state) {
		this(hosts, Lists.newArrayList("login_host="+login_host,
		"login_password="+login_password,
		"login_port="+login_port,
		"login_user="+login_user,
		"name="+name,
		"target="+target,
		"state="+state), null);
	}
	
	public Mssql_dbCommand(List<String> hosts, List<String> moduleArgs, List<String> options) {
		super(hosts, Module.ping.toString(), moduleArgs, options);
	}
}

```
## License

This code is under the [Apache Licence v2](https://www.apache.org/licenses/LICENSE-2.0).


## Additional Resources

* [SshClientPool](https://github.com/woostju/ssh-client-pool) a java implementation of ssh clients object pool with sshj, apache common pool2, expectIt
