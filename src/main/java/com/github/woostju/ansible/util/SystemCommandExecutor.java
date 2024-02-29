package com.github.woostju.ansible.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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

	private final ExecutorService executorService;

	private SystemCommandExecutor(){
		this.executorService = Executors.newFixedThreadPool(2);
	}
	
	public static SystemCommandExecutor newExecutor(){
		return new SystemCommandExecutor();
	}

	public List<String> consumeStream(InputStream inputStream) {
		List<String> output = new ArrayList<>();
		executorService.submit(()->{
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				String outputLine;
				while((outputLine = reader.readLine())!=null){
					output.add(outputLine);
					logger.debug(outputLine);
				}
				reader.close();
			} catch (Exception e) {
				logger.error("Consume Stream Error", e);
			} finally {
				IOUtils.closeQuietly(reader);
			}
		});
		return output;
	}
	
	public List<String> executeCommand(List<String> command, int timeout) throws IOException {
		InputStream inputStream  = null;
		InputStream errorStream = null;
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
			List<String> stdOut = consumeStream(inputStream);
			List<String> errorOut = consumeStream(errorStream);

			process.waitFor();
			stdout.addAll(errorOut);
			stdout.addAll(stdOut);
			return stdout;
		}catch(Exception e) {
			logger.error("error execute system command", e);
		}
		finally {
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(errorStream);
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
