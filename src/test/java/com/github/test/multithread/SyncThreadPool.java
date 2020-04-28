package com.github.test.multithread;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;


/**
 * 
 * @author jameswu
 *
 *	thread pool 
 *	execute multithread and return result in main thread
 */
public class SyncThreadPool {
	private final static Logger logger = LoggerFactory.getLogger(SyncThreadPool.class);
	
	private Map<Object, JobExecutor> jobs = new ConcurrentHashMap<>();
	private Map<Object, Future<?>> futures = new ConcurrentHashMap<>(); 
	
	private int timeout;
	private TimeUnit timeunit;
	private SyncThreadPool(){
		
	}
	
	public static SyncThreadPool newPool(){
		return newPool(-1, TimeUnit.SECONDS);
	}
	
	public static SyncThreadPool newPool(int timeOut, TimeUnit timeunit){
		SyncThreadPool pool = new SyncThreadPool();
		pool.timeout = timeOut;
		pool.timeunit = timeunit;
		return pool;
	}
	
	/**
	 * 
	 * threadpool.addJob("name", ()->{return value;})
	 * @param key
	 * @param task
	 */
	public SyncThreadPool addJob(Object key, Supplier<Object> task){
		JobExecutor jobExecutor = new JobExecutor();
		jobExecutor.setState(JobExecutorState.created);
		jobExecutor.setTask(task);
		jobExecutor.setKey(key);
		this.jobs.put(key, jobExecutor);
		return this;
	}
	
	public void removeJob(Object key){
		this.jobs.remove(key);
	}
	
	public void clearJobs(){
		this.jobs.clear();
	}
	
	public void stop(){
		this.killAllThreads();
		for(Object key: jobs.keySet()){
			JobExecutor job = jobs.get(key);
			if(!job.stateComplete()){
				job.setState(JobExecutorState.manaul_stop);
			}
		}
	}
	
	private void killAllThreads(){
		this.futures.values().forEach(item->{
			item.cancel(true);
		});
	}
	
	/**
	 * 
	 * throw Timeoutexception if all thread exceed timeout
	 * return [jobid:executor]
	 * executor: 
	 * 	[state:successful rtObject:nonnull] 执行成功
	 *  [state:failed rtObject:null throwable:nonnull] 执行过程中出错，如exeception
	 *  [state:timeout rtObejct:null throwable:timeoutExpcetion] job超时
	 *  [state:manual_stop rtObject:null throwable:null] 人工停止
	 * 
	 */
	public Map<Object, JobExecutor> run(){
		logger.debug("Sync thread pool begin running "+this.jobs.size()+" jobs");
		ExecutorService executorService =  Executors.newFixedThreadPool(jobs.size());
		CountDownLatch latch = new CountDownLatch(jobs.size());
		for(Object key: this.jobs.keySet()){
			Future<?> future = executorService.submit(new InnerRunnableTask(this.jobs.get(key), latch, MDC.getCopyOfContextMap()));
			this.futures.put(key, future);
		}
		executorService.shutdown();
		try {
			if(this.timeout == -1){
				latch.await();
			}else{
				latch.await(this.timeout, this.timeunit);
			}
			jobs.values().stream().filter(item->!item.stateComplete()).collect(Collectors.toList()).forEach(item->{
				item.setState(JobExecutorState.timeout);
			});
		} catch (InterruptedException e) {
			logger.error("threadpool interrupted exception", e);
		}finally {
			this.killAllThreads();		
		}
		return jobs;
	}
	
}

class InnerRunnableTask implements Runnable{
	private final static Logger logger = LoggerFactory.getLogger(InnerRunnableTask.class);
	CountDownLatch latch;
	JobExecutor executor;
	Map<String, String> logContextMap;
	
	InnerRunnableTask(JobExecutor executor, CountDownLatch latch, Map<String, String> logContextMap){
		this.latch = latch;
		this.executor =executor;
		this.logContextMap = logContextMap;
		this.executor.setState(JobExecutorState.created);
	}

	@Override
	public void run() {
		try{
			if(this.logContextMap!=null) {
				MDC.setContextMap(this.logContextMap);
			}
			executor.setState(JobExecutorState.running);
			Object object = executor.getTask().get();
			if(!executor.stateComplete()){
				executor.setState(JobExecutorState.successful);
				executor.setRtObject(object);
			}
		}catch(Exception e){
			logger.error("执行innerrunnabletask出错", e);
			if(!JobExecutorState.timeout.equals(executor.getState())) {
				executor.setState(JobExecutorState.failed);
				executor.setThrowable(e);
			}
		}finally {
			latch.countDown();
		}
	}
}
