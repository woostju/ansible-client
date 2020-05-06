package com.github.woostju.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {

	private final static Logger logger = LoggerFactory.getLogger(FileUtil.class);
	
	/**
	 * 
	 * @param filePath contain folder and filename
	 * @return folder in filepath
	 */
	public static String getFolder(String filepath) {
		String result = filepath;
		result = result.replaceAll("\\\\", "/");
		if (filepath.contains("/")) {
			result = filepath.substring(0, filepath.lastIndexOf("/"));
		}
		return result;
	}
	
	
	public static boolean createFile(String fullfilepath, String content, boolean replace) {
		if(content==null || content.length()==0){
			logger.error("create file "+fullfilepath+":no content at all");
			return false;
		}
	    //create folder if necessary
		String folderPath = getFolder(fullfilepath);
		File folder = new File(folderPath);
		if(!folder.exists()){
			folder.mkdirs();
		}
	    File file = new File(fullfilepath);
	    boolean writeContent = false;
	    if (!file.exists()) {
	    	 try {
		         file.createNewFile();
		         writeContent = true;
	        } catch (IOException e) {
	        	logger.error("create file "+fullfilepath+" error", e);
	        	return false;
	        }
	    }else{
	    	if(replace){
	    		writeContent = true;
	    	}
	    }
	    if(!writeContent){
	    	return true;
	    }
	    FileOutputStream fileOutputStream = null;
	    try {
	    	fileOutputStream = new FileOutputStream(file);
			if (content != null) {
				fileOutputStream.write(content.getBytes("UTF-8"));
			}
			fileOutputStream.close();
	    } catch (Exception e) {
	    	logger.error("create file "+fullfilepath+" error ", e);
        	return false;
	    }finally {
			IOUtils.closeQuietly(fileOutputStream);
		}
        return true;
	}
}
