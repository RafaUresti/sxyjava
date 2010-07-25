package cis551proj4.log.viewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.KeyException;
import java.security.KeyPair;
import java.util.logging.Logger;

import javax.swing.*;

import cis551proj4.Disk;
import cis551proj4.log.LogReader;

public class LogMenu extends JMenuBar{
	
	private static Logger log =
	    Logger.getLogger(LogMenu.class.getName());
	
	static final long serialVersionUID = 2345567432456L;
	
	private JFileChooser fileChoose = new JFileChooser();
	private LogViewer viewer = null;
	
	JMenu fileMenu = new JMenu("File");
	
	// Callback for opening a Log File
	ActionListener openLogAction = new ActionListener(){
		public void actionPerformed(ActionEvent event){
			int result;
									
			result = fileChoose.showDialog(LogMenu.this.getParent(), 
					"Open Log File");
			if(result == JFileChooser.APPROVE_OPTION){
				try{
					if(viewer == null){
						 JOptionPane.showMessageDialog(LogMenu.this, "Pane Did Not Load Correctly",
								 "Error: Pane did not load correctly",
			                      JOptionPane.ERROR_MESSAGE);
						 return;
					}
					if(viewer.getKeys() == null || viewer.getKeys().getPrivate() == null){
						 JOptionPane.showMessageDialog(LogMenu.this, "No Private Key is loaded",
								 "Error: No Private Key is Loaded",
			                      JOptionPane.ERROR_MESSAGE);
						 return;
					}
					
					LogReader reader = new LogReader(viewer.getKeys().getPrivate());
					reader.loadFile(fileChoose.getSelectedFile().getAbsolutePath());
					
					log.info("Loading Log File " + fileChoose.getSelectedFile().getAbsolutePath() + " loaded");
					
					LogMenu.this.viewer.getMainPanel().getTextArea().setText(reader.getLogText());
									
					log.info("Done loading file");
					
				}catch(IOException e){
					 JOptionPane.showMessageDialog(LogMenu.this, "I/O Error While Opening File: " + e.getMessage(),
		                       "I/O Error While Opening File: " + e.getMessage(), 
		                       JOptionPane.ERROR_MESSAGE);
					 e.printStackTrace();
				}catch(KeyException e){
					 JOptionPane.showMessageDialog(LogMenu.this, "Error: " + e.getMessage(),
		                       "Error: " + e.getMessage(), 
		                       JOptionPane.ERROR_MESSAGE);
				}

			}
			fileChoose.setSelectedFile(null);
		}
	};
	
	// Callback for opening a Key File
	ActionListener openKeyAction = new ActionListener(){
		public void actionPerformed(ActionEvent event){
			int result;
									
			result = fileChoose.showDialog(LogMenu.this.getParent(), 
					"Open Key File");
			if(result == JFileChooser.APPROVE_OPTION){
				try{
					if(viewer != null){
						viewer.setKeys(
								(KeyPair)Disk.load(fileChoose.getSelectedFile()
										.getAbsolutePath()));
						log.info("Key File " + fileChoose.getSelectedFile() + " loaded");
					}
				}catch(IOException e){
					 JOptionPane.showMessageDialog(LogMenu.this, e.getMessage(),
		                       "I/O Error While Opening File: " + e.getMessage(), 
		                       JOptionPane.ERROR_MESSAGE);
				}
			}
			fileChoose.setSelectedFile(null);
		}
	};
	
	ActionListener exitAction = new ActionListener(){
		public void actionPerformed(ActionEvent event){
			System.exit(0);
		}
	};
	
	String[] fileItems = new String[] {"Open Log File", "Open Key File", "Exit"};
	char[] fileShortcuts = {'O', 'K', 'X'};
	ActionListener[] fileActions = new ActionListener[] {openLogAction, openKeyAction, exitAction};
	
	/**
	 * MainMenu default constructor
	 */
	public LogMenu(LogViewer viewer){
		super();
		
		this.viewer = viewer;
		
		JMenuItem tempMenuItem;
				
		// Add the file menus
		for(int i = 0; i < fileItems.length; i++){
			tempMenuItem = new JMenuItem(fileItems[i], fileShortcuts[i]);
			tempMenuItem.addActionListener(fileActions[i]);
			
			if(i == fileItems.length - 1){
				fileMenu.addSeparator();
			}
			
			fileMenu.add(tempMenuItem);
		}
		
		// Add the file menu to the window
		add(fileMenu);
	}
}