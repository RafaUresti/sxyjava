import java.util.*;
/**
 * The Model Class: Backend for Adventure game
 * @author Mai Irie
 * @author sxycode
 */

public class Model {

	/* Things in the game */
	private Gold gold1, gold2, gold3;
	private Key key;
	private Hammer hammer;
	private Barrel waterBarrel;
	private Room northRoom, eastRoom, southRoom, westRoom, centerRoom, hiddenRoom;
	private Adventurer player;
	
	/**
	 * sets up the whole game
	 */
	public Model() { 
		northRoom = new Room("Kitchen", "No food available at this time...");
		eastRoom = new Room("Bedroom", "Sweet dreams every night.");
		southRoom = new Room("Bathroom", "I get total relief here.");
		westRoom = new Room("Studyroom", "I think, therefore I am.");
		centerRoom = new Room ("Living room", "Welcome to my house!");
		hiddenRoom = new Room ("Treasure room", "Here is all my savings.");
		connectRooms();
		player = new Adventurer(northRoom);
		createThings();
		addThingsToRooms();
	}
	
	/**
	 * creates all the items needed for the rooms
	 */
	private void createThings() {
		gold1 = new Gold("Gold", "This is a small piece of Gold");
		gold2 = new Gold("Gold", "This is a small piece of Gold");
		gold3 = new Gold("Gold", "This is a large piece of Gold");

		key	= new Key("Skeleton Key", "This unlocks the Skeleton Door");
		hammer = new Hammer("Rusty Hammer", "You can use this Hammer to break a water Barrel");
		waterBarrel = new Barrel("Water Barrel", "This barrel is full of water. Perhaps there could be something of use inside...", key);

	}

	/**
	 * connects the rooms
	 */
	private void connectRooms() {
		// 0, 1, 2, 3 corresponds to directions
		// N, E, S, W
		centerRoom.connectRooms(northRoom, 0, false); 
		centerRoom.connectRooms(eastRoom, 1, false); 
		centerRoom.connectRooms(southRoom, 2, false); 
		centerRoom.connectRooms(westRoom, 3, false); 
		eastRoom.connectRooms(hiddenRoom, 2, true);
	}
	
	/**
	 * add items to the rooms
	 */
	private void addThingsToRooms() {
		westRoom.addThingToRoom(gold1);
		southRoom.addThingToRoom(gold2);
		hiddenRoom.addThingToRoom(gold3);
		centerRoom.addThingToRoom(waterBarrel);
		eastRoom.addThingToRoom(hammer);
	}
	
	/**
	 * check if the game is over (all three pieces of gold are collected)
	 * @return if the game is over
	 */
	public boolean gameOver() {
		ArrayList<Thing> playerInventory = player.getInventory();
		if (playerInventory.contains(gold1) && playerInventory.contains(gold2) && playerInventory.contains(gold3))
			return true;
		return false;
	}
	
	/**
	 * getter method of the player
	 * @return the player
	 */
	public Adventurer getPlayer() {
		return player;
	}
	
}
