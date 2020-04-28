package com.github.test;

import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.github.woostju.ssh.SshClientConfig;
import com.github.woostju.ssh.SshClientFactory;
import com.github.woostju.ssh.SshResponse;
import com.github.woostju.ssh.exception.AuthException;
import com.github.woostju.ssh.exception.SshException;
import com.github.woostju.ssh.exception.TimeoutException;


public class TestSshClient {
	
	private SshClientConfig clientConfig;
	
	private String host = "52.80.212.95";
	
	@Before
	public void init() throws IOException {
		String path = this.getClass().getClassLoader().getResource("id_rsa").getPath();
		clientConfig = new SshClientConfig(host, 22, "centos", null, path);
	}
	
	@Test
	public void testSuccess() throws SshException{
		SshResponse response = SshClientFactory.newInstance(clientConfig).connect(100)
			.authPublickey().startSession(false)
			.executeCommand("echo 'yes'", 100);
		
		assertArrayEquals(new Object[]{
				0,true
		}, new Object[]{
				response.getCode(),
				response.getStdout().stream().collect(Collectors.joining("")).contains("yes")
		});
	}
	
	@Test(expected = SshException.class)
	public void testConnectToUnreachableHost() throws SshException{
		clientConfig.setHost(clientConfig.getHost()+"1");
		SshClientFactory.newInstance(clientConfig).connect(100);
	}
	
	@Test(expected = SshException.class)
	public void testConnectWithWrongPort() throws SshException{
		clientConfig.setPort(33);;
		SshClientFactory.newInstance(clientConfig).connect(100);
	}
	
	@Test(expected = AuthException.class)
	public void testAuthWithWrongUser() throws SshException{
		clientConfig.setUsername(clientConfig.getUsername()+"1");
		SshClientFactory.newInstance(clientConfig).connect(100)
			.authPublickey();
	}
	
	@Test
	public void testExecuteCommandTimeout() throws SshException{
		SshResponse response = SshClientFactory.newInstance(clientConfig).connect(100)
				.authPublickey().startSession(false)
				.executeCommand("echo 'timeout' >> /home/b.txt; sleep 100s;", 60);
		assertArrayEquals(new Object[]{
				true
		}, new Object[]{
				response.getException() instanceof TimeoutException
		});
	}
	
	@Test
	public void testExecuteWrongCommand() throws SshException{
		SshResponse response = SshClientFactory.newInstance(clientConfig).connect(100)
				.authPublickey().startSession(false)
				.executeCommand("sleep 20s echo 'timeout'",100);
		assertArrayEquals(new Object[]{
				1
		}, new Object[]{
				response.getCode()
		});
	}
	
	@Test
	public void testSuccessWithShellmode() throws SshException{
		SshResponse response = SshClientFactory.newInstance(clientConfig).connect(100)
			.authPublickey().startSession(true)
			.executeCommand("echo 'yes'", 100);
		
		assertArrayEquals(new Object[]{
				0,true
		}, new Object[]{
				response.getCode(),
				response.getStdout().stream().collect(Collectors.joining("")).contains("yes")
		});
	}
	
	@Test
	public void testExecuteCommandTimeoutWithShellmode() throws SshException{
		SshResponse response = SshClientFactory.newInstance(clientConfig).connect(100)
				.authPublickey().startSession(true)
				.executeCommand("echo 'timeout' >> /home/b.txt; sleep 100s;", 60);
		assertArrayEquals(new Object[]{
				true
		}, new Object[]{
				response.getException() instanceof TimeoutException
		});
	}
	
	@Test
	public void testExecuteWrongCommandWithShellmode() throws SshException{
		SshResponse response = SshClientFactory.newInstance(clientConfig).connect(100)
				.authPublickey().startSession(true)
				.executeCommand("sleep 20s echo 'timeout'",100);
		assertArrayEquals(new Object[]{
				0
		}, new Object[]{
				response.getCode()
		});
	}
	
}
