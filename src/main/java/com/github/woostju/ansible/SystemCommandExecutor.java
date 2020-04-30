package com.github.woostju.ansible;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jameswu
 * 
 * 系统本地进程执行
 */
public class SystemCommandExecutor {
	
	private final static Logger logger = LoggerFactory.getLogger(SystemCommandExecutor.class);
	
	boolean stopped = false;
	
	protected RemoteResult result = RemoteResult.FAIL;
	
	protected String errorMessage = "";
	
	private SystemCommandExecutor(){
		
	}
	
	public static SystemCommandExecutor newExecutor(){
		return new SystemCommandExecutor();
	}
	
	public SystemCommandExecutor executeCommand(List<String> command, int timeout, Consumer<List<String>> stoutConsumer) {
		try {
			logger.info("execute system command: "+command.stream().collect(Collectors.joining(" ")));
			// create the ProcessBuilder and Process
			ProcessBuilder pb = new ProcessBuilder(command);
			Process process = pb.start();
			
			// i'm currently doing these on a separate line here in case i need
			// to set them to null
			// to get the threads to stop.
			// see
			// http://java.sun.com/j2se/1.5.0/docs/guide/misc/threadPrimitiveDeprecation.html
			InputStream inputStream = process.getInputStream();
			InputStream errorStream = process.getErrorStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        	BufferedReader error_reader = new BufferedReader(new InputStreamReader(errorStream, "UTF-8"));
        	List<String> outputLines = new ArrayList<>();
            String outputLine;
            while(!stopped && (outputLine = error_reader.readLine())!=null){
            	outputLines.add(outputLine);
            }
            while(!stopped && (outputLine = reader.readLine())!=null){
            	outputLines.add(outputLine);
            }
            if(Thread.interrupted()) {
            	errorMessage = "execute command timeout";
            	logger.error("execute command timeout");
            }else {
            	logger.debug("finish executing command, console:");
            	outputLines.forEach(item->{
                	logger.debug(item);
                });
                if(stoutConsumer != null){
                	stoutConsumer.accept(outputLines);
                }
                result = RemoteResult.SUCCESS;
            }
		}
		catch (IOException e) {
			errorMessage = "execute command fail with reason:"+e.getMessage();
			logger.error("execute command fail",e);
		} finally {
			stopped=true;
		}
		return this;
		
	}
	
	public RemoteResult getResult() {
		return result;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public static void main(String[] args) {
		//ArrayList<String> commands = Lists.newArrayList("./ansible", "-i","/Users/jameswu/ansible/inventory","47.99.222.237","-m command -a echo 'a'");
		ArrayList<String> commands  = Lists.newArrayList("./sshpass");
	}
}
