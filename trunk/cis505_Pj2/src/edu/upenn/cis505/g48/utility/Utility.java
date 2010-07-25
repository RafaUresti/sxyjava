package edu.upenn.cis505.g48.utility;


import java.io.*;

public class Utility {
	
	//public Write_Utility() {}
	
	public static boolean write_to(Serializable obj, String objFile) 
    throws IOException
    {
          FileOutputStream fos = new FileOutputStream(objFile);
          ObjectOutputStream oos = new ObjectOutputStream(fos);
          oos.writeObject(obj);
          oos.close();
          fos.close();
          return true;
    }
	
	public static Object read_from(String objFile) 
    throws IOException 
    {
          FileInputStream fis = new FileInputStream(objFile);
          ObjectInputStream ois = new ObjectInputStream(fis);
          Object obj = null;
          try {
                obj = ois.readObject();
          } catch (ClassNotFoundException e) {
                System.out.println("load failed: " + e);
                System.exit(1);
          }
          ois.close();
          fis.close();
          return obj;
    }
}
