package socketTest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String[] args){
		try {
			ServerSocket sk = new ServerSocket(10000);
			Socket sock = sk.accept();
			System.out.println("Server: got connection from client");
			ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
			ObjectInputStream is = new ObjectInputStream(sock.getInputStream());
			System.out.println("oos closed");
			String hello = is.readUTF();
			System.out.println(hello);
			byte[] b = (byte[])is.readObject();
			System.out.println(b.length);
			is.close();
			oos.close();
			sock.close();
			System.out.println("is closed");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
