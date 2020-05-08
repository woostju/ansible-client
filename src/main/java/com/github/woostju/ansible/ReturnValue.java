package com.github.woostju.ansible;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * Formatted return value of Ansible command output
 * 
 * @author jameswu
 *
 */
public class ReturnValue {
	public enum Result {
		failed, changed, success, unreachable, unmanaged, unknown
	}

	private Result result;
	
	private int rc;
	
	private Map<String, Object> value;
	
	private List<String> stdout = new ArrayList<String>();

	/**
	 * 
	 * @return output
	 */
	public List<String> getStdout() {
		return stdout;
	}

	/**
	 * set output
	 * @param stdout output
	 */
	public void setStdout(List<String> stdout) {
		this.stdout = stdout;
	}

	/**
	 * 
	 * @return return code
	 */
	public int getRc() {
		return rc;
	}

	/**
	 * set return code
	 * @param rc return code
	 */
	public void setRc(int rc) {
		this.rc = rc;
	}

	/**
	 * 
	 * @return result
	 */
	public Result getResult() {
		return result;
	}
	
	/**
	 * set result
	 * @param result result
	 */
	public void setResult(Result result) {
		this.result = result;
	}

	
	/**
	 * 
	 * @return json stout to map 
	 */
	public Map<String, Object> getValue() {
		return value;
	}
	
	/**
	 * 
	 * @param value json stout to map 
	 */
	public void setValue(Map<String, Object> value) {
		this.value = value;
	}

	
	/**
	 * 
	 * @param key key in json
	 * @return value in json
	 */
	public Object getValue(String key) {
		return this.getValue().get(key);
	}
	
	/**
	 * @return if result is changed or success, then true, else false
	 */
	public boolean isSuccess() {
		return this.getResult()==Result.changed || this.getResult()==Result.success;
	}
	
	@Override
	public String toString() {
		return "rc="+rc+" result="+result+" value="+value;
	}

}
