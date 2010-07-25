package cis551proj4.log.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.util.Collections;

import javax.swing.*;

public class LogViewerPanel extends JFrame{
	
	public final static long serialVersionUID = 2356543256543L;
	private JPanel basePanel = new JPanel(); 
	private JScrollPane scrollPane = new JScrollPane();
	private JTextArea textArea = new JTextArea();
	
	private LogViewer parent = null;
	
	private final int HEIGHT = 600;
	private final int WIDTH = 800;
	
	public LogViewerPanel(LogViewer viewer){
		
		// Set up the Graphics Window
		super("Secure Banking System Log Viewer");
		
		this.parent = viewer;
		
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
		
		textArea.setSize(WIDTH, HEIGHT);
		textArea.setEditable(false);
		textArea.setText("To view a log, open a key file, then open a log file using the 'File' menu");
		
		scrollPane.setSize(WIDTH, HEIGHT);
		scrollPane.getViewport().add(textArea);
		
		basePanel.setLayout(new BorderLayout());
		basePanel.add(scrollPane, BorderLayout.CENTER);
		
		// Add the menu bar
		this.setJMenuBar(new LogMenu(viewer));
		this.add(basePanel);
		
		this.setSize(WIDTH, HEIGHT);
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		this.setMaximumSize(new Dimension(WIDTH, HEIGHT));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				Collections.EMPTY_SET);
		this.pack();
	}

	public JTextArea getTextArea() {
		return textArea;
	}

	public void setTextArea(JTextArea textArea) {
		this.textArea = textArea;
	}
}
