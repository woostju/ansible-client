package com.github.woostju.ansible;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReturnValue {
	public enum Result {
		failed, changed, success, unreachable, unmanaged, unknown
	}

	private Result result;
	private int rc;
	private Map<String, Object> value;
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

	public Map<String, Object> getValue() {
		return value;
	}

	public void setValue(Map<String, Object> value) {
		this.value = value;
	}

	public Object getValue(String key) {
		return this.getValue().get(key);
	}
	
	public boolean isSuccess() {
		return this.getResult()==Result.changed || this.getResult()==Result.success;
	}
	
	@Override
	public String toString() {
		return "rc="+rc+" result="+result+" value="+value;
	}

}
