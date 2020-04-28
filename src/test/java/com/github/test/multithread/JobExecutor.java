package com.github.test.multithread;

import java.util.function.Supplier;

public class JobExecutor {
	private Object key;
	private JobExecutorState state;
	private Object rtObject;
	private Throwable throwable;
	private Supplier<Object> task;
	
	public boolean stateComplete(){
		return state != JobExecutorState.created
				&&  state != JobExecutorState.running;
				
	}
	
	public boolean stateTimeout(){
		return state == JobExecutorState.timeout;
	}
	
	public boolean stateSuccessful(){
		return state == JobExecutorState.successful;
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public JobExecutorState getState() {
		return state;
	}

	public void setState(JobExecutorState state) {
		this.state = state;
	}

	public Object getRtObject() {
		return rtObject;
	}

	public void setRtObject(Object rtObject) {
		this.rtObject = rtObject;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	public Supplier<Object> getTask() {
		return task;
	}

	public void setTask(Supplier<Object> task) {
		this.task = task;
	}
	
	
}
