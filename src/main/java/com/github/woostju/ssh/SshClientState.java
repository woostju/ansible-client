package com.github.woostju.ssh;

/**
 * 
 * state of SshClient, See {@link SshClient#getState()} for more information
 * 
 * @author jameswu
 *
 */
public enum SshClientState {
	inited, 
	connected, 
	disconnected
}
