package web;
import java.net.*;  // Gittleman, Example 2.2, pp. 67-68
import java.applet.Applet;

public class ShowURL extends Applet {
   /**
	 * 
	 */
	private static final long serialVersionUID = 8042444173949832039L;

public void init() {
      try {
         URL url = new URL(getParameter("www.google.com"));
         getAppletContext().showDocument(url);
      }
      catch(MalformedURLException e) {
         e.printStackTrace();
      }
   }
}

