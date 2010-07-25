import java.util.*;
/**
 * The Model Class: Backend for Adventure game
 * @author Mai Irie
 * @author Xiaoyi Sheng
 */

public class Model {

//	private enum Direction { NORTH, EAST, SOUTH, WEST };	
	
	/* Things in the game */
	private Gold gold1, gold2, gold3;
	private Key key;
	private Hammer hammer;
	private Barrel waterBarrel;
	private Room northRoom, eastRoom, southRoom, westRoom, centerRoom, hiddenRoom;
	
	/* Adventurer */
	Adventurer player;
	
	// Chingo Bling Tamale

	public Model() { 
		northRoom = new Room("Kitchen", "This is the Kitchen");
		eastRoom = new Room("Bedroom", "My lovely bedroom");
		southRoom = new Room("Bathroom", "I get total relief here");
		westRoom = new Room("Studyroom", "I think, therefore I am");
		centerRoom = new Room ("Living room", "Welcome to my house");
		hiddenRoom = new Room ("Treasure room", "You are not supposed to be here if you were not me");
		connectRooms();
		player = new Adventurer(northRoom);
		createThings();
		addThingsToRooms();
	}
	
	private void createThings() {
		gold1 = new Gold("Gold", "This is a small piece of Gold");
		gold2 = new Gold("Gold", "This is a small piece of Gold");
		gold3 = new Gold("Gold", "This is a large piece of Gold");

		key	= new Key("Skeleton Key", "This unlocks the Skeleton Door");
		
		hammer = new Hammer("Rusty Hammer", "You can use this Hammer to break a water Barrel");
		
		
		waterBarrel = new Barrel("Water Barrel", "This barrel is full of water. Perhaps there could be something of use inside...", key);
	}
	private void connectRooms() {
		// 0, 1, 2, 3
		// N, E, S, W
		centerRoom.connectRooms(northRoom, 0, false); 
		centerRoom.connectRooms(eastRoom, 1, false); 
		centerRoom.connectRooms(southRoom, 2, false); 
		centerRoom.connectRooms(westRoom, 3, false); 
		eastRoom.connectRooms(hiddenRoom, 2, true);
	}
	private void addThingsToRooms() {
		westRoom.addThingToRoom(gold1);
		southRoom.addThingToRoom(gold2);
		hiddenRoom.addThingToRoom(gold3);
		centerRoom.addThingToRoom(waterBarrel);
		eastRoom.addThingToRoom(hammer);
	}
	
	public boolean gameOver() {
		ArrayList<Thing> playerInventory = player.getInventory();
		if (playerInventory.contains(gold1) && playerInventory.contains(gold2) && playerInventory.contains(gold3))
			return true;
		return false;
	}
	
	public Adventurer getPlayer() {
		return player;
	}
	
}
