package cis551proj4.log.viewer;

import java.security.KeyPair;

public class LogViewer{
	
	private LogViewerPanel mainPanel;
	private KeyPair keys;
	
	public static void main(String[] args) {		
		LogViewer myproject = new LogViewer();
		myproject.show();
	}
	
	void show(){	
		mainPanel = new LogViewerPanel(this);
		mainPanel.setVisible(true);
	}
	
	public void setKeys(KeyPair keys){
		this.keys = keys;
	}
	
	public KeyPair getKeys(){
		return keys;
	}
	
	public LogViewerPanel getMainPanel(){
		return mainPanel;
	}
}
