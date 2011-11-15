/**
 * 
 */
package edu.kit.aifb.eorg.connectors;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;

/**
 * 
 * 
 * 
 * @author Robin Hoffmann
 * 
 *         created on: 14.11.2011
 */
public final class GAEConnector {

	protected static String urlString; 
	protected static RandomAccessFile file; 
	
	public final static void doInitialize(String url)  {
		urlString = url;		
		try {
			file = new RandomAccessFile("latencies.csv", "rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public final static void writeToGAE (final String dbKey, final String dbValue) {
		    			
	    try {  
	    	String key = URLEncoder.encode("key", "UTF-8") + "=" + URLEncoder.encode(dbKey, "UTF-8");
		    String data = key + "&" + URLEncoder.encode("value", "UTF-8") + "=" + URLEncoder.encode(dbValue, "UTF-8");
    	    // POST data
    	    URL url = new URL(urlString);
    	    URLConnection conn = url.openConnection();
    	    conn.setDoOutput(true);
    	    conn.setReadTimeout(5000);
    	    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
    	    long startTime = new Date().getTime();
    	    wr.write(data);
    	    wr.flush();
    	    	    	    
    	    // Get the response
    	    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    	    String line;
    	    while ((line = rd.readLine()) != null) {
    	        System.out.println(line);	    	        
    	    }
    	    long endTime = new Date().getTime();
    	    long latency = (endTime-startTime)/2;
    	    file.writeBytes("\nLatency POST in ms: ;" + latency);
    	    wr.close();
    	    rd.close();
	    	}
	    	catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    } 	    	    		

	public final static String readFromGAE(final String key) {
		String result = "";
		try{
    	    // GET data
    	    URL url = new URL(urlString+"?"+URLEncoder.encode("key", "UTF-8") + "=" + URLEncoder.encode(key, "UTF-8");
    	    URLConnection conn = url.openConnection();
    	    conn.setReadTimeout(5000);
    	    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    	    String line;
    	    long startTime = new Date().getTime();
    	    while ((line = rd.readLine()) != null) {
    	        result = result + "\n" + line;
    	    }
    	    long endTime = new Date().getTime();
    	    long latency = (endTime-startTime)/2;
    	    file.writeBytes("\nLatency GET in ms: ;" + latency);
    		rd.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
