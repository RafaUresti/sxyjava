package cis551proj3.dict;

import java.io.*;
import java.io.IOException;
import java.util.*;

public class Dictionary {
	HashSet<String> words = new HashSet<String>();
		
	public Dictionary(){}
	
	public void loadFile(String name) throws IOException{
		
		System.out.println("Loading File '" + name + "' ...");
		
		RandomAccessFile file = new RandomAccessFile(name, "r");

		String line = null;
		
		do{
			line = file.readLine();
			if(line != null){
				words.add(line.toUpperCase());
			}
		}while(line != null);
		
		System.out.println("Done loading file '" + name + "' ...");
	}

	public boolean lookup(String word){
		return words.contains(word.toUpperCase());
	}
}
