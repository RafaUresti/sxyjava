import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BattleshipGui extends JFrame {
	
	JPanel oceanBoard = new JPanel();
	JPanel topBar = new JPanel();
	JPanel rightBar = new JPanel();
	JButton[][] oceanBoxes = new JButton[10][10];
	Battleship game;

	/**
	 * @param args
	 */
	
	public BattleshipGui(Battleship game) {
		createGui();
		this.game = game;
	}

	/** Creates a new Ocean object, initialize the game by calling the relevant methods, 
	 * displays the score at the end of the game, 
	 * asks the user if he/she wants to play another game 
	 */

	public void createGui() {

		setTitle("Battleship");
		setBounds(250, 250, 350, 350);
		// topBar.add(new JButton("battleships"));

		topBar.setSize(300, 100);
		add(topBar, BorderLayout.NORTH);
		add(rightBar, BorderLayout.EAST);

		oceanBoard.setSize(200, 200);
		oceanBoard.setLayout(new GridLayout(10, 10));
		ActionListener oceanListener = new OceanListener();
		add(oceanBoard, BorderLayout.CENTER);

		Color oceanColor = new Color(0, 0, 128);

		// initialize the ocean and displays

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				JButton oceanBox = new JButton();
				oceanBox.setBackground(oceanColor);
				oceanBox.addActionListener(oceanListener);
				oceanBoxes[i][j] = oceanBox;
				oceanBoard.add(oceanBoxes[i][j]);
			}
		}

		setVisible(true);
	}

	public void markHit(int row, int column) {
		JButton oceanBox = oceanBoxes[row][column];
		oceanBox.setBackground(Color.red);
	}

	public void markMiss(int row, int column) {
		JButton oceanBox = oceanBoxes[row][column];
		oceanBox.setBackground(Color.white);
	}

	public void markShip(int row, int column) {
		JButton oceanBox = oceanBoxes[row][column];
		oceanBox.setBackground(Color.black);
	}

	class OceanListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			JButton oceanBox = (JButton) e.getSource();
			int row = getOceanBoxRow(oceanBox);
			int column = getOceanBoxColumn(oceanBox);
			game.shootAt(row, column);
		}
	}

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

}