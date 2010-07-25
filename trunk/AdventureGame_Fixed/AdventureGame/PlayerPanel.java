import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.event.*;

import java.awt.*;
public class PlayerPanel extends JPanel implements ActionListener {
	private JPanel room;
	private JPanel things;
	private JPanel roomDescriptionPanel = new JPanel();
	private JTextArea roomDescription;
	private JTextArea itemList;
	private JPanel itemListPanel = new JPanel();
	private JComboBox itemChoice;
	private JPanel itemActions;
	private JButton lookAtItem = new JButton("Look at item");
	private JButton pickUpItem = new JButton("Pick up item");
	private Border roomBorder;
	private Border itemListBorder;
	private Border itemsBorder;
	
	public PlayerPanel(){
		this.setLayout(new GridLayout(2,1));
		initRoom();
		initThings();
		drawBorders();
		this.add(room);
		this.add(things);
	}
	
	private JPanel initRoom() {
		room = new JPanel();
		room.setLayout(new FlowLayout());
		room.add(roomDescriptionPanel);
		
		roomDescription = new JTextArea("This is a room", 15,15);
		roomDescription.setEditable(false);
		roomDescription.setLineWrap(true);//XXX
		roomDescriptionPanel.add(roomDescription);
		itemList = new JTextArea("These\nare\nthe\nitems\nin\nthe\nroom", 15, 5);
		//room.add(roomDescription);
		itemListPanel.add(itemList);
		itemList.setLineWrap(true);
		itemList.setEditable(false);
		room.add(itemListPanel);
		return room;
	}
	
	private JPanel initThings() {
		things = new JPanel();
		JPanel emptyPanel1 = new JPanel();
		JPanel emptyPanel2 = new JPanel();
		//things.setLayout(new GridLayout(1,2));
		itemChoice = new JComboBox();
		things.add(itemChoice);
		itemChoice.addItem("Item One");
		itemChoice.addItem("Item two");
		itemChoice.addItem("Item Three");
		itemActions = new JPanel();
		itemActions.setLayout(new GridLayout(3,1));
		itemActions.add(lookAtItem);
		itemActions.add(emptyPanel1);
		itemActions.add(pickUpItem);
		things.add(emptyPanel2);
		things.add(itemActions);
		return things;
	}
	private void drawBorders()  {
		Font f = new Font("Sans-serif", Font.BOLD, 14);

		roomBorder = BorderFactory.createEtchedBorder();
		roomBorder = BorderFactory.createTitledBorder(roomBorder, "Name of Room"/*, TitledBorder.DEFAULT_JUSTIFICATION,
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
	public void actionPerformed(ActionEvent e) {
		
	}
}
