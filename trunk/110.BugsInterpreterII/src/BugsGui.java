import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.Timer;

import parser.*;
import tree.*;

/**
 * The GUI controller of Bugs Language Interpreter.
 * Load Bugs program file (txt) from File->Load...
 * @author Xiaoyi Sheng
 * @author David Matuszek
 * @version 4-27-2008
 */
public class BugsGui extends JFrame {

	JSlider speedControl;
	JButton stepButton;
	JButton runButton;
	JButton pauseButton;
	JButton resetButton;
	String programDescription; //stores the bug program from text file
	Tree<Token> programTree;//stores the bug program parsed from text file
	Parser parser;
	Timer timer;
	final int WIDTH = 600, HEIGHT = 600;
	private Interpreter interpreter;
	private View view;
	public BugsGui() {
		super();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(WIDTH, HEIGHT);
		setLayout(new BorderLayout());
		createAndInstallMenus();
		createControlPanel();
		initializeButtons();
		setVisible(true);
	}

	private void createAndInstallMenus() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");        
		JMenu helpMenu = new JMenu("Help");
		JMenuItem loadMenuItem = new JMenuItem("Load...");
		JMenuItem quitMenuItem = new JMenuItem("Quit");
		JMenuItem aboutMenuItem = new JMenuItem("About");
		menuBar.add(fileMenu);
		fileMenu.add(loadMenuItem);
		fileMenu.add(quitMenuItem);

		quitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				quit();
			}});
		FileChooseListener fileChooseListener = new FileChooseListener();
		loadMenuItem.addActionListener(fileChooseListener);
		menuBar.add(helpMenu);
		helpMenu.add(aboutMenuItem);
		aboutMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				help();
			}});

		this.setJMenuBar(menuBar);
	}

	class FileChooseListener implements ActionListener{
		public void actionPerformed(ActionEvent arg0){
			JFileChooser fileChooser = new JFileChooser();
			FileNameExtensionFilter fileFilter = new FileNameExtensionFilter(
					"Bug program", "txt");
			fileChooser.setFileFilter(fileFilter);
			int result = fileChooser.showOpenDialog(view);
			if (result == JFileChooser.APPROVE_OPTION){
				File file = fileChooser.getSelectedFile();
				if (file != null) {
					String fileName;
					FileReader fileReader;
					BufferedReader reader;
					String line;
					try {
						fileName = file.getCanonicalPath();
						fileReader = new FileReader(fileName);
						reader = new BufferedReader(fileReader);
						programDescription = "";
						while((line = reader.readLine()) != null) {	
							programDescription += line+"\n";
						}
						reader.close( );
						parser = new Parser(programDescription);
						if (parser.isProgram()){
							runButton.setEnabled(true);
							stepButton.setEnabled(true);
							pauseButton.setEnabled(false);
							resetButton.setEnabled(false);
							speedControl.setEnabled(true);
							programTree = parser.stack.peek();
							start();
						}
						else {
							showError("Invalid Bugs Program!");
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (RuntimeException e){
						e.printStackTrace();
						showError(e.getMessage());
					}
				}
			}
		}
	}

	private void start() {
		interpreter = new Interpreter(programTree);
		view = new View(interpreter);
		createViewPanel();
		view.setSize(WIDTH-10, HEIGHT - 110);
		if (timer != null)
			timer.stop();//stops the timer at the beginning of a new file
		try{
			interpreter.run();
		}catch (Exception e){
			e.printStackTrace();
			showError(e.getMessage());
		}
	}
	
	private void createViewPanel() {
		add(view, BorderLayout.CENTER);
	}


	private void createControlPanel() {
		JPanel controlPanel = new JPanel();

		addSpeedLabel(controlPanel);       
		addSpeedControl(controlPanel);
		addStepButton(controlPanel);
		addRunButton(controlPanel);
		addPauseButton(controlPanel);
		addResetButton(controlPanel);
		add(controlPanel, BorderLayout.SOUTH);
	}

	private void addSpeedLabel(JPanel controlPanel) {
		controlPanel.add(new JLabel("Speed:"));
	}

	private void addSpeedControl(JPanel controlPanel) {
		speedControl = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 50);
		speedControl.setMajorTickSpacing(10);
		speedControl.setMinorTickSpacing(5);
		speedControl.setPaintTicks(true);
		speedControl.setPaintLabels(true);
		speedControl.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				resetSpeed(speedControl.getValue());
			}
		});
		controlPanel.add(speedControl);
	}

	private void addStepButton(JPanel controlPanel) {
		stepButton = new JButton("Step");
		stepButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stepAnimation();
			}
		});
		controlPanel.add(stepButton);
	}

	private void addRunButton(JPanel controlPanel) {
		runButton = new JButton("Run");
		runButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				runAnimation();
			}
		});
		controlPanel.add(runButton);
	}

	private void addPauseButton(JPanel controlPanel) {
		pauseButton = new JButton("Pause");
		pauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pauseAnimation();
			}
		});
		controlPanel.add(pauseButton);
	}

	private void addResetButton(JPanel controlPanel) {
		resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetAnimation();
			}
		});
		controlPanel.add(resetButton);
	}

	private void initializeButtons() {
		speedControl.setEnabled(false);
		stepButton.setEnabled(false);
		runButton.setEnabled(false);
		pauseButton.setEnabled(false);
		resetButton.setEnabled(false);
	}

	private void resetSpeed(int value) {
		interpreter.setSpeed(value);
	}

	protected void stepAnimation() {
		if (timer != null){
			timer.stop();
			timer.setRepeats(false);
			timer.start();
		}
		else startNewTimer();
		timer.setRepeats(false);
		stepButton.setEnabled(true);
		runButton.setEnabled(true);
		pauseButton.setEnabled(false);
		resetButton.setEnabled(true);
	}

	protected void runAnimation() {

		stepButton.setEnabled(true);
		runButton.setEnabled(false);
		pauseButton.setEnabled(true);
		resetButton.setEnabled(true);
		startNewTimer();
		timer.setRepeats(true);
	}

	private void startNewTimer() {
		timer = new Timer(25, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (interpreter.deadBugs.size()< interpreter.bugs.size()){
						view.repaint();
					interpreter.unblockAllBugs();
				}
			}
		});

		timer.start();
	}

	/**
	 * Pops up an error window showing the error message
	 * @param message The error message
	 */
	private void showError(String message) {
		JOptionPane.showMessageDialog(this, message,"Bug Program Error",0);
	}

	protected void pauseAnimation() {
		timer.stop();
		stepButton.setEnabled(true);
		runButton.setEnabled(true);
		pauseButton.setEnabled(false);
		resetButton.setEnabled(true);

	}
	
	protected void resetAnimation() {
		timer.stop();
		start();
		stepButton.setEnabled(true);
		runButton.setEnabled(true);
		pauseButton.setEnabled(false);
		resetButton.setEnabled(false);
	}

	protected void help() {
		JOptionPane.showMessageDialog(view, "Author: Xiaoyi Sheng\n" +
				"Author: David Matuszek\n" +
				"http://www.cis.upenn.edu/~matuszek/" +
		"cit594-2008/Assignments/10-interpreter-2.html.");
	}

	protected void quit() {
		System.exit(0);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new BugsGui();
	}
}
