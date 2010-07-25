package cis551proj4.log;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.KeyException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.logging.Logger;

import cis551proj4.log.viewer.LogMenu;

public class LogReader {

	PrivateKey key;
	ArrayList<LogMessage> messages = new ArrayList<LogMessage>();
	
	private static Logger log =
	    Logger.getLogger(LogMenu.class.getName());
	
	public LogReader(PrivateKey key){
		this.key = key;
	}
	
	public void loadFile(String fileName) throws IOException, KeyException{
		ObjectInputStream reader = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileName)));
		
		log.info("Opened Stream for " + fileName);
		
		try{
			LogEntry temp;
			do{
				temp = (LogEntry)reader.readObject();
				if(temp == null) break;
				
				Object record = temp.decryptObject(key);

				if(record instanceof Exception){
					messages.add(new LogMessage((Exception)record));
				}
				else if(record instanceof LogMessage){
					messages.add((LogMessage)record);
				}
				else{
					log.info("Unknown Object Type Found and Ignored");
				}
			}while(temp != null);
		}catch(EOFException e){
			log.info("Done Reading File");
		}catch(ClassNotFoundException e){
			throw new IOException("Class not found " + e.getMessage());
		}finally{
			reader.close();
		}
	}
	
	public String getLogText(){
		StringBuffer b = new StringBuffer();
		for(int i = 0; i < messages.size(); i++){
			b.append((i+1)).append(": ");
			b.append(messages.get(i).toString());
			b.append("\n");
		}
		
		return b.toString();
		
	}
}
