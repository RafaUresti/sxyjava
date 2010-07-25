import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;


/**
 * Adventure Game
 * @author Mai Irie
 * @author Xiaoyi Sheng
 *
 */

public class AdventurePanel extends JPanel implements ActionListener /*, ItemListener */ {

	/* TEMP MAIN TODO REMOVE!!!!! TESTING ONLY
	 */
	public static void main(String[] args) {
		JFrame f = new JFrame();
		AdventurePanel a = new AdventurePanel();
		f.add(a);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setResizable(false);
		f.pack();
	} // END OF TEST METHOD

	/* GUI Text Feedback Control */
	private JPanel jpFeedback;
	private Border borderFeedback;
	private JTextArea jtArearesults;
	
	/* GUI Inventory Control */
	private JPanel jpInventoryControl;
	private Border borderInventory;
	private JComboBox jComboinventory;
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
	
	/**
	 * Constructor for the main panel for the AdventurePanel
	 * Lays out content and ensures all JComboBoxes and JButtons
	 * have appropriate listeners.
	 */
	public AdventurePanel() {
		setLayout(new GridLayout(3,1));
//		setLayout(new FlowLayout(FlowLayout.CENTER));
		initFeedback();
		initInventory();
		initDirections();
		
		drawBorders();
		
		add(jpFeedback);
		
		add(jpFeedback);
		add(jpInventoryControl);
		add(jpDirectionPanel);
//		JPanel jpIntermediatePanel = new JPanel(new GridLayout(2,1));	
//		jpIntermediatePanel.add(jpInventoryControl);
//		jpIntermediatePanel.add(jpDirectionPanel);
		
//		add(jpIntermediatePanel);
	}
	
	/**
	 * Sets up the Feedback panel
	 */
	private void initFeedback() {
		jpFeedback = new JPanel();
		jtArearesults = new JTextArea("Your inventory is currently empty", 10, 5);
		jtArearesults.setEditable(false);
		jpFeedback.add(jtArearesults);
	}
	
	/**
	 * Sets up the Inventory panel and adds appropriate listeners
	 * to the following buttons: Look, Use, Drop
	 */
	private void initInventory() {
		jpInventoryControl = new JPanel(new GridLayout(1,2));
		
		JPanel jpInventory = new JPanel(new GridLayout(2,1));
		jComboinventory = new JComboBox(); //TODO CHANGE THIS TO TAKE IN THE STATIC OBJECT[] VAR OF ITEMS
//		jComboinventory.addItemListener(this);
		jComboinventory.addActionListener(this);
		JPanel jpComboPanel = new JPanel();
		jpComboPanel.add(jComboinventory);
		jComboinventory.addItem("Temp Item1"); //TODO GET RID OF TEMP ITEMS
		jComboinventory.addItem("Temp Item2");
		
		JPanel jpEmpty  = new JPanel();

		jpInventory.add(jpComboPanel);
		jpInventory.add(jpEmpty);
		
		jpInventoryControl.add(jpInventory);
		
		JPanel jpInventoryActions = new JPanel(new GridLayout(3,1));
		
		jbtnLook = new JButton("Look at Item");
		jbtnLook.addActionListener(this);
		JPanel jpBtnLook = new JPanel();
		jpBtnLook.add(jbtnLook);
		
		jbtnUse  = new JButton("Use Item");
		jbtnUse.addActionListener(this);
		JPanel jpBtnUse = new JPanel();
		jpBtnUse.add(jbtnUse);
		
		jbtnDrop = new JButton("Drop Item");
		jbtnDrop.addActionListener(this);
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
		jbtnNorth.addActionListener(this);
		JPanel jpNorth = new JPanel();
		jpNorth.add(jbtnNorth);
		
		jbtnEast = new JButton("East");
		jbtnEast.addActionListener(this);
		JPanel jpEast = new JPanel();
		jpEast.add(jbtnEast);
		
		jbtnSouth = new JButton("South");
		jbtnSouth.addActionListener(this);
		JPanel jpSouth = new JPanel();
		jpSouth.add(jbtnSouth);
		
		jbtnWest = new JButton("West");
		jbtnWest.addActionListener(this);
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
	
//	/**
//	 * Handles action for the JComboBox for this panel
//	 */
//	public void itemStateChanged(ItemEvent e) { // CHECK IF THIS IS CORRECT LISTENER FOR 
//		// TODO Auto-generated method stub
//		System.out.println("ComboBox");
//		System.out.println("Item Selected: " + jComboinventory.getSelectedIndex());
//	}

	/**
	 * Handles the actions for all buttons in this panel
	 */
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == jbtnLook) {
			System.out.println("Look Button");
		} else if (e.getSource() == jbtnUse) {
			System.out.println("Use Button");
		} else if (e.getSource() == jbtnDrop) {
			System.out.println("Drop Button");
		} else if (e.getSource() == jbtnNorth) {
			System.out.println("North Button");
		} else if (e.getSource() == jbtnEast) {
			System.out.println("East Button");
		} else if (e.getSource() == jbtnSouth) {
			System.out.println("South Button");
		} else if (e.getSource() == jbtnWest) {
			System.out.println("West Button");
		} else {
			String selection = (String) jComboinventory.getSelectedItem();
			System.out.println("Selected Item = " + selection);
		}
	}
}
