package com.thirtythreelabs.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class readXmlResource {

	public static String readXml(Context context, int res) {
	    InputStream raw = context.getResources().openRawResource(res);
	
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	
	    int i;
	    try {
	        i = raw.read();
	        while (i != -1) {
	            byteArrayOutputStream.write(i);
	            i = raw.read();
	        }
	        raw.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	
	
	    return byteArrayOutputStream.toString();
	
	}
}
