package edu.upenn.cis505;

public class Util {
	
	public static int parsePortNumber(String portString) {
		int port = -1;
		try{ 
			port = Integer.parseInt(portString);
			if (port > 65535 || port < 0){
				throw new IllegalArgumentException("Port out of range!");
			}
		} catch (NumberFormatException e){
			System.out.println("Port number has to be an integer!");
			return -1;
		} catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
			return -1;
		}
		return port;
	}
}
