import java.io.*;
import java.net.InetAddress;
import java.util.*;

/**
 * This class aggregates server data to one single batch-file which fills
 * every consistency monitoring instance with the data it needs.
 * Better Version with integrated SSH-Communication-Thread and Multi-Key-Support
 * 
 * @author Robin Hoffmann
 * 
 *         created on: 12.12.2011
 */
public class SudoCompiler {

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
			String puttySrc = props.getProperty("putty");
			String DNS_file = props.getProperty("DNS");
			String installFile = props.getProperty("install");
			String keys = props.getProperty("private-keys");
			System.out.println(keys);
			HashMap <String, String> keyMap = new KeyGen(keys).toHashMap();			
			String rolesDir = props.getProperty("rolesDir");
			String outputDir = props.getProperty("outputDir");
			String monitoring_props = props.getProperty("monitoring-properties");
			
			try {
				System.out.println("Erstelle Roles-Verzeichnis...");
				new File(outputDir+"\\Roles").mkdirs();
				System.out.println("Lese " + DNS_file);
				serverUrls = readServerUrls(DNS_file);
				System.out.println("Erstelle shell- und command-files...");
				createResources (serverUrls, installFile, rolesDir, outputDir);
				System.out.println("Aktualisiere monitoring.properties...");
				updateCollectorIP (monitoring_props, serverUrls);
				System.out.println("Erstelle batch file in: " + outputDir+"\\putty.bat");
				createBatchFiles (puttySrc, serverUrls, keyMap, outputDir);
				System.out.println("Batch Erstellung erfolgreich! Lade erforderliche Dateien auf Server...");
				System.out.println("Beginne Initialisierung...");				
				initialize(serverUrls, user, keyMap, outputDir);
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
	}
	
	private static void updateCollectorIP(String monitoring_props, List<String> ServerUrls) throws IOException {

		BufferedReader br;
		PrintWriter pw;
		String ipRaw = ServerUrls.get(0);
		String ip = InetAddress.getByName(ipRaw.substring(ipRaw.indexOf(":") + 1)).getHostAddress();
		System.out.println("Neue Collector-IP: "+ip);
		
		String line;
		String newLine;
		File monitoring_props_temp = new File("temp.properties");
		br = new BufferedReader (new InputStreamReader(new FileInputStream(monitoring_props)));
		pw = new PrintWriter (new OutputStreamWriter(new FileOutputStream(monitoring_props_temp)));
		while ((line = br.readLine())!=null){
			newLine = line;
			if (line.startsWith("collectorurl")) {
				newLine = line.replaceAll("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}", ip);
			}	
			pw.println(newLine);
		}
		br.close();
		pw.close();
		br = new BufferedReader (new InputStreamReader(new FileInputStream(monitoring_props_temp)));
		pw = new PrintWriter (new OutputStreamWriter(new FileOutputStream(monitoring_props)));
		while ((line = br.readLine())!=null){
			pw.println(line);
		}
		br.close();
		pw.close();
		monitoring_props_temp.delete();
	}
	
	public static ArrayList<String> readServerUrls (String serverUrls) throws IOException{
		
		ArrayList<String> dnsStrings = new ArrayList<String>();		
		
			BufferedReader br = 
				new BufferedReader(new InputStreamReader(new FileInputStream(serverUrls)));
			String line;
			
			while ((line=br.readLine())!=null){
				dnsStrings.add(line); 
			}			
			br.close();
		
		return dnsStrings;
	}
	
	public static void createResources (List<String> serverUrls, String installFile, 
			String rolesDir, String outputDir) throws FileNotFoundException{
		
		Iterator<String> iter = serverUrls.iterator();
			while (iter.hasNext()){
				PrintWriter sh;
				PrintWriter cmd;
				String role;
				String line = iter.next();
				role = line.substring(0, line.indexOf(":"));
				
				String shell = "commands_"+role+".sh";
				File shellFile = new File (outputDir +"\\Roles\\"+shell);
								
				String command = outputDir+"\\Roles\\remoteCommands_"+role+".txt";
				File commandFile = new File (command);
				
				//Shell
				sh = new PrintWriter(new OutputStreamWriter(new FileOutputStream(shellFile)));
				sh.println("wget " + installFile + " -nc;"); 
				sh.println("chmod 775 install.sh;"); 
				sh.println("sudo ./install.sh;");
				sh.println("cd monitoring;");
				sh.println("chmod 775 run.sh;");
				sh.println("echo ““  >  " + role +";");
				sh.println("find . -name \"run.sh\" -exec sed -i 's/<id>/" + role +"/g' {} \\;;");
//				sh.println("screen ./run.sh;");
				sh.close();
				//Command
				cmd = new PrintWriter(new OutputStreamWriter(new FileOutputStream(commandFile)));
				cmd.println("wget " + rolesDir + "/"+shell + " -nc;"); 
				cmd.println("mv " +shell+ " start.sh;");
				cmd.println("chmod 775 start.sh;");
				cmd.println("echo ““  >  " + role +";");
				cmd.println("exit;");
				cmd.close();
		}
		
	}
	
	public static void createBatchFiles (String puttySrc, List<String> serverUrls, HashMap<String,String> keyMap, String outputDir) throws FileNotFoundException{
		
		File batchFile = new File(outputDir+"\\putty_optional.bat");
		File manuellFile = new File(outputDir+"\\2_manuell.bat");
		PrintWriter bf = new PrintWriter(new OutputStreamWriter(new FileOutputStream(batchFile)));
		PrintWriter mf = new PrintWriter(new OutputStreamWriter(new FileOutputStream(manuellFile)));
		Iterator<String> iter = serverUrls.iterator();
		String key = null;
		
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
				
				bf.println("start "+ puttySrc +" ec2-user@"+ serverURL +" -i \""+key+ "\" -m \".\\Roles\\remoteCommands_"+role+".txt\"");		
				mf.println("start "+ puttySrc +" ec2-user@"+ serverURL +" -i \""+key);				
			}
			bf.println("exit");
			mf.println("exit");
			bf.close();	
			mf.close();
	}
	private static void initialize(ArrayList<String> serverUrls, String user, HashMap <String, String> keyMap, String outputDir) {

		Iterator<String> iter = serverUrls.iterator();
		while (iter.hasNext()){
			String line=iter.next();
			String role = line.substring(0, line.indexOf(":"));	
			String host = line.substring(line.indexOf(":")+1);	
			String key = null;
			
			Set <String> set = keyMap.keySet();
			Iterator <String> setIter = set.iterator();
			while (setIter.hasNext()){
				String keyString = setIter.next();
				if (host.contains(keyString)){
					key=keyMap.get(keyString);
				}
			}
		
			System.out.println(role +": "+host);			
			new RemoteShell(host, user, key, outputDir+"\\Roles\\remoteCommands_"+role+".txt").start();
		}
		
	}

}


