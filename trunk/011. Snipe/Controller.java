/**
 * Assignment 11 Snipe for CIT591, Fall 2007.
 * @author Dave Matuszek
 * @author sxycode
 * @author Suva Shrestha
 * @version Dec 6th, 2007
 */

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;


/**
 * The controller of the game. Enable the user to control the movement of the ball
 *
 */
public class Controller extends JApplet {
	float clock =0;
	private final int  REFRESH_TIME = 50;
	public static final int PANEL_SIZE = 500;
	JPanel panel1 = new JPanel(new FlowLayout());
	JPanel panel2 = new JPanel(new BorderLayout());
	public static final int EXTRA_WIDTH = 40;
	public static final int EXTRA_LENGTH = 75;
	private final int BALL_SPEED = 20;
	JButton playKey = new JButton("Play");
	JButton pauseKey = new JButton("Pause");
	JButton resumeKey = new JButton("Resume");
	JButton quitKey = new JButton("Quit");
	Timer timer;

	Model model = new Model();
	View view = new View(model, this);

	/**
	 * Getter method for time display
	 * @return time display
	 */
	public int getClock() {
		return (int)clock;
	}
	
	/**
	 * Listeners to the buttons: play, pause, resume and quit
	 */
	private void attachListenersToComponents() {
		playKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if(clock==0)	
				{timer = new Timer(true);
				timer.schedule(new Strobe(), 0, REFRESH_TIME);
				}
				pauseKey.setEnabled(true);
				playKey.setEnabled(false);
			}

		});
		pauseKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				resumeKey.setEnabled(true);
				pauseKey.setEnabled(false);
				model.balls.get(0).setFixed(true);
				timer.cancel();
			}
		});
		resumeKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				timer = new Timer(true);
				timer.schedule(new Strobe(), 0, REFRESH_TIME);
				pauseKey.setEnabled(true);
				playKey.setEnabled(false);
				resumeKey.setEnabled(false);
				model.balls.get(0).setFixed(false);
			}
		});
		quitKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// TODO define actions
				System.exit(0);
			}
		});
	}

	/**
	 * Listener for keyboard input of W/I for moving the ball up,
	 * A/J left, S/K down, D/L right
	 */
	class ControlKeys implements KeyListener {
		@Override
		public void keyReleased(KeyEvent e) {	
		}
		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (!model.balls.get(0).getFixed()) {
				int id = e.getKeyCode();
				if (id == KeyEvent.VK_W || id == KeyEvent.VK_I) {
					if (model.balls.get(0).getDeltaY() > -BALL_SPEED)
						model.balls.get(0).setDeltaY(model.balls.get(0).getDeltaY() - 1);
				}
				if (id == KeyEvent.VK_A || id == KeyEvent.VK_J) {
					if (model.balls.get(0).getDeltaX()> -BALL_SPEED)
						model.balls.get(0).setDeltaX(model.balls.get(0).getDeltaX() - 1);
				}
				if (id == KeyEvent.VK_S || id == KeyEvent.VK_K) {
					if (model.balls.get(0).getDeltaY() < BALL_SPEED)
						model.balls.get(0).setDeltaY(model.balls.get(0).getDeltaY() + 1);
				}
				if (id == KeyEvent.VK_D || id == KeyEvent.VK_L) {
					if (model.balls.get(0).getDeltaX() < BALL_SPEED)
						model.balls.get(0).setDeltaX(model.balls.get(0).getDeltaX() + 1);
				}
			}	
		}
	}

	/**
	 * initializes the game by putting the GUI layout, observer and listener together
	 */
	public void init() {
		this.setSize(PANEL_SIZE+EXTRA_WIDTH, PANEL_SIZE+EXTRA_LENGTH);
		model.createArrayOfBalls(PANEL_SIZE);
		layOutComponents();
		attachListenersToComponents();
		// Connect model and view
		model.addObserver(view);
		this.addKeyListener( new ControlKeys());
		this.setFocusable(true);
		playKey.setFocusable(false);
		pauseKey.setFocusable(false);
		resumeKey.setFocusable(false);
		quitKey.setFocusable(false);
	}

	/**
	 * Layouts the GUI components
	 */
	private void layOutComponents() {
		setLayout(new BorderLayout());
		this.add(BorderLayout.SOUTH, panel1);
		panel1.add(playKey);
		panel1.add(pauseKey);
		panel1.add(resumeKey);
		panel1.add(quitKey);
		playKey.setEnabled(true);
		pauseKey.setEnabled(false);
		resumeKey.setEnabled(false);
		this.add(BorderLayout.CENTER, panel2);
		panel2.setSize(PANEL_SIZE,PANEL_SIZE);
		panel2.add(BorderLayout.CENTER, view);
	}

	/**
	 * Moves the balls around in the view and refreshes the time display,
	 * also checks if the game is over
	 *
	 */
	private class Strobe extends TimerTask {
		public void run() {
			int counter=0;
			for(int i=0;i<model.balls.size();i++){
				if(model.balls.get(i).getFixed()==true)counter++;
				model.makeOneStep(model.balls.get(i));
			}
			if(counter==(model.balls.size()-1) || counter==(model.balls.size())){
				model.balls.get(0).setFixed(true);
			}
			else{clock += (REFRESH_TIME/1000.0);}
			if (model.gameIsOver()) {
				JOptionPane.showMessageDialog(null, "Congratulations! \nYou hit all balls in "+ (int)clock+ " seconds!");
				System.exit(0);
			}
		}
	}
}
