package BouncingBall;
import java.util.Observable;

class Model extends Observable {
    public final int BALL_SIZE = 20;
    private int xPosition = 0;
    private int yPosition = 0;
    private int xLimit, yLimit;
    private int xDelta = 6;
    private int yDelta = 4;

    public void setLimits(int xLimit, int yLimit) {
        this.xLimit = xLimit - BALL_SIZE;
        this.yLimit = yLimit - BALL_SIZE;
    }
    
    public int getX() {
        return xPosition;
    }
    
    public int getY() {
        return yPosition;
    }
    
    public void makeOneStep() {
        // Do the work
        xPosition += xDelta;
        if (xPosition < 0 || xPosition >= xLimit) {
            xDelta = -xDelta;
            xPosition += xDelta;
        }
        yPosition += yDelta;
        if (yPosition < 0 || yPosition >= yLimit) {
            yDelta = -yDelta;
            yPosition += yDelta;
        }
        // Notify observers
        setChanged();
        notifyObservers();
    }
}
