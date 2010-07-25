package edu.upenn.cis505.g48.utility;
import java.io.Serializable;

	
	public class test_email implements Serializable {
		public int title;
		public String content;
		
		public test_email ( int ti, String con) {
			title = ti;
			content = con;
		}

		public test_email() {
			// TODO Auto-generated constructor stub
		}
	}