package cit110_BugsInterpreterII;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.*;
/**
 * The view panel on the BugsGui showing the animation 
 * of the Bugs and the lines drawn.
 * @author sxycode
 * @author David Matuszek
 * @version 4-27-2008
 */

public class View extends JPanel{
	private Interpreter interpreter;

	/**
	 * Constructor for the View class
	 * @param interpreter the interpreter which is shown by the View
	 */
	public View(Interpreter interpreter){
		this.interpreter = interpreter;
	}

	/**
	 * Paints a triangle to represent this Bug.
	 * 
	 * @param g Where to paint this Bug.
	 */
	public synchronized void paint(Graphics g) {
		//clear the previous painting
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		//paint the bugs
		for (String bugName: interpreter.bugs.keySet()){
			Bug bug = interpreter.bugs.get(bugName);

			if (bug.color == null) break;
			g.setColor(bug.color);

			int x1 = (int) (scaleX(bug.x) + computeDeltaX(12, (int)bug.angle));
			int x2 = (int) (scaleX(bug.x) + computeDeltaX(6, (int)bug.angle - 135));
			int x3 = (int) (scaleX(bug.x) + computeDeltaX(6, (int)bug.angle + 135));

			int y1 = (int) (scaleY(bug.y) + computeDeltaY(12, (int)bug.angle));
			int y2 = (int) (scaleY(bug.y) + computeDeltaY(6, (int)bug.angle - 135));
			int y3 = (int) (scaleY(bug.y) + computeDeltaY(6, (int)bug.angle + 135));
			g.fillPolygon(new int[] { x1, x2, x3 }, new int[] { y1, y2, y3 }, 3);
		}
		for (int i = 0; i < interpreter.getCommands().size(); i++){
			Command command = interpreter.getCommands().get(i);
			if (command.color == null) break;
			g.setColor(command.color);
			int x1 = (int) (scaleX(command.x1));
			int x2 = (int) (scaleX(command.x2));
			int y1 = (int) (scaleY(command.y1));
			int y2 = (int) (scaleY(command.y2));
			g.drawLine(x1,y1,x2,y2);
		}

	}

	/**
	 * Changing the y scale to proportional to the
	 * height of the view
	 * @param y the y axis location
	 * @return the y axis location for the View
	 */
	private double scaleY(double y) {
		return y/100.0*getHeight();
	}

	/**
	 * Changing the x scale to proportional to the
	 * width of the view
	 * @param x the x axis location
	 * @return the x axis location for the View
	 */
	private double scaleX(double x) {
		return x/100.0*getWidth();
	}

	/**
	 * Computes how much to move to add to this Bug's x-coordinate,
	 * in order to displace the Bug by "distance" pixels in 
	 * direction "degrees".
	 * 
	 * @param distance The distance to move.
	 * @param degrees The direction in which to move.
	 * @return The amount to be added to the x-coordinate.
	 */
	private static double computeDeltaX(int distance, int degrees) {
		double radians = Math.toRadians(degrees);
		return distance * Math.cos(radians);
	}

	/**
	 * Computes how much to move to add to this Bug's y-coordinate,
	 * in order to displace the Bug by "distance" pixels in 
	 * direction "degrees.
	 * 
	 * @param distance The distance to move.
	 * @param degrees The direction in which to move.
	 * @return The amount to be added to the y-coordinate.
	 */
	private static double computeDeltaY(int distance, int degrees) {
		double radians = Math.toRadians(degrees);
		return distance * Math.sin(-radians);
	}

}
