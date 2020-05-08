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

### How many module does ansible-client support?

Until this release, ansible-client support module below:


| 左对齐 | 右对齐 | 居中对齐 |
| :-----| ----: | :----: |
| 单元格 | 单元格 | 单元格 |
| 单元格 | 单元格 | 单元格 |

## License

This code is under the [Apache Licence v2](https://www.apache.org/licenses/LICENSE-2.0).


## Additional Resources

* [SshClientPool](https://github.com/woostju/ssh-client-pool) a java implementation of ssh clients object pool with sshj, apache common pool2, expectIt
