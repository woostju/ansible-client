package com.github.woostju.ansible;

import java.util.ArrayList;
import java.util.List;

public class ReturnValue {
	public enum Result{
		failed,
		changed,
		success,
		unreachable,
		unmanaged,
		unknown
	}
	private Result result;
	private int rc;
	private Object value;
	private List<String> stdout = new ArrayList<String>();
	
	public List<String> getStdout() {
		return stdout;
	}
	public void setStdout(List<String> stdout) {
		this.stdout = stdout;
	}
	
	public int getRc() {
		return rc;
	}
	public void setRc(int rc) {
		this.rc = rc;
	}
	public Result getResult() {
		return result;
	}
	public void setResult(Result result) {
		this.result = result;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
}
