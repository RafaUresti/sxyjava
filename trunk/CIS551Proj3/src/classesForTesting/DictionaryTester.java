package classesForTesting;

import java.io.IOException;

import cis551proj3.dict.Dictionary;

public class DictionaryTester {
	public static void main(String[] args){
		Dictionary y = new Dictionary();
		try{
			y.loadFile("DICT.TXT");
			System.out.println("Found 'hello' " + y.lookup("HELLO"));
			System.out.println("Found 'goodbye' " + y.lookup("goodbye"));
			System.out.println("Found 'begin' " + y.lookup("begin"));
			System.out.println("Found 'end' " + y.lookup("end"));
			System.out.println("Found 'End' " + y.lookup("End"));
			System.out.println("Found 'proliferate' " + y.lookup("proliferate"));
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
