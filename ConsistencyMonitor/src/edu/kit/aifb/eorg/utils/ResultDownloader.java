import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;


public class ResultDownloader {
	
	static PrintWriter cf;
	
public static void main(String[] args) {
		
		Properties  props = new Properties();
		
		if (args.length!=1){
			System.out.println("Bitte [*.properties] als Argument angeben.");
		}
		
		else {		
			
				String propsFile = args[0];
                try {
                    props.load(new FileInputStream(propsFile));
                } catch (Exception e) {
                    System.out.println("Error loading properties: " +
                                       e.getMessage());
                }
               
    		ArrayList<String> serverUrls;   
    		String user   = props.getProperty("username");		
			String DNS_file = props.getProperty("DNS");
			String keys = props.getProperty("private-keys");
			HashMap <String, String> keyMap = new KeyGen(keys).toHashMap();	
			String outputDir = props.getProperty("outputDir");
			String downloadOutputDir = props.getProperty("DownloadOutputDir").replaceAll("/", "\\\\");
						
			try {

				System.out.println("Erstelle txt file in: " + outputDir+"\\Resources\\commandFile.txt");
				File commandFile = new File(outputDir+"\\Resources\\commandFile.txt");
				cf = new PrintWriter(new FileWriter(commandFile));
				writeBaseCommands();
				
				System.out.println("Lese " + DNS_file);
				serverUrls = readServerUrls(DNS_file);				
				
				writeCommands(user, serverUrls, keyMap, downloadOutputDir);
				
				System.out.println("txt Erstellung erfolgreich!");				
				
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
	}

public static void writeBaseCommands (){
	cf.println("# Automatically abort script on errors");
	cf.println("# option batch abort");
	cf.println("# Disable overwrite confirmations that conflict with the previous");
	cf.println("option confirm off");
	cf.println("# Force binary mode transfer");
	cf.println("option transfer binary");
}

public static ArrayList<String> readServerUrls (String serverUrls) throws IOException{
	
	ArrayList<String> dnsStrings = new ArrayList<String>();		
	
		BufferedReader br = new BufferedReader(new FileReader(serverUrls));
		String line;
		
		while ((line=br.readLine())!=null){
			dnsStrings.add(line); 
		}			
		br.close();
	
	return dnsStrings;
}

public static void writeCommands (String user, List<String> serverUrls, HashMap<String,String> keyMap, String downloadOutputDir) throws IOException{

		Iterator<String> iter = serverUrls.iterator();
		String key = null;
		cf.println("# Connect");
		
		// Erster Durchlauf um für alle Server Key in den Cache zu laden.
		while (iter.hasNext()){
			String line=iter.next();
			String serverURL = line.substring(line.indexOf(":")+1);	
			
			Set <String> set = keyMap.keySet();
			Iterator <String> setIter = set.iterator();
			while (setIter.hasNext()){
				String keyString = setIter.next();					
				if (serverURL.contains(keyString)){
					key=keyMap.get(keyString);
				}
			}						
			
			cf.println("open sftp://"+user+"@"+serverURL + " -privatekey="+"\""+key+"\"");
			cf.println("close");
		}
			
		//Zweiter Durchlauf für Befehle
		iter = serverUrls.iterator();
		while (iter.hasNext()){
			String line=iter.next();
			String role = line.substring(0, line.indexOf(":"));	
			String serverURL = line.substring(line.indexOf(":")+1);	
			
			Set <String> set = keyMap.keySet();
			Iterator <String> setIter = set.iterator();
			while (setIter.hasNext()){
				String keyString = setIter.next();					
				if (serverURL.contains(keyString)){
					key=keyMap.get(keyString);
				}
			}						
			
			cf.println("open sftp://"+user+"@"+serverURL + " -privatekey="+"\""+key+"\"");
			cf.println("cd /home/ec2-user/monitoring");		
			cf.println("rm *.jar");
			cf.println("get ./ "+downloadOutputDir+"\\Downloads\\"+role);	
			cf.println("close");
		}
		cf.println("exit");
		cf.close();	

	}
}
