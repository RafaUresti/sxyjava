import java.awt.*;
import javax.swing.*;


public class MyPortrait extends JApplet {


    public void paint(Graphics g) {
    	//Whole head
        g.setColor(new Color(250,200,200));
        g.fillOval(60,40,120,160);
        
        //Ears
        g.setColor(new Color(250,200,200));
        g.fillOval(50,105,20,40);//Left
        g.fillOval(168,105,20,40);//Right
        
        //Left eye
        g.setColor(Color.BLACK);
        g.drawArc(75,110,35,20,35,110);//eyebrow
        g.drawOval(80,114,25,15);//eyepit
        g.fillOval(86,115,13,13);//eyeball
        
        //right eye
        g.drawArc(130,110,35,20,35,110);//eyebrow
        g.drawOval(135,114,25,15);//eyepit
        g.fillOval(141, 115, 13, 13);//eyeball
        
        //Glasses
        g.setColor(new Color(240,240,240));
        g.drawRoundRect(76,113,33,20,20,20);//left
        g.drawRoundRect(131,113,33,20,20,20);//right
        g.drawLine(109, 120, 131, 120);//bridge
        g.drawLine(76,120,60,110);//left arm
        g.drawLine(164,120,176,110);//left arm
        
        //Hair
        g.setColor(Color.BLACK);
        g.fillArc(59,40,122,90,0,180);//Top hair
        g.fillArc(-15,43,150,70,269,100);//Left hair (viewer side)
        g.fillArc(130,60,100,50,180,91);//Right hair
        
        //nose
        g.setColor(Color.BLACK);
        g.drawLine(115,118,115,140);//bridge
        g.drawLine(115,140,110,150);//lower
        g.drawArc(110,148,4,4,180,135);//left curve
        g.drawArc(114,150,6,4,200,170);//middle curve
        g.drawArc(119,148,4,4,230,145);//right curve
        
        //mouth
        g.drawArc(98,152,40,20,210,135);//upper
        g.drawArc(98,146,40,30,200,145);//lower
        
        g.drawString("sxycode", 80, 215); 
    }
    
}
