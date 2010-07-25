package socketTest;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
public class Client {

	public static void main(String[] args){
		try {
			Socket sock = new Socket("localhost", 10000);
			System.out.println("Client: Connected to server");
			ObjectOutputStream os = new ObjectOutputStream(sock.getOutputStream());
			ObjectInputStream is = new ObjectInputStream(sock.getInputStream());
			os.writeUTF("hello from client");
			ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
			os.writeObject(new byte[3]);
			os.close();
			sock.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
