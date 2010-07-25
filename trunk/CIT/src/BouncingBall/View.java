package BouncingBall;
import java.awt.*;
import java.util.*;

class View extends Panel implements Observer {
    Model model;

    View(Model model) {
        this.model = model;
    }
    
    public void paint(Graphics g) {
        g.setColor(Color.red);
        g.fillOval(model.getX(), model.getY(),
                   model.BALL_SIZE, model.BALL_SIZE);
    }
    
    public void update(Observable obs, Object arg) {
        repaint();
    }
}
    