package com.github.woostju.ssh.pool;

import java.util.List;
import java.util.Map;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObjectInfo;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.woostju.ssh.SshClientConfig;
import com.github.woostju.ssh.SshClientEventListener;
import com.github.woostju.ssh.SshClientState;
import com.github.woostju.ssh.config.SshClientPoolConfig;
import com.github.woostju.ssh.exception.SshException;

/**
 * 
 * SshClient objects pool
 * <p>Cache shell mode connected SshClient in the pools, to save connect time, also improve performance
 * 
 * @author jameswu
 */

public class SshClientsPool extends GenericKeyedObjectPool<SshClientConfig, SshClientWrapper>{
	private final static Logger logger = LoggerFactory.getLogger(SshClientsPool.class);
	
	static long recycle_window = 120;
	
	static int core_pool_size = 20;
	
	static int client_connect_timeout = 60;
	
	static long request_wait_timeout = 120;
	
	private SshClientPoolConfig poolConfig;
	
	/**
	 * maxTotal 20, maxIdle 20, idleTime 120 seconds, maxWaitMillis 120 seconds
	 */
	public SshClientsPool() {
		this(core_pool_size, core_pool_size, recycle_window, request_wait_timeout);
	}
	
	/**
	 * 
	 * @param poolConfig create SshClientsPool with {@code poolConfig}
	 */
	public SshClientsPool(SshClientPoolConfig poolConfig) {
		super(new SshClientsObjectFactory(), poolConfig);
		((SshClientsObjectFactory)this.getFactory()).setSshClientsPool(this);
		this.poolConfig = poolConfig;
	}
	
	/**
	 * 
	 * @param maxTotal max clients in pool
	 * @param maxIdle max idle clients in pool
	 * @param idleTime idle time clients live in the pool
	 * @param maxWaitTime wait time when request block
	 */
	public SshClientsPool(int maxTotal, int maxIdle, long idleTime, long maxWaitTime) {
		this(new SshClientPoolConfig(maxTotal, maxIdle, idleTime, maxWaitTime));
	}
	
	/**
	 * request a connected client from pool, may be a cached one, maybe a brand-new one  
	 * @param config the connection information to host
	 * @return SshClientWrapper
	 */
	public SshClientWrapper client(SshClientConfig config) {
		try {
			return this.borrowObject(config);
		} catch (Exception e) {
			logger.error("create ssh client error", e);
			throw new RuntimeException(e);
		}
	}
	
	public SshClientPoolConfig getPoolConfig() {
		return poolConfig;
	}

	/**
	 * query objects with same server connection information
	 * @param config server connection information
	 * @return lists of DefaultPooledObjectInfo with SshClientWrapper inside
	 */
	public List<DefaultPooledObjectInfo> getObjects(SshClientConfig config) {
		Map<String, List<DefaultPooledObjectInfo>> objects = listAllObjects();
		return objects.get(config.toString());
	}
	
}

class SshClientsObjectFactory extends BaseKeyedPooledObjectFactory<SshClientConfig, SshClientWrapper> implements SshClientEventListener{
	private final static Logger logger = LoggerFactory.getLogger(SshClientsObjectFactory.class);
	
	private SshClientsPool pool;
	
	public void setSshClientsPool(SshClientsPool pool) {
		this.pool = pool;
	}
	
	@Override
	public boolean validateObject(SshClientConfig key, PooledObject<SshClientWrapper> p) {
		return p.getObject().getState() == SshClientState.connected;
	}

	@Override
	public void destroyObject(SshClientConfig key, PooledObject<SshClientWrapper> p) throws Exception {
		logger.debug("销毁对象 "+p);
		p.getObject().setEventListener(null);
		p.getObject().disconnect();
	}
	
	@Override
	public void activateObject(SshClientConfig key, PooledObject<SshClientWrapper> p) throws Exception {
		super.activateObject(key, p);
	}
	
	@Override
	public PooledObject<SshClientWrapper> wrap(SshClientWrapper value) {
		return new DefaultPooledObject<SshClientWrapper>(value);
	}
	
	@Override
	public SshClientWrapper create(SshClientConfig config) {
		SshClientWrapper wrapper = new SshClientWrapper(config, pool.getPoolConfig());
		try {
			wrapper.setEventListener(this).connect(SshClientsPool.client_connect_timeout).auth().startSession();
		} catch (SshException e) {
			throw new RuntimeException("create ssh client fail");
		}
		return wrapper;
	}

	@Override
	public void didExecuteCommand(Object client) {
		if(client instanceof SshClientWrapper) {
			SshClientWrapper wrapper = (SshClientWrapper)client;
			pool.returnObject(wrapper.getConfig(), wrapper);
		}
	}

	@Override
	public void didDisConnected(Object client) {
		if(client instanceof SshClientWrapper) {
			SshClientWrapper wrapper = (SshClientWrapper)client;
			try {
				pool.invalidateObject(wrapper.getConfig(), wrapper);
			} catch (Exception e) {
				logger.error("invalidate object "+client+" failed",e);
			}
		}
	}
	
	@Override
	public void didConnected(Object client) {
		
	}
	
}
