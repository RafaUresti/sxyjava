package cit011_Snipe;
import java.awt.*;
import java.util.*;

/**
 * Creates the view for the game
 * 
 */

class View extends Panel implements Observer {
	Model model;
	Controller controller;
	View(Model model, Controller controller) {
		this.model = model;
		this.controller = controller;
	}

	/**
	 * paints the balls and timer
	 */
	public void paint(Graphics g) {
		for (Ball ball : model.getBalls()) {
			g.setColor(ball.getColor());
			g.fillOval(ball.getCoordinateX(), ball.getCoordinateY(),
					Ball.BALL_SIZE, Ball.BALL_SIZE);
		}
		g.setColor(Color.black);
		g.drawString("Clock: "+controller.getClock(), Controller.PANEL_SIZE-30,20);
	}

	/**
	 * repaints the view
	 */
	public void update(Observable obs, Object arg) {
		repaint();
	}
}
