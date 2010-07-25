

/**
 * Adventure Game Class
 * The class that handles the entire game
 * @author Mai Irie
 * @author Xiaoyi Sheng
 * @version November 16, 2007
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import java.util.*;

public class AdventureGame extends JFrame {

	private Adventurer player;
	private Model model;
	private AdventurerPanel jpAdventurePanel;
	private JPanel room;
	private JPanel things;
	private JPanel roomDescriptionPanel = new JPanel();
	private JTextArea roomDescription;
	private JTextArea itemList;
	private JPanel itemListPanel = new JPanel();
	private ArrayList<Thing> actualRoomObjects;
	private JComboBox roomItemChoice;
	private JPanel itemActions;
	private JButton lookAtItem = new JButton("Look at item");
	private JButton pickUpItem = new JButton("Pick up item");
	private Border roomBorder;
	private Border itemListBorder;
	private Border itemsBorder;


	private JPanel jpFeedback;
	private Border borderFeedback;
	private JTextArea jtArearesults;

	/* GUI Inventory Control */
	private JPanel jpInventoryControl;
	private Border borderInventory;
	private JComboBox playerInventory;
	private ArrayList<Thing> actualInventoryObjects;
	private JButton jbtnLook;
	private JButton jbtnUse;
	private JButton jbtnDrop;

	/* GUI Movement Control */
	private JPanel jpDirectionPanel;
	private Border borderDirection;
	private JButton jbtnNorth;
	private JButton jbtnEast;
	private JButton jbtnSouth;
	private JButton jbtnWest;

	private static AdventureGame game;

	private String roomItemList;
	private ArrayList <Thing> currentRoomContents;
	/**
	 * main method of the program
	 * @param args
	 */
	public static void main(String[] args) {
		game = new AdventureGame();
	}

	/**
	 * initializes the game and GUI
	 */
	public AdventureGame() {
		setTitle("Adventure Game");
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		model = new Model();
		player =  model.getPlayer();

		actualInventoryObjects = new ArrayList<Thing>();
		actualRoomObjects = new ArrayList<Thing>();		

		setLayout(new GridLayout(1,2));
		JPanel jpPlayerPanel = new RoomPanel();
		this.add(jpPlayerPanel);	
		jpAdventurePanel = new AdventurerPanel();	
		this.add(jpAdventurePanel);

		this.pack();
		centerWindow(this);
		refreshAll();
		setVisible(true);
	}

	/**
	 * Determines the placement of the window relative to the screen size
	 * @param w The current window to be placed
	 */
	private void centerWindow(Window w) {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();
		setLocation((d.width - w.getWidth())/2, (d.height - w.getHeight())/2);
	}

	/**
	 * Inner Class: PlayerPanel (left half of GUI)
	 */
	class RoomPanel extends JPanel {
		/**
		 * initializes the room panel (left half of GUI)
		 */
		public RoomPanel(){
			this.setLayout(new GridLayout(2,1));
			initRoom();
			initThings();
			drawBorders();
			this.add(room);
			this.add(things);
		}

		/**
		 * initializes the panel including room description
		 * and item list of the room
		 * @return the room panel
		 */
		private JPanel initRoom() {
			room = new JPanel();
			room.setLayout(new FlowLayout());
			room.add(roomDescriptionPanel);
			roomDescription = new JTextArea("This is a room", 15,15);
			roomDescription.setEditable(false);
			roomDescription.setLineWrap(true);
			roomDescription.setWrapStyleWord(true);
			roomDescriptionPanel.add(roomDescription);
			itemList = new JTextArea("", 15, 5);
			itemListPanel.add(itemList);
			itemList.setLineWrap(true);
			itemList.setWrapStyleWord(true);
			itemList.setEditable(false);
			room.add(itemListPanel);
			return room;
		}

		/**
		 * Initializes the panel for operations on
		 * things in the room
		 * @return the thing operation panel
		 */
		private JPanel initThings() {
			things = new JPanel();
			JPanel emptyPanel1 = new JPanel();
			JPanel emptyPanel2 = new JPanel();
			//things.setLayout(new GridLayout(1,2));
			roomItemChoice  = new JComboBox();
			things.add(roomItemChoice);
			roomItemChoice.addActionListener(new RoomItemListener());
			itemActions = new JPanel();
			itemActions.setLayout(new GridLayout(3,1));
			itemActions.add(lookAtItem);
			lookAtItem.addActionListener(new RoomItemListener());
			itemActions.add(emptyPanel1);
			itemActions.add(pickUpItem);
			pickUpItem.addActionListener(new RoomItemListener());
			things.add(emptyPanel2);
			things.add(itemActions);
			return things;
		}

		/**
		 * Draws borders for the components for the room panel. (left half of GUI)
		 */
		private void drawBorders()  {
			Font f = new Font("Sans-serif", Font.BOLD, 14);

			roomBorder = BorderFactory.createEtchedBorder();
			roomBorder = BorderFactory.createTitledBorder(roomBorder, "Room Information"/*, TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, f, Color.BLACK*/);
			roomDescriptionPanel.setBorder(roomBorder);

			itemListBorder = BorderFactory.createEtchedBorder();
			itemListBorder = BorderFactory.createTitledBorder(itemListBorder, "Items", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, f, Color.BLACK);
			itemListPanel.setBorder(itemListBorder);


			itemsBorder = BorderFactory.createEtchedBorder();
			itemsBorder = BorderFactory.createTitledBorder(itemsBorder, "Items Available:", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, f, Color.BLACK);
			things.setBorder(itemsBorder);
		}
	}

	/**
	 * Inner Class: Adventure Panel
	 */
	class AdventurerPanel extends JPanel {
		/**
		 * Constructor for the main panel for the AdventurerPanel
		 * Lays out content and ensures all JComboBoxes and JButtons
		 * have appropriate listeners.
		 */
		public AdventurerPanel() {
			setLayout(new GridLayout(3,1));
			initFeedback();
			initInventory();
			initDirections();
			drawBorders();
			add(jpFeedback);
			add(jpInventoryControl);
			add(jpDirectionPanel);
		}

		/**
		 * Sets up the Feedback panel
		 */
		private void initFeedback() {
			jpFeedback = new JPanel();
			jtArearesults = new JTextArea("Your inventory is currently empty", 8,30);
			jtArearesults.setEditable(false);
			jpFeedback.add(jtArearesults);
			jtArearesults.setLineWrap(true);
			jtArearesults.setWrapStyleWord(true);
		}

		/**
		 * Sets up the Inventory panel and adds appropriate listeners
		 * to the following buttons: Look, Use, Drop
		 */
		private void initInventory() {
			jpInventoryControl = new JPanel(new GridLayout(1,2));
			JPanel jpInventory = new JPanel(new GridLayout(2,1));
			playerInventory = new JComboBox();
			playerInventory.addActionListener(new ItemListener());
			JPanel jpComboPanel = new JPanel();
			jpComboPanel.add(playerInventory);
			JPanel jpEmpty  = new JPanel();//empty JPanel to help align other components
			jpInventory.add(jpComboPanel);
			jpInventory.add(jpEmpty);
			jpInventoryControl.add(jpInventory);
			JPanel jpInventoryActions = new JPanel(new GridLayout(3,1));
			jbtnLook = new JButton("Look at Item");
			jbtnLook.addActionListener(new ItemListener());
			JPanel jpBtnLook = new JPanel();
			jpBtnLook.add(jbtnLook);
			jbtnUse  = new JButton("Use Item");
			jbtnUse.addActionListener(new ItemListener());
			JPanel jpBtnUse = new JPanel();
			jpBtnUse.add(jbtnUse);
			jbtnDrop = new JButton("Drop Item");
			jbtnDrop.addActionListener(new ItemListener());
			JPanel jpBtnDrop = new JPanel();
			jpBtnDrop.add(jbtnDrop);
			jpInventoryActions.add(jpBtnLook);
			jpInventoryActions.add(jpBtnUse);
			jpInventoryActions.add(jpBtnDrop);
			jpInventoryControl.add(jpInventoryActions);
		}

		/**
		 * Setup Direction Panel
		 * Add appropriate listeners to the following buttons:
		 * North, East, South, West
		 */
		private void initDirections() {
			jpDirectionPanel = new JPanel(new GridLayout(3,3));
			JPanel empty1 = new JPanel(); // Want to arrange the buttons in normal compass arrangement
			JPanel empty2 = new JPanel(); // So therefore using empty panels as spacers
			JPanel empty3 = new JPanel();
			JPanel empty4 = new JPanel();
			JPanel empty5 = new JPanel();

			jbtnNorth = new JButton("North");
			jbtnNorth.addActionListener(new DirectionListener());
			JPanel jpNorth = new JPanel();
			jpNorth.add(jbtnNorth);

			jbtnEast = new JButton("East");
			jbtnEast.addActionListener(new DirectionListener());
			JPanel jpEast = new JPanel();
			jpEast.add(jbtnEast);

			jbtnSouth = new JButton("South");
			jbtnSouth.addActionListener(new DirectionListener());
			JPanel jpSouth = new JPanel();
			jpSouth.add(jbtnSouth);

			jbtnWest = new JButton("West");
			jbtnWest.addActionListener(new DirectionListener());
			JPanel jpWest = new JPanel();
			jpWest.add(jbtnWest);

			jpDirectionPanel.add(empty1);
			jpDirectionPanel.add(jpNorth);
			jpDirectionPanel.add(empty2);
			jpDirectionPanel.add(jpWest);
			jpDirectionPanel.add(empty3);
			jpDirectionPanel.add(jpEast);
			jpDirectionPanel.add(empty4);
			jpDirectionPanel.add(jpSouth);
			jpDirectionPanel.add(empty5);
		}

		/**
		 * Makes etched titled borders for the components in AdventurePanel
		 */
		private void drawBorders()  {
			Font f = new Font("Sans-serif", Font.BOLD, 14);

			borderFeedback = BorderFactory.createEtchedBorder();
			borderFeedback = BorderFactory.createTitledBorder(borderFeedback, "Results of your Actions", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, f, Color.BLACK);
			jpFeedback.setBorder(borderFeedback);

			borderInventory = BorderFactory.createEtchedBorder();
			borderInventory = BorderFactory.createTitledBorder(borderInventory, "Your Inventory", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, f, Color.BLACK);
			jpInventoryControl.setBorder(borderInventory);

			borderDirection = BorderFactory.createEtchedBorder();
			borderDirection = BorderFactory.createTitledBorder(borderDirection, "Direction(s) you can Move:", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, f, Color.BLACK);
			jpDirectionPanel.setBorder(borderDirection);
		}
	}
	/**
	 * Updates the contents of all components in the GUI
	 */
	public void refreshAll() {
		refreshRoomInfo();
		refreshDoors();
		refreshRoomContents();
		refreshPlayerContents();
	}

	/**
	 * Updates the room description
	 */
	public void refreshRoomInfo() {
		Room currentLocation = player.getLocation();
		String roomText = currentLocation.getName() + "\n" + currentLocation.getDescription();
		roomDescription.setText(roomText);
	}

	/**
	 * Updates the door button availability 
	 * when moving among rooms
	 */
	public void refreshDoors() { 
		Room currentLocation = player.getLocation();
		int [] currentLocationDoors = currentLocation.getDoors();
		if (currentLocationDoors[0] == -1)
			jbtnNorth.setEnabled(false);
		else jbtnNorth.setEnabled(true);
		if (currentLocationDoors[1] == -1)
			jbtnEast.setEnabled(false);
		else jbtnEast.setEnabled(true);
		if (currentLocationDoors[2] == -1)
			jbtnSouth.setEnabled(false);
		else jbtnSouth.setEnabled(true);
		if (currentLocationDoors[3] == -1)
			jbtnWest.setEnabled(false);
		else jbtnWest.setEnabled(true);
	}

	/**
	 *Updates the current room contents combo box
	 *as well as the item list for the room 
	 */
	public void refreshRoomContents() {
		Room currentLocation = player.getLocation();
		currentRoomContents = currentLocation.getRoomContents();
		roomItemChoice.removeAllItems();
		actualRoomObjects = new ArrayList<Thing>();
		roomItemList = "";
		for (Thing toAdd: currentRoomContents) {
			roomItemChoice.addItem(toAdd.getName());
			actualRoomObjects.add(toAdd);
			roomItemList += toAdd.getName() + "\n";
		}
		itemList.setText(roomItemList);
	}

	/**
	 * Finds the corresponding item in the room
	 * according to the name
	 * @param name the name of the item
	 * @return the item bearing the name
	 */
	public Thing findRoomObject(String name) {
		for (Thing item: actualRoomObjects) {
			if (item.getName().equals(name))
				return item;
		}
		return null;
	}

	/**
	 * Updates the player inventory combo box to reflect
	 * what the player is carrying
	 */
	public void refreshPlayerContents(){
		ArrayList <Thing> inventory = player.getInventory();
		playerInventory.removeAllItems();
		actualInventoryObjects = new ArrayList<Thing>();
		for (Thing toAdd: inventory) {
			actualInventoryObjects.add(toAdd);
			playerInventory.addItem(toAdd.getName());
		}
	}

	/**
	 * Finds the corresponding item in the player inventory
	 * according to the name
	 * @param name the name of the item
	 * @return the item bearing the name
	 */
	public Thing findPlayerObject(String name) {
		for (Thing item: actualInventoryObjects) {
			if (item.getName().equals(name))
				return item;
		}
		return null;
	}
	/**
	 * Listener for the operations on items the player carries.
	 */
	class ItemListener implements ActionListener {

		/**
		 * Handles the actions for all events in AdventurePanel
		 */
		public void actionPerformed(ActionEvent e) {			
			if (e.getSource() == jbtnLook) {
				if (! player.carryingItems()) {
					jtArearesults.setText("You don't have any items to look at!");

				} else { // update player feedback
					String nameOfItem = (String)(playerInventory.getSelectedItem());
					Thing item = findPlayerObject(nameOfItem);
					String lookingAt = player.lookAt(item);
					jtArearesults.setText(lookingAt);
				}
			} else if (e.getSource() == jbtnUse) {
				if (!player.carryingItems()) {
					jtArearesults.setText("You don't have any items to use!");
				} else {
					String nameOfItem = (String)(playerInventory.getSelectedItem());
					Thing item = findPlayerObject(nameOfItem);
					if (player.use(item)) {
						String results = "Successful: You used " + item.getName() +". "+ item.effect();
						jtArearesults.setText(results);
						refreshAll();
					} else {
						String results = "Failure: You could not use " + item.getName();
						jtArearesults.setText(results);
					}
				}
			} else if (e.getSource() == jbtnDrop) {
				if (! player.carryingItems()) {
					jtArearesults.setText("You don't have any items to drop!");
				} else {
					String nameOfItem = (String)(playerInventory.getSelectedItem());
					Thing item = findPlayerObject(nameOfItem);
					player.drop(item);
					jtArearesults.setText("You have dropped "+ item.getName());
					player.getLocation().addThingToRoom(item);
					refreshAll();
					refreshPlayerContents();
				}
			}
		}
	}

	/**
	 *	Listener for the direction buttons
	 */
	class DirectionListener implements ActionListener {
		/**
		 * Moves player to different directions according to
		 * direction buttons pushed
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == jbtnNorth) {
				if (player.move(player.getLocation().getDirectionRoom(0)))
					refreshAll();
				else 
					jtArearesults.setText("The room is locked.");
			} else if (e.getSource() == jbtnEast) {
				if (player.move(player.getLocation().getDirectionRoom(1)))
					refreshAll();
				else jtArearesults.setText("The room is locked.");
			} else if (e.getSource() == jbtnSouth) {
				if (player.move(player.getLocation().getDirectionRoom(2)))
					refreshAll();
				else 
					jtArearesults.setText("The room is locked.");
			} else if (e.getSource() == jbtnWest) {
				if (player.move(player.getLocation().getDirectionRoom(3)))
					refreshAll();
				else
					jtArearesults.setText("The room is locked.");
			} 
		}
	}

	/**
	 *	Listener for the operations on the items in the room
	 */
	class RoomItemListener implements ActionListener {

		/**
		 * Handles the operations on the items in the room
		 */
		public void actionPerformed(ActionEvent e) {				
			Room location = player.getLocation();
			if (e.getSource() == lookAtItem) {
				if (location.getRoomContents().isEmpty())
					jtArearesults.setText("There is nothing in the room");
				else {
					String nameOfItem = (String)(roomItemChoice.getSelectedItem());
					Thing item = findRoomObject(nameOfItem);
					jtArearesults.setText(player.lookAt(item));
				}
			}
			if (e.getSource() == pickUpItem) {
				if (location.getRoomContents().isEmpty())
					jtArearesults.setText("There is nothing in the room");
				else {
					String nameOfItem = (String)(roomItemChoice.getSelectedItem());
					Thing item = findRoomObject(nameOfItem);
					if (player.pickUp(item)) {
						jtArearesults.setText("You have picked up the " + item.getName());
						refreshRoomContents();
						refreshRoomInfo();
						refreshPlayerContents();
						if (model.gameOver())
							JOptionPane.showMessageDialog(game, "Congratulations!\nYou have achieved the goal!");
					}
					else jtArearesults.setText("You can't pick up the "+ item.getName());
				}
			}
		}
	}
}