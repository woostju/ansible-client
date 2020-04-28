package com.github.woostju.ssh.exception;

/**
 * Ssh auth failed
 * @author jameswu
 *
 */
public class AuthException extends SshException{

	public AuthException(String message) {
		this(message, null);
	}
	
	public AuthException(String message, Throwable error) {
		super(message, error);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3961786667342327L;

}
