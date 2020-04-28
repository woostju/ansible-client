package com.github.woostju.ssh;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Response return from {@link SshClient#executeCommand(String, int)}
 * 
 * @author jameswu
 *
 */
public class SshResponse {
	
	private int code;
	
	private Exception exception;
	
	private List<String> stdout = new ArrayList<String>();
	
	/**
	 * @return 0
	 */
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	/**
	 * 
	 * @return the exception in {@link SshClient#executeCommand(String, int)}
	 */
	public Exception getException() {
		return exception;
	}
	
	public void setException(Exception exception) {
		this.exception = exception;
	}
	
	/**
	 * 
	 * @return the output from remote server after send command
	 */
	public List<String> getStdout() {
		return stdout;
	}
	
	public void setStdout(List<String> stdout) {
		this.stdout = stdout;
	}
	
	
	
}
