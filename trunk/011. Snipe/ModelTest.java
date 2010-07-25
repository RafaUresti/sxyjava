import java.util.ArrayList;
import junit.framework.TestCase;

public class ModelTest extends TestCase {
	
	Model model = new Model();

	public void testCreateArrayOfBalls() {
		model.createArrayOfBalls(200);
		ArrayList<Ball> ballList = model.getBalls();
		assertTrue(ballList.size() == 8);
	}

	public void testMakeOneStep() {
		model.createArrayOfBalls(200);
		ArrayList<Ball> ballList = model.getBalls();
		Ball ball = ballList.get(1);
		int x1 = ball.getCoordinateX();
		int y1 = ball.getCoordinateY();
		int deltaX = ball.getDeltaX();
		int deltaY = ball.getDeltaY();
		model.makeOneStep(ball);
		int x2 = ball.getCoordinateX();
		int y2 = ball.getCoordinateY();
		assertTrue (x2 == x1 + deltaX);
		assertTrue (y2 == y1 + deltaY);
	}

	public void testGameIsOver() {
		model.createArrayOfBalls(500);
		for (Ball ball : model.balls)
			ball.setFixed(true);
		assertTrue(model.gameIsOver());
	}
}
