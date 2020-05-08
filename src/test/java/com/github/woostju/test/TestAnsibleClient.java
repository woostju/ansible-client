package com.github.woostju.test;

import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.util.Map;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import com.github.woostju.ansible.AnsibleClient;
import com.github.woostju.ansible.ReturnValue;
import com.github.woostju.ansible.ReturnValue.Result;
import com.github.woostju.ansible.command.CmdCommand;
import com.github.woostju.ansible.command.CopyCommand;
import com.github.woostju.ansible.command.FileCommand;
import com.github.woostju.ansible.command.PingCommand;
import com.github.woostju.ansible.command.PlaybookCommand;
import com.github.woostju.ansible.command.ScriptCommand;
import com.github.woostju.ansible.command.FileCommand.FileCommandState;
import com.github.woostju.ansible.command.GitCommand;
import com.github.woostju.ssh.SshClientConfig;
import com.github.woostju.ssh.pool.SshClientsPool;

public class TestAnsibleClient {
	
	static SshClientsPool pool = new SshClientsPool();
	
	private SshClientConfig clientConfig;
	
	private String host = "52.80.146.61";
	
	private String host_inner_ip = "172.31.31.82";
	
	@Before
	public void init() throws IOException {
		String path = this.getClass().getClassLoader().getResource("id_rsa").getPath();
		clientConfig = new SshClientConfig(host, 22, "centos", null, path);
	}
	
	private AnsibleClient ansibleClient() {
		AnsibleClient client = new AnsibleClient(clientConfig, pool);
		client.setInventoryPath("/opt/anchora/cmp/ansible/inventory");
		return client;
	}
	
	private AnsibleClient localAnsibleClient() {
		AnsibleClient client = new AnsibleClient();
		client.setAnsibleRootPath("/usr/local/bin/");
		return client;
	}
	
	
	@Test
	public void testPingSuccess(){
		Map<String, ReturnValue> result = ansibleClient().execute(new PingCommand(Lists.newArrayList(host_inner_ip)), 1000);
		
		assertArrayEquals(new Object[]{
				true,true
		}, new Object[]{
				result.get(host_inner_ip).isSuccess(),
				result.get(host_inner_ip).getValue("ping").equals("pong")
		});
	}
	
	@Test
	public void testPingdLocalSuccess() {
		Map<String, ReturnValue> result = localAnsibleClient().execute(new PingCommand(Lists.newArrayList("127.0.0.1")), 100);
		assertArrayEquals(new Object[]{
				true, true
		}, new Object[]{
				result.get("127.0.0.1").isSuccess(),
				result.get("127.0.0.1").getValue("ping").equals("pong")
		});
	}
	
	@Test
	public void testUnmanagedNode(){
		Map<String, ReturnValue> result = ansibleClient().execute(new PingCommand(Lists.newArrayList(host_inner_ip+1)), 1000);
		
		assertArrayEquals(new Object[]{
				ReturnValue.Result.unmanaged
		}, new Object[]{
				result.get(host_inner_ip+1).getResult()
		});
	}
	
	@Test
	public void testCommandSuccess() {
		Map<String, ReturnValue> result = ansibleClient().execute(new CmdCommand(Lists.newArrayList(host_inner_ip), "echo '123'"), 100);
		assertArrayEquals(new Object[]{
				true, 0, 1l
		}, new Object[]{
				result.get(host_inner_ip).isSuccess(),
				result.get(host_inner_ip).getRc(),
				result.get(host_inner_ip).getStdout().stream().filter(item->item.equals("123")).count()
		});
	}
	
	@Test
	public void testCommandLocalSuccess() {
		Map<String, ReturnValue> result = localAnsibleClient().execute(new CmdCommand(Lists.newArrayList("127.0.0.1"), "echo '123'"), 100);
		assertArrayEquals(new Object[]{
				true, 0, 1l
		}, new Object[]{
				result.get("127.0.0.1").isSuccess(),
				result.get("127.0.0.1").getRc(),
				result.get("127.0.0.1").getStdout().stream().filter(item->item.equals("123")).count()
		});
	}
	
