/**
 * 
 */
package edu.kit.aifb.eorg.connectors;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.amazon.s3.AWSAuthConnection;
import com.amazon.s3.S3Object;

/**
 * 
 * This class provides some very fast but hard-coded access methods for S3
 * 
 * @author Robin Hoffmann
 * 
 *         created on: 10.11.2011
 */
public final class GAEConnector {

	private static String urlString;
	
	public final static void doInitialize(String url)  {
		urlString = url;
	}

	public final static void writeToGAE (final String dbKey, final String dbValue) {
		String key = URLEncoder.encode("key", "UTF-8") + "=" + URLEncoder.encode(dbKey, "UTF-8");
	    String data = key + "&" + URLEncoder.encode("value", "UTF-8") + "=" + URLEncoder.encode(dbValue, "UTF-8");
    	
	    try {   
	    	
    	    // POST data
    	    URL url = new URL(urlString);
    	    URLConnection conn = url.openConnection();
    	    conn.setDoOutput(true);
    	    conn.setReadTimeout(5000);
    	    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
    	    wr.write(data);
    	    wr.flush();
    	    	    	    
    	    // Get the response
    	    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    	    String line;
    	    while ((line = rd.readLine()) != null) {
    	        System.out.println(line);	    	        
    	    }
    	    wr.close();
    	    rd.close();
	    	}
	    	catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    } 	    	    	   
	
	

	public final static String readFromGAE(final String key) {
		String result;
		try{
    	    // GET data
    	    URL url = new URL(urlString+"?"+key);
    	    URLConnection conn = url.openConnection();
    	    conn.setReadTimeout(5000);
    	    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    	    String line;
    	    while ((line = rd.readLine()) != null) {
    	        result = result + "\n" + line;
    	    }
    		rd.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
