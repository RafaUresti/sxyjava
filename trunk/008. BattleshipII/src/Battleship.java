/**
 * Assignment 8 Battleship II for CIT591, Fall 2007.
 * @author Dave Matuszek
 * @author sxycode
 * @author Adam Rothblatt
 * @version November 8, 2007
 */

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

public class Battleship extends JFrame{
	private Ocean ocean;
	private JPanel oceanPanel = new JPanel();
	private JPanel topBar = new JPanel();
	private JPanel shipBar = new JPanel();
	private JPanel rightBar = new JPanel();
	private JLabel gameStatusLabel = new JLabel();
	private JButton[][] oceanBoxes = new JButton[10][10];
	private JLabel numberOfHitsLabel;
	private JLabel numberOfShotsLabel;
	private Color oceanColor = new Color(0, 0, 128);
	private JRadioButton battleship;
	private JRadioButton cruisers;
	private JRadioButton destroyers;
	private JRadioButton submarines;
	private JRadioButton removeShip = new JRadioButton("Remove Ship");
	private JRadioButton verticalRB = new JRadioButton("Vertical", true);
	private JRadioButton horizontalRB = new JRadioButton("Horizontal");
	private ButtonGroup shipGroup = new ButtonGroup();
	private String shipToPlace = " ";
	private int battleshipNumber;
	private int cruiserNumber;
	private int destroyerNumber;
	private int submarineNumber;
	private boolean isHorizontal;
	
	/**
	 * Main Method creates new GUI
	 * @param args
	 */
	public static void main(String[] args) {
		new Battleship().newGame();
	}
	
	/**
	 * Creates a new game with interface to place ships.
	 * @param args
	 */
	private void newGame() {
		ocean = new Ocean();
		createPlaceShipsGui();
	}
	
	/**
	 * Creates a new game with randomly-generated ships. Skips place ship interface.
	 */	
	private void newRandomGame() {
		ocean = new Ocean();
		createPlayGameGui();
		ocean.placeAllShipsRandomly();
	}
	
	/**
	 * Builds the play game interface to begin playing the game.
	 */
	private void startGame() {
		createPlayGameGui();	
	}
	
	/**
	 * Displays dialogue that game is over.
	 */
	public void endGame() {
		displayMessage("Game Over. You have sunk the entire enemy fleet!");
	}

	/**
	 * Creates the GUI for the user to place ships.
	 */
	public void createPlaceShipsGui() {
		setTitle("Welcome to Battleship!");
		setBounds(200,200,600,500);
		add(topBar(), BorderLayout.NORTH);
		add(oceanCreaterPanel(), BorderLayout.CENTER);
		add(rightBar(), BorderLayout.EAST);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setVisible(true);
	}

	/**
	 * Creates the GUI to play the game.
	 */
	public void createPlayGameGui() {
		setTitle("Welcome to Battleship!");
		setBounds(200,200,600,500);

		add(gamePlayTopBar(), BorderLayout.NORTH);
		add(gamePlayOceanPanel(), BorderLayout.CENTER);
		add(gamePlayRightBar(),  BorderLayout.EAST);
		
		setVisible(true);
	}	

