package com.github.woostju.ssh.exception;


/**
 * Timeout Exception
 * @author jameswu
 *
 */
public class TimeoutException extends SshException{

	public TimeoutException(String message) {
		this(message, null);
	}
	
	public TimeoutException(String message, Throwable error) {
		super(message, error);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -39618386667342727L;

}
