package com.github.woostju.ssh.exception;

/**
 * The ssh connection is disconnected
 * @author jameswu
 *
 */
public class LostConnectionException extends SshException{

	
	private static final long serialVersionUID = -3961870786667342727L;

	public LostConnectionException(String message) {
		this(message, null);
	}
	
	public LostConnectionException(String message, Throwable error) {
		super(message, error);
	}
}
