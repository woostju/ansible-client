package com.github.woostju.ansible.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.schmizz.sshj.common.IOUtils;

/**
 * 
 * @author jameswu
 * 
 * 系统本地进程执行
 */
public class SystemCommandExecutor {
	
	private final static Logger logger = LoggerFactory.getLogger(SystemCommandExecutor.class);
	
	boolean stopped = false;
	
	private SystemCommandExecutor(){
		
	}
	
	public static SystemCommandExecutor newExecutor(){
		return new SystemCommandExecutor();
	}
	
	public List<String> executeCommand(List<String> command, int timeout) throws IOException {
		InputStream inputStream  = null;
		InputStream errorStream = null;
		BufferedReader reader = null;
		BufferedReader error_reader = null;
		Process process = null;
		List<String> stdout = new ArrayList<String>();
		try {
			logger.info("execute system command: "+command.stream().collect(Collectors.joining(" ")));
			// create the ProcessBuilder and Process
			ProcessBuilder pb = new ProcessBuilder(command);
			process = pb.start();
			// i'm currently doing these on a separate line here in case i need
			// to set them to null
			// to get the threads to stop.
			// see
			// http://java.sun.com/j2se/1.5.0/docs/guide/misc/threadPrimitiveDeprecation.html
			inputStream = process.getInputStream();
			errorStream = process.getErrorStream();
			reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        	error_reader = new BufferedReader(new InputStreamReader(errorStream, "UTF-8"));
            String outputLine;
			while(!stopped && (outputLine = error_reader.readLine())!=null){
            	stdout.add(outputLine);
            	logger.debug(outputLine);
            }
            while(!stopped && (outputLine = reader.readLine())!=null){
            	stdout.add(outputLine);
            	logger.debug(outputLine);
            }
		}catch(Exception e) {
			logger.error("error execute system command", e);
		}
		finally {
			stopped=true;
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(errorStream);
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(error_reader);
			if(process!=null) {
				try {
					process.destroyForcibly();
				}catch(Exception e) {
					
				}
			}
		}
		return stdout;
	}
}
