import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class Renamer {

	public static void main(String[] args) {

		if (args.length!=1){
			System.out.println("Aufruf mit [inputPath]");
		}
		
		else{
			String filePath = args[0];
			
			try {
				BufferedReader br = new BufferedReader (new FileReader (new File(filePath+"/DNS_roh.txt")));
				PrintWriter pw = new PrintWriter (new FileWriter(new File(filePath+"/DNS.txt")));
				
				String line;
				int i = 0;
				while ((line=br.readLine())!=null){
					
					String substring = line.substring(line.indexOf(":")+2);
					//Collector
					if (i==0){
						pw.println("collector:"+substring);
					}
					//Writer
					else if (i==1){
						pw.println("gaewriter:"+substring);
					}
					else{
						pw.println("gaemon"+(i-1)+":"+substring);
					}
					
					i++;
				}			
				br.close();
				pw.close();
				System.out.println("Saved DNS.txt in InputDir.");
				
			} catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			
			
		}

	}

}
