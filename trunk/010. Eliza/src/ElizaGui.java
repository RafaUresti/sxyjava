import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 
 * This is a user interface of Eliza.
 * It has a File menu with the following menu items on it:
 * A Load... menu item to read in the file of patterns.
 * A Save As... menu item to save the complete conversation on a file that user chooses. 
 * It also has a scrollable text area to contain the entire conversation.
 *Beneath the large text area, there is a  JTextField into which the user types.
 *Program ends when the user types Goodbye into the input field.
 */

public class ElizaGui extends JFrame{
	Eliza eliza;
	private JPanel panel;

	private JTextArea responseArea;
	private JTextField inputField;
	private JMenuBar menuBar;
	private JMenu menu;
	private JScrollPane scrollPane;
	private JMenuItem load;
	private JMenuItem saveAs;

	private ArrayList<String> myPatternList = new ArrayList<String>();
	private BufferedReader reader;
	private File file;
	private String fileName;
	private String question="";

	private PrintWriter printWriter;

	public static void main(String[] args) {
		new ElizaGui().run();
	}

	void run() {

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(10, 50, 400, 400);
		panel= new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(menuBar(), BorderLayout.NORTH);
		panel.add(responseArea(),BorderLayout.CENTER );
		panel.add(inputField(), BorderLayout.SOUTH);
		this.add(panel);
		setVisible(true);
	}

	/*
	 *Creates the JTextField for the GUI. 
	 */
	private JTextField inputField() {
		inputField = new JTextField();
		Border inputBorder = BorderFactory.createEtchedBorder();
		inputBorder = BorderFactory.createTitledBorder(inputBorder, "Please type your words here:");
		inputField.setBorder(inputBorder);
		inputField.addActionListener(new ActionListenerField());
		return inputField;
	}

	/*
	 * Creates the JTextArea for the GUI with the ScrollPane.
	 */
	private JScrollPane responseArea() {
		responseArea = new JTextArea();
		responseArea.setSize(10, 10);
		responseArea.setLineWrap(true);
		responseArea.setWrapStyleWord(true);
		scrollPane = new JScrollPane(responseArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		return scrollPane;
	}

	/*
	 * Creates the JMenuBar for the GUI.
	 */
	private JMenuBar menuBar() {
		menuBar = new JMenuBar();
		menu = new JMenu ("Menu");
		load = new JMenuItem ("Load...");
		saveAs = new JMenuItem("Save As...");
		load.addActionListener(new ActionListenerLoad());
		saveAs.addActionListener(new ActionListenerSave());

		//responseArea.addActionListener(new ActionListenerArea());
		menu.add(load);
		menu.add(saveAs);
		menuBar.add(menu);
		return menuBar;
	}

	/*
	 * Implements the ActionListener for load JMenuItem.
	 */
	class ActionListenerLoad implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Load");
			int choice=0;
			do {
				int result = chooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					file = chooser.getSelectedFile();
					try {
						if (file != null) {
							fileName = file.getCanonicalPath();
							reader = new BufferedReader(new FileReader(fileName));
							String line;
							while((line = reader.readLine()) != null) {	
								myPatternList.add(line);
							}
//							for(int i=0;i<myPatternList.size();i++) {
//								responseArea.append(myPatternList.get(i)+"\n");
//							}
						}
						choice=2;
						reader.close();
					}

					catch (IOException c) {
						c.printStackTrace();
						Object[] options = new String[] {"Load New File", "Exit"};
						choice = JOptionPane.showOptionDialog(null, "Invalid FileChoosen." + 
								"Would you like to load a new file " + "or exit?", "Options",
								JOptionPane.YES_NO_OPTION,JOptionPane.ERROR_MESSAGE,null, options,
								options[0]);
						if (choice == 1)
							System.exit(0);				
					}
				}
				else if(result== JFileChooser.CANCEL_OPTION) {
					Object[] options = new String[] { "Load Different File", "Exit" };
					choice = JOptionPane.showOptionDialog(null,"Would you like to load a new file "
							+ " or exit?", "Options",JOptionPane.YES_NO_OPTION,
							JOptionPane.ERROR_MESSAGE, null, options,
							options[0]);
					if (choice == 1)
						System.exit(0);
				}
			}while(choice==0);


		}
	}

	/*
	 * Implements the ActionListner for the Save JMenuItem.
	 */
	class ActionListenerSave implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("SaveAs");
			int choice=0;
			do {
				int result = chooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					file = chooser.getSelectedFile();
					try {
						if (file != null) {
							fileName = file.getCanonicalPath();
							printWriter = new PrintWriter(new FileOutputStream(fileName), true);
						}
						printWriter.append(responseArea.getText());
						choice=2;

					}

					catch (IOException c) {
						c.printStackTrace();
						Object[] options = new String[] {"Choose New File", "Exit"};
						choice = JOptionPane.showOptionDialog(null, "Invalid FileChoosen." + 
								"Would you like to choose a new file " + "or exit?", "Options",
								JOptionPane.YES_NO_OPTION,JOptionPane.ERROR_MESSAGE,null, options,
								options[0]);
						if (choice == 1)
							System.exit(0);				
					}
				}
				else if(result== JFileChooser.CANCEL_OPTION) {
					Object[] options = new String[] { "Choose Different File", "Exit" };
					choice = JOptionPane.showOptionDialog(null,"Would you like to choose a new file "
							+ " or exit?", "Options",JOptionPane.YES_NO_OPTION,
							JOptionPane.ERROR_MESSAGE, null, options,
							options[0]);
					if (choice == 1)
						System.exit(0);
				}
			}while(choice==0);
			printWriter.flush();
			printWriter.close();

		}
	}


	//TODO
	/*
	 * Implements the ActionListener for the JTextField.
	 */
	class ActionListenerField implements ActionListener{
		public void actionPerformed(ActionEvent e) {
		
			question = inputField.getText();
			eliza=new Eliza(myPatternList,question);
			inputField.setText("");
			responseArea.append("I: "+question+"\n");
			responseArea.append("Eliza: "+eliza.generateAnswer()+"\n");
			//responseArea.append(question+"\n");
			//responseArea.append(eliza.generateAnswer()+"\n");

		}
	}

}



