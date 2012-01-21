package edu.kit.aifb.eorg.connectors;

import java.io.*;
import java.net.*;
import java.util.Date;

public class GAEConnectorService extends Thread{
	
	private String urlString;
	private String keyString;
	
	public GAEConnectorService (String urlString, String keyString){
		
		this.urlString = urlString;
		this.keyString = keyString;
		
	}
	public void run(){
	 
	 String result = "";
		try{
 	    // GET data
		long startTime = new Date().getTime();
 	    URL url = new URL(urlString+"?"+URLEncoder.encode("key", "UTF-8") + "=" + URLEncoder.encode(keyString, "UTF-8"));
 	    URLConnection conn = url.openConnection();
 	    conn.setReadTimeout(5000);
 	    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
 	    String line;
 	    line = rd.readLine();    	    
 	    String [] split = line.split(";");
 	    result = split[0];
 	    String classVariable = split[1];
 	    long endTime = new Date().getTime();
 	    long latency = (endTime-startTime)/2;
 	    System.out.println("Data = "+result+"\nKlassenvariable: "+classVariable+"\nLatency GET in ms: " + latency);
 	    System.out.println("\nAuf Server gespeichert:\n"+result+";Klassenvariable;"+classVariable+";Latency GET in ms:;" + latency + "\n;");
 		rd.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
}

