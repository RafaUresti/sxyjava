import java.net.*;

public class RandomServerPorts
{
  public static void main(String [] args) throws Exception
  {
    for(int i=0;i<3;i++)
    {
      ServerSocket a=new ServerSocket(0);
      System.out.println("grabbed port: "+a.getLocalPort());
      a.close();
    }
  }
}
