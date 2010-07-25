package cit011_Snipe;
import java.util.Random;
import java.awt.*;

/**
 * Class to create balls
 *
 */
public class Ball {

	private String name;
	private int coordinateX;
	private int coordinateY;
	private int deltaX;
	private int deltaY;
	private Color color;
	private boolean fixed;
	public final static int BALL_SIZE = 20;
	// this value will be used when assigning colors to balls
	static final int COLOR_RANGE = 100;
	static int targetBallCount = 0;

/**
 * Generate the player ball
 * @param name 
 * @param dimensionX
 * @param dimensionY
 */
	public Ball(String name, int dimensionX, int dimensionY) {
		this.name = name;
		this.coordinateX = dimensionX/2;
		this.coordinateY = dimensionY/2;
		this.deltaX = 0;
		this.deltaY = 0;
		this.color = Color.BLACK;
	}

	/**
	 * Generate target ball
	 * 
	 * @param name the name of the target ball
	 * @param coordinateX the x axis position of the target ball
	 * @param coordinateY the y axis position of the target ball
	 * @param deltaX pixels to move on the x axis for the ball for each frame
	 * @param deltaY pixels to move on the x axis for the ball for each frame
	 * @param color the color of the target ball
	 */
	public Ball(int dimensionX, int dimensionY, Color color) {
		this.name = "targetBall" + targetBallCount++;
		this.coordinateX = generateRandomNumber(dimensionX); 
		this.coordinateY = generateRandomNumber(dimensionY); 
		this.deltaX = generateRandomNumber(12) - 6; //choose an x speed in range -5..+5
		this.deltaY = generateRandomNumber(12) - 6; //choose an y speed in range -5..+5
		this.color = color;
	}

	/**
	 * Random number generator for generic responses.
	 * 
	 * @param range the upper limit of the random number
	 * @return random number the random number
	 */
	private int generateRandomNumber(int range) {
		Random random = new Random();
		return random.nextInt(range);
	}

	/**
	 * Getter for the name of the ball
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter the name of the ball
	 * @param name the name of the ball
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter for the x axis position of the ball
	 * @return the x axis position of the ball
	 */
	public int getCoordinateX() {
		return coordinateX;
	}

	/**
	 * Getter for the y axis position of the ball
	 * @return the y axis position of the ball
	 */
	public void setCoordinateX(int coordinateX) {
		this.coordinateX = coordinateX;
	}

	/**
	 * Setter for the x axis position of the ball
	 * @return
	 */
	public int getCoordinateY() {
		return coordinateY;
	}

	/**
	 * Setter for the y axis position of the ball
	 * @param coordinateY
	 */
	public void setCoordinateY(int coordinateY) {
		this.coordinateY = coordinateY;
	}

	/**
	 * Getter for deltaX
	 * @return pixels to move on the x axis for the ball for each frame
	 */
	public int getDeltaX() {
		return deltaX;
	}

	/**
	 * Setter for deltaX
	 * @param pixels to move on the x axis for the ball for each frame
	 */
	public void setDeltaX(int deltaX) {
		this.deltaX = deltaX;
	}

	/**
	 * Getter for deltaY
	 * @return pixels to move on the y axis for the ball for each frame
	 */
	public int getDeltaY() {
		return deltaY;
	}

	/**
	 * Setter for deltaY
	 * @param deltaY pixels to move on the y axis for the ball for each frame
	 */
	public void setDeltaY(int deltaY) {
		this.deltaY = deltaY;
	}

	/**
	 * Getter for color of the ball
	 * @return color of the ball
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Setter for color of the ball
	 * @param color color of the ball
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	
	/**
	 * Set the ball to be fixed or not
	 * @param fixed if the ball is fixed
	 */
	public void setFixed(boolean fixed) {
		this.fixed=fixed;
	}
	
	/**
	 * Check if the ball is fixed
	 * @return true if the ball is fixed, false otherwise
	 */
	public boolean getFixed(){
		return this.fixed;
	}
	
}