	/**
	 * Creates a JPanel containing the ocean for the place ships GUI.
	 * @return JPanel containing the ocean.
	 */
	public JPanel oceanCreaterPanel() {
		oceanPanel.removeAll();
		Border oceanBorder = BorderFactory.createEtchedBorder();
		oceanBorder = BorderFactory.createTitledBorder(oceanBorder, "The Ocean");
		oceanPanel.setBorder(oceanBorder);
		oceanPanel.setLayout(new GridLayout(10, 10));

		ActionListener oceanCreaterListener = new OceanCreaterListener();
		Color oceanColor = new Color(0, 0, 128);
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				JButton oceanBox = new JButton();
				oceanBox.setBackground(oceanColor);
				oceanBox.addActionListener(oceanCreaterListener);
				oceanBoxes[i][j] = oceanBox;
				oceanPanel.add(oceanBoxes[i][j]);
			}
		}
		return oceanPanel;
	}
	
	/**
	 * Creates a JPanel containing the ocean for the play game GUI.
	 * @return JPanel containing the ocean.
	 */
	public JPanel gamePlayOceanPanel() {
		oceanPanel.removeAll();
		Border oceanBorder = BorderFactory.createEtchedBorder();
		oceanBorder = BorderFactory.createTitledBorder(oceanBorder, "The Ocean");
		oceanPanel.setBorder(oceanBorder);
		oceanPanel.setLayout(new GridLayout(10, 10));
		
		ActionListener oceanListener = new OceanListener();
		
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				JButton oceanBox = new JButton();
				oceanBox.setBackground(oceanColor);
				oceanBox.addActionListener(oceanListener);
				oceanBoxes[i][j] = oceanBox;
				oceanPanel.add(oceanBoxes[i][j]);
			}
		}
		return oceanPanel;
	}
	
	/**
	 * Creates the right command bar for the place ships GUI
	 * @return
	 */	
	public JPanel rightBar() {
		rightBar.removeAll();
		Border commandBorder = BorderFactory.createEtchedBorder();
		commandBorder = BorderFactory.createTitledBorder(commandBorder, "Commands:");
		rightBar.setLayout(new GridLayout(2,1));
		JLabel instructions = new JLabel();
		instructions.setText("<html>Choose the ship" +
				"<BR>and orientation.<BR>"+
				"Select the<BR>top-left location<BR>" +
		"of the ship<BR>in the ocean.</html>");
		instructions.setFont(new Font("Serif", Font.PLAIN, 14));

		Border instructionBorder = BorderFactory.createEtchedBorder();
		instructionBorder = BorderFactory.createTitledBorder(instructionBorder, "Instructions");
		instructions.setBorder(instructionBorder);

		JPanel commands = new JPanel();
		commands.setLayout(new GridLayout(2,1));
		JButton random = new JButton("Random");
		ActionListener randomListener = new MyRandomListener();
		random.addActionListener(randomListener);
		JButton play = new JButton("Play!");
		ActionListener playButtonListener = new PlayButtonListener();
		play.addActionListener(playButtonListener);
		commands.add(random);
		commands.add(play);
		commands.setBorder(commandBorder);

		rightBar.add(commands);
		rightBar.add(instructions);

		return rightBar;
	}

	/**
	 * Creates the right command bar for the play game GUI
	 * @return
	 */	
	public JPanel gamePlayRightBar() {
		rightBar.removeAll();
		Border commandBorder = BorderFactory.createEtchedBorder();
		commandBorder = BorderFactory.createTitledBorder(commandBorder, "Commands:");
		rightBar.setLayout(new GridLayout(2,1));
		JLabel instructions = new JLabel();
		instructions.setText("<html>Click on" +
				"<BR>an ocean<BR>"+
				"square to<BR>shoot!<BR></html>");
		instructions.setFont(new Font("Serif", Font.PLAIN, 14));

		
		Border instructionBorder = BorderFactory.createEtchedBorder();
		instructionBorder = BorderFactory.createTitledBorder(instructionBorder, "Instructions");
		instructions.setBorder(instructionBorder);
		
		JPanel commands = new JPanel();
		commands.setLayout(new GridLayout(2,1));
		
		JButton play = new JButton("Play Again");
		play.setName("PlayAgain");
		ActionListener playButtonListener = new PlayAgainButtonListener();
		play.addActionListener(playButtonListener);
		commands.add(play);
		
		JButton quickPlay = new JButton("Quick Play");
		quickPlay.setName("QuickPlay");
		quickPlay.addActionListener(playButtonListener);
		commands.add(quickPlay);
		
		commands.setBorder(commandBorder);
		
		rightBar.add(commands);
		rightBar.add(instructions);
		
		return rightBar;		
	}

	/**
	 * creates the top command/status bar for the place ships GUI
	 * @return JPanel containing the command/status bar
	 */
	public JPanel topBar() {
		topBar.removeAll();
		topBar.add(shipBar());
		topBar.add(removeShip);
		removeShip.addItemListener(new MyShipListener());
		return topBar;
	}

	/**
	 * Creates the menu bar of ships to place for the place ships GUI
	 * @return JPanel status bar
	 */
	public JPanel shipBar() {
		shipBar.removeAll();
		shipBar.setLayout(new GridLayout(2,1));
		JPanel shipToPlacePanel = new JPanel();

		battleshipNumber = 1;
		cruiserNumber = 2;
		destroyerNumber = 3;
		submarineNumber = 4;
		battleship = new JRadioButton("Battleship ("+ battleshipNumber +")");
		cruisers = new JRadioButton("Cruisers ("+ cruiserNumber+")");
		destroyers = new JRadioButton("Destroyers ("+destroyerNumber +")");
		submarines = new JRadioButton("Submarines ("+ submarineNumber+")");

		Border shipBorder = BorderFactory.createEtchedBorder();
		shipBorder = BorderFactory.createTitledBorder(shipBorder, "Ships Available:");
		shipGroup.add(battleship);
		shipGroup.add(cruisers);
		shipGroup.add(destroyers);
		shipGroup.add(submarines);
		shipGroup.add(removeShip);

		shipBar.setBorder(shipBorder);

		battleship.addItemListener(new MyShipListener());
		cruisers.addItemListener(new MyShipListener());
		destroyers.addItemListener(new MyShipListener());
		submarines.addItemListener(new MyShipListener());

		shipToPlacePanel.add(battleship);
		shipToPlacePanel.add(cruisers);
		shipToPlacePanel.add(destroyers);
		shipToPlacePanel.add(submarines);
		shipToPlacePanel.add(removeShip);
		shipBar.add(shipToPlacePanel);
		shipBar.add(orientation());
		return shipBar;
	}

	/**
	 * Creates the game status/scores section on the top of the play game GUI
	 * @return JPanel containing the game status/scores bar
	 */
	public JPanel gamePlayTopBar() {
		topBar.removeAll();
		numberOfShotsLabel = new JLabel();
		numberOfShotsLabel.setText("Number Shots: 0                     ");
		topBar.add(numberOfShotsLabel);
		
		gameStatusLabel = new JLabel();		
		topBar.add(gameStatusLabel);
		
		numberOfHitsLabel = new JLabel();		
		numberOfHitsLabel.setText("                               Number Hits: 0");
		topBar.add(numberOfHitsLabel);	
		
		return topBar;
	}

	/**
	 * Creates the orientation panel for the place new ships GUI
	 * @return JPanel containing the orientation panel
	 */
	public JPanel orientation() {
		JPanel orientation = new JPanel();
		orientation.setLayout(new FlowLayout(FlowLayout.CENTER));
		JLabel shipOrientation = new JLabel("Orientation: ");

		orientation.add(shipOrientation);
		orientation.add(verticalRB);
		orientation.add(horizontalRB);
		verticalRB.addItemListener(new OrientationListener());
		horizontalRB.addItemListener(new OrientationListener());

		ButtonGroup orientationGroup = new ButtonGroup();
		orientationGroup.add(verticalRB);
		orientationGroup.add(horizontalRB);
		return orientation;
	}

	/**
	 * Registers a successful hit onto the GUI
	 * @param row row of the box clicked by user
	 * @param column column of the box clicked by user
	 */
	private void markHit(int row, int column) {
		JButton oceanBox = oceanBoxes[row][column];
		oceanBox.setBackground(Color.red);
		gameStatusLabel.setForeground(Color.red);
		gameStatusLabel.setText("                  HIT!                  ");
	}

	/**
	 * Registers a miss onto the GUI
	 * @param row row of the box clicked by user
	 * @param column column of the box clicked by user
	 */
	private void markMiss(int row, int column) {
		JButton oceanBox = oceanBoxes[row][column];
		oceanBox.setBackground(Color.lightGray);
		gameStatusLabel.setForeground(Color.black);
		gameStatusLabel.setText("          You missed!          ");
	}

	/**
	 * Marks a location of a ship in the GUI
	 * @param row row of the ship location
	 * @param column column of the ship location
	 */
	private void markShip(int row, int column) {
		JButton oceanBox = oceanBoxes[row][column];
		oceanBox.setBackground(Color.black);
	}
	
	/**
	 * Clears the specified box, resets GUI to ocean color.
	 * @param row row of specified box
	 * @param column column of specified box
	 */
	private void clearBox(int row, int column) {
		JButton oceanBox = oceanBoxes[row][column];
		oceanBox.setBackground(oceanColor);	
	}

	/**
	 * Marks the location of all ships for place ships GUI
	 */
	private void markAllShips() {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (ocean.isOccupied(i, j)) {
					markShip(i, j);
				}
			}
		}
	}
	
	/**
	 * Clears the entire ocean panel in the GUI, sets color to ocean color.
	 */
	private void clearOceanPanel() {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				clearBox(i, j);
			}
		}
	}
	
	/**
	 * Refreshes the ocean panel display on GUI.
	 */
	private void refreshOceanPanel() {
		clearOceanPanel();
		markAllShips();		
	}

	/**
	 * Listener for ocean boxes. Identifies which ocean location was clicked.
	 *
	 */
	class OceanListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			JButton oceanBox = (JButton) e.getSource();
			int row = getOceanBoxRow(oceanBox);
			int column = getOceanBoxColumn(oceanBox);
			shootAt(row, column);
		}
	}
	
	/**
	 * Registers a shot made by user, passes to Ocean class.
	 * @param row row specified by user.
	 * @param column column specified by user.
	 */
	public void shootAt(int row, int column) {
		
		if (ocean.isGameOver()) {
			endGame();
		}
		else {
			boolean hitTarget = ocean.shootAt(row, column);

			if (hitTarget) {
				markHit(row, column);
			}			
			else {
				markMiss(row, column);
			}
			
			numberOfHitsLabel.setText("Number of Hits: "+Integer.toString(ocean.numberOfHits())+"                    ");
			numberOfShotsLabel.setText("                    Number of Shots: "+Integer.toString(ocean.numberOfShotsFired()));
			
			if (ocean.isGameOver()) {
				endGame();
			}
			
		}	
	}    

	/**
	 * Listener for play button in place ships GUI. Validates ship placement 
	 * and starts game. 
	 */
	class PlayButtonListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			if (battleshipNumber == 0 && cruiserNumber == 0 
				&& destroyerNumber == 0 && submarineNumber == 0)
			startGame();
			else displayMessage("You must first place all of your ships.");
		}
	}	
	
	/**
	 * Listener for Random button in place ships GUI.
	 *
	 */
	class MyRandomListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			newGame();
			ocean.placeAllShipsRandomly();
			refreshOceanPanel();
			battleshipNumber = 0;
			cruiserNumber = 0;
			destroyerNumber = 0;
			submarineNumber = 0;
			battleship.setText("Battleship ("+ battleshipNumber +")");
			cruisers.setText("Cruisers ("+ cruiserNumber+")");
			destroyers.setText("Destroyers ("+destroyerNumber +")");
			submarines.setText("Submarines ("+ submarineNumber+")");
			battleship.setEnabled(false);
			cruisers.setEnabled(false);
			destroyers.setEnabled(false);
			submarines.setEnabled(false);
		}
	}	
	
	/**
	 * Listener for Play Again button in play game GUI.
	 */	
	class PlayAgainButtonListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			JButton playButton = (JButton) e.getSource();
			
	    	if (ocean.isGameOver() && playButton.getName()=="PlayAgain") {
	    		newGame();
	    	}
	    	else if (ocean.isGameOver() && playButton.getName()=="QuickPlay") {
	    		newRandomGame();
	    	}
	    	else {
	    		boolean confirm = confirmBox("Confirm Restart", "Are you sure you want to restart the game?", "Yes, Restart", "No, Resume");
	    	
	        	if (confirm==true && playButton.getName()=="PlayAgain") {
	        		newGame();
	        	} 
	        	else if (confirm==true && playButton.getName()=="QuickPlay")  {
	        		newRandomGame();
	        	}
	    	
	    	}			
		}
	}	

	/**
	 * retrieves row of ocean box clicked by user.
	 * @param oceanBox ocean box clicked by user.
	 * @return row of box clicked by user.
	 */
	private int getOceanBoxRow(JButton oceanBox) {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (oceanBox == oceanBoxes[i][j]) {
					return i;
				}
			}
		}
		return 0;
	}

	/**
	 * retrieves column of ocean box clicked by user.
	 * @param oceanBox ocean box clicked by user.
	 * @return column of box clicked by user.
	 */
	private int getOceanBoxColumn(JButton oceanBox) {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (oceanBox == oceanBoxes[i][j]) {
					return j;
				}
			}
		}
		return 0;
	}

	/**
	 * Displays an option dialog window with specified options.
	 * @param title Title of option dialog window.
	 * @param question Text of question to be asked.
	 * @param option1 Text of "Yes" option.
	 * @param option2 Text of "No" option.
	 * @return boolean true/false based on Yes/No user input.
	 */
    private boolean confirmBox(String title, String question, String option1, String option2) {
    	
	   	String[] options = new String[] {option1, option2};
    	int option = JOptionPane.showOptionDialog(oceanPanel, 
    		question,
            title,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE, 
            null,                         
            options,
            options[0]);
    	if (option==0) return true;
    	else return false;
    }

    /**
     * Displays a message dialog window with specified message text.
     * @param message specified message text.
     */
	public void displayMessage(String message) {
		JOptionPane.showMessageDialog(oceanPanel, message);
	}

	/**
	 * Listener for the ship radio buttons in place ship GUI.
	 */
	public class MyShipListener implements ItemListener{
		public void itemStateChanged(ItemEvent arg0) {
			if (arg0.getSource() == battleship)
				shipToPlace = "Battleship";
			if (arg0.getSource() == cruisers)
				shipToPlace = "Cruiser";
			if (arg0.getSource() == destroyers)
				shipToPlace = "Destroyer";
			if (arg0.getSource() == submarines)
				shipToPlace = "Submarine";
			if (arg0.getSource() == removeShip)
				shipToPlace = "Remove";
		}
	}
	
	/**
	 * Listener for Horizontal/Vertical orientation radio buttons in place ships GUI.
	 */
	public class OrientationListener implements ItemListener{
		public void itemStateChanged(ItemEvent arg0) {
			if (verticalRB.isSelected())
				isHorizontal = false;
			if (horizontalRB.isSelected()) {
				isHorizontal = true;
			}
		}
	}
	
	/**
	 * Listener for placing ships in the ocean in the place ships GUI. Validates
	 * ship placement.
	 */
	public class OceanCreaterListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			JButton oceanBox = (JButton) e.getSource();
			int row = getOceanBoxRow(oceanBox);
			int column = getOceanBoxColumn(oceanBox);
			if (shipToPlace == " ")
				displayMessage("Please select an available ship.");
			if (shipToPlace == "Battleship")
				if (ocean.okToPlaceBattleshipAt(row, column, isHorizontal)) {
					ocean.placeBattleshipAt(row, column, isHorizontal);
					battleshipNumber--;	
					battleship.setText("Battleship ("+ battleshipNumber +")");
					if (battleshipNumber <= 0) {
						battleship.setEnabled(false);
						shipToPlace = " ";
					}
					else battleship.setEnabled(true);
				}

				else displayMessage("You can't place a Battleship here");
			if (shipToPlace == "Cruiser")
				if (ocean.okToPlaceCruiserAt(row, column, isHorizontal)) {
					ocean.placeCruiserAt(row, column, isHorizontal);
					cruiserNumber--;
					cruisers.setText("Cruisers ("+ cruiserNumber+")");
					if (cruiserNumber <= 0) {
						cruisers.setEnabled(false);
						shipToPlace = " ";
					}
					else cruisers.setEnabled(true);
				}

				else displayMessage("You can't place a Cruiser here");
			if (shipToPlace == "Destroyer") 
				if (ocean.okToPlaceDestroyerAt(row, column, isHorizontal)) {
					ocean.placeDestroyerAt(row, column, isHorizontal);
					destroyerNumber--;
					destroyers.setText("Destroyers ("+destroyerNumber +")");
					if (destroyerNumber <= 0) {
						destroyers.setEnabled(false);
						shipToPlace = " ";
					}
					else destroyers.setEnabled(true);
				}

				else displayMessage("You can't place a Destroyer here");
			if (shipToPlace == "Submarine")
				if (ocean.okToPlaceSubmarineAt(row, column)) {
					ocean.placeSubmarineAt(row, column);
					submarineNumber--;
					submarines.setText("Submarines ("+ submarineNumber+")");
					if (submarineNumber <= 0) {
						submarines.setEnabled(false);
						shipToPlace = " ";
					}

					else submarines.setEnabled(true);
				}

				else displayMessage("You can't place a Submarine here");
			if (shipToPlace == "Remove" && ocean.isOccupied(row, column)) {
				String removedShip = ocean.removeShipAt(row, column);
				if ("Battleship".equals(removedShip)) {
					battleshipNumber++;
					battleship.setText("Battleship ("+ battleshipNumber +")");
					battleship.setEnabled(true);
				}
				if ("Cruiser".equals(removedShip)) {
					cruiserNumber++;
					cruisers.setText("Cruisers ("+ cruiserNumber+")");
					cruisers.setEnabled(true);
				}
				if ("Destroyer".equals(removedShip)) {
					destroyerNumber++;
					destroyers.setText("Destroyers ("+destroyerNumber +")");
					destroyers.setEnabled(true);
				}
				if ("Submarine".equals(removedShip)) {
					submarineNumber++;
					submarines.setText("Submarines ("+ submarineNumber+")");
					submarines.setEnabled(true);
				}
			}
			refreshOceanPanel();
		}
	}
}

