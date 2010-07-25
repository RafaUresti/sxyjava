package cis551proj4.log;
import java.security.KeyException;
import java.security.PublicKey;
import java.security.Key;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import cis551proj4.Disk;

import cis551proj4.Crypto;

public class Log {

	private String filename;
	private PublicKey key;

	/**
	 * Primary Log constructor
	 * 
	 * The Bank log takes the PublicKey of the bank to encrypt the
	 * logs.
	 * 
	 * Each line of the log is encrypted using RSA and the public key
	 * The log can therefore be read by the private key of the Bank,
	 * which is considered secure
	 * 
	 * A log file is a series of LogEngry object.  A LogEntry object is a
	 * wrapper object for a byte array with row sizes of 256, where a 
	 * serialized object of any size can be encrypted chunk-by-chunk
	 * and stored
	 * 
	 * If a log file exists, new LogEntry's are appended on to the end of
	 * that log.
	 * 
	 * @param file The filename of the log file
	 * @param key 
	 */
	public Log(String file, PublicKey key) 
	{
			this.key = key;
			this.filename = file;
	}

	public void write(Serializable obj) throws IOException, KeyException {
		System.out.println(obj.toString());
		Disk.append(new LogEntry(obj, key), filename);
	}
}
