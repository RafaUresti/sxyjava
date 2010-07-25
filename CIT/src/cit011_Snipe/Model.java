package cit011_Snipe;
import java.util.ArrayList;
import java.util.Observable;
import java.awt.*;
/**
 * Creates the balls and provides method to make movements of the balls
 * assesses if a ball is hit by the player ball and if so, stops the ball.
 * Provides the method to check if game is over
 * (whether all balls have been hit)
 */
class Model extends Observable {
	public ArrayList<Ball> balls;
	private int PANEL_SIZE;
	public Ball playerBall;

	/**
	 * Creates all the player ball and 7 other balls
	 * @param PANEL_SIZE the size (width or length) of the panel to contain the balls
	 */
	public void createArrayOfBalls(final int PANEL_SIZE) {
		this.PANEL_SIZE = PANEL_SIZE;
		balls = new ArrayList<Ball>();
		Ball playerBall = new Ball("playerBall", PANEL_SIZE, PANEL_SIZE);
		balls.add(playerBall);
		Ball toAdd = new Ball(PANEL_SIZE, PANEL_SIZE, Color.RED);
		balls.add(toAdd);
		toAdd = new Ball(PANEL_SIZE, PANEL_SIZE, Color.GREEN);
		balls.add(toAdd);
		toAdd = new Ball(PANEL_SIZE, PANEL_SIZE, Color.BLUE);
		balls.add(toAdd);
		toAdd = new Ball(PANEL_SIZE, PANEL_SIZE, Color.GRAY);
		balls.add(toAdd);
		toAdd = new Ball(PANEL_SIZE, PANEL_SIZE, Color.PINK);
		balls.add(toAdd);
		toAdd = new Ball(PANEL_SIZE, PANEL_SIZE, Color.YELLOW);
		balls.add(toAdd);
		toAdd = new Ball(PANEL_SIZE, PANEL_SIZE, Color.MAGENTA);
		balls.add(toAdd);
	}

	/**
	 * make one movement for a ball
	 * @param ball the ball to move
	 */
	public void makeOneStep(Ball ball){
		if(!ball.getFixed()){
			if((ball.getCoordinateX() + ball.getDeltaX()) <=0){
				ball.setDeltaX(-ball.getDeltaX());
			}
			if((ball.getCoordinateX() + ball.getDeltaX()) >= PANEL_SIZE){
				ball.setDeltaX(-ball.getDeltaX());
			}
			ball.setCoordinateX(ball.getCoordinateX() + ball.getDeltaX());

			if((ball.getCoordinateY() + ball.getDeltaY()) <=0){
				ball.setDeltaY(-ball.getDeltaY());
			}
			if((ball.getCoordinateY() + ball.getDeltaY()) >= PANEL_SIZE){
				ball.setDeltaY(-ball.getDeltaY());
			}
			ball.setCoordinateY(ball.getCoordinateY() + ball.getDeltaY());

			assessHit();
		}

		// Notify observers
		setChanged();
		notifyObservers();
	}

	/**
	 * checks if a ball is hit by the player ball
	 */
	public void assessHit (){

		for (int i = 1; i <balls.size(); i++){
			double distanceX = Math.abs(balls.get(0).getCoordinateX() - balls.get(i).getCoordinateX());
			double distanceY = Math.abs(balls.get(0).getCoordinateY() - balls.get(i).getCoordinateY());
			double distance = Math.sqrt(distanceX*distanceX + distanceY*distanceY);
			if ( distance < Ball.BALL_SIZE)
				balls.get(i).setFixed(true);
		}
		return;
	}
	
	/**
	 * getter method for the List of balls
	 * @return List of the balls
	 */
	public ArrayList<Ball> getBalls(){
		return balls;
	}
	
	/**
	 * Check if the game is over (whether all balls are hit)
	 * @return true if the game is over, false otherwise
	 */
	public boolean gameIsOver() {
		for (Ball ball : balls)
			if (!ball.getFixed())
				return false;
		return true;
	}
}