	@Test
	public void testCommandFailed() {
		Map<String, ReturnValue> result = ansibleClient().execute(new CmdCommand(Lists.newArrayList(host_inner_ip), "echo1 '123'"), 100);
		assertArrayEquals(new Object[]{
				false, 2, 0l
		}, new Object[]{
				result.get(host_inner_ip).isSuccess(),
				result.get(host_inner_ip).getRc(),
				result.get(host_inner_ip).getStdout().stream().filter(item->item.equals("123")).count()
		});
	}
	
	@Test
	public void testFileAndCopySuccess() {
		Map<String, ReturnValue> result = ansibleClient().execute(new FileCommand(Lists.newArrayList(host_inner_ip), "/tmp/a", FileCommandState.touch), 100);
		
		result = ansibleClient().execute(new CopyCommand(Lists.newArrayList(host_inner_ip), "/tmp/a", "/tmp/b", true, null, null), 100);
		assertArrayEquals(new Object[]{
				true,
		}, new Object[]{
				result.get(host_inner_ip).isSuccess(),
		});
		
		result = ansibleClient().execute(new FileCommand(Lists.newArrayList(host_inner_ip), "/tmp/a", FileCommandState.absent), 100);
		result = ansibleClient().execute(new FileCommand(Lists.newArrayList(host_inner_ip), "/tmp/b", FileCommandState.absent), 100);
	}
	
	//
	@Test
	public void testGitSuccess() {
		Map<String, ReturnValue> result = ansibleClient().execute(new GitCommand(Lists.newArrayList(host_inner_ip), "https://github.com/woostju/ssh-client-pool.git", "/tmp/ssh-client-pool", null), 1000);
		assertArrayEquals(new Object[]{
				true,
		}, new Object[]{
				result.get(host_inner_ip).isSuccess(),
		});
		
		result = ansibleClient().execute(new FileCommand(Lists.newArrayList(host_inner_ip), "/tmp/ssh-client-pool/LICENSE", FileCommandState.file), 100);
		assertArrayEquals(new Object[]{
				true,
		}, new Object[]{
				result.get(host_inner_ip).isSuccess(),
		});
	}
	
	@Test
	public void testGitFailed() {
		Map<String, ReturnValue> result = ansibleClient().execute(new GitCommand(Lists.newArrayList(host_inner_ip), "git@github.com:woostju/ssh-client-pool.git", "/tmp/ssh-client-pool", null), 1000);
		assertArrayEquals(new Object[]{
				false,
		}, new Object[]{
				result.get(host_inner_ip).isSuccess(),
		});
	}
	
	@Test
	public void testScriptSuccess() {
		String path = this.getClass().getClassLoader().getResource("script.sh").getPath();
		localAnsibleClient().execute(new CopyCommand(Lists.newArrayList(clientConfig.getHost()), path, "/tmp/script.sh", true, null, null), 100);
		
		Map<String, ReturnValue> result = ansibleClient().execute(new ScriptCommand(Lists.newArrayList(host_inner_ip), "/tmp/script.sh", "/bin/sh"), 100);
		assertArrayEquals(new Object[]{
				true,
		}, new Object[]{
				result.get(host_inner_ip).isSuccess(),
		});
		result = ansibleClient().execute(new FileCommand(Lists.newArrayList(host_inner_ip), "/tmp/script.sh", FileCommandState.absent), 100);
	}
	
	@Test
	public void testScriptFailed() {
		Map<String, ReturnValue> result = ansibleClient().execute(new ScriptCommand(Lists.newArrayList(host_inner_ip), "/tmp/script2.sh", "/bin/sh"), 100);
		assertArrayEquals(new Object[]{
				false,
		}, new Object[]{
				result.get(host_inner_ip).isSuccess(),
		});
	}

	@Test
	public void testPlaybookSuccess() {
		String path = this.getClass().getClassLoader().getResource("playbook-unittest-success.yaml").getPath();
		localAnsibleClient().execute(new CopyCommand(Lists.newArrayList(clientConfig.getHost()), path, "/tmp/playbook-unittest-success.yaml", true, null, null), 100);
		
		Map<String, ReturnValue> result = ansibleClient().execute(new PlaybookCommand(Lists.newArrayList(host_inner_ip,"172.31.31.81"), "/tmp/playbook-unittest-success.yaml", null), 100);
		assertArrayEquals(new Object[]{
				true, Result.unmanaged
		}, new Object[]{
				result.get(host_inner_ip).isSuccess(),
				result.get("172.31.31.81").getResult()
		});
	}
	
}
