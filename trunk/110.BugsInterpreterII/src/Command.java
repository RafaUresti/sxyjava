import java.awt.Color;

/**
 * The class for constructing command objects to be
 * painted on the View
 * @author Xiaoyi Sheng
 *
 */
public class Command {
	Color color;
	double x1, y1, x2, y2;
	public Command(Color color, double x1, double y1, double x2, double y2){
		this.color = color;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
}
