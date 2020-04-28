package com.github.woostju.ssh.exception;

public class SshException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2052615275027564490L;
	
	public SshException(String message, Throwable error) {
		super(message);
		if(error != null) {
			initCause(error);
		}
	}
	
	public SshException(String message) {
		this(message, null);
	}
	
}
