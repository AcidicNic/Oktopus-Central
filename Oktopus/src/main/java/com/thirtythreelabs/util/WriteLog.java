package com.thirtythreelabs.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteLog {
	
	private final static String LOG_TAG = "WriteLog";

	public void appendLog(String text){       
	
		File logFile = new File("/sdcard/download/Oktopus.log");
		if (!logFile.exists()){
			try {
				logFile.createNewFile();
			} catch (IOException e){
			
				e.printStackTrace();
			}
		}
		try {
			String mDate = (String) android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", new java.util.Date());
			  
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
			buf.append(mDate + ": " + text);
			buf.newLine();
			buf.close();
		}catch (IOException e){
		
			e.printStackTrace();
		}
	}
}
