import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;

public class Controller extends JApplet {
    JPanel buttonPanel = new JPanel();
    JButton runButton = new JButton("Run");
    JButton stopButton = new JButton("Stop");
    Timer timer;
        
    Model model = new Model();
    View view = new View(model);
    
    public void init() {
      layOutComponents();
      attachListenersToComponents();
      
      // Connect model and view
      model.addObserver(view);
   }

    private void attachListenersToComponents() {
        runButton.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent event) {
                 runButton.setEnabled(false);
                 stopButton.setEnabled(true);
                timer = new Timer(true);
                timer.schedule(new Strobe(), 0, 40);
             }
          });
          stopButton.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent event) {
                runButton.setEnabled(true);
                stopButton.setEnabled(false);
                timer.cancel();
             }
          });
    }

    private void layOutComponents() {
        setLayout(new BorderLayout());
          this.add(BorderLayout.SOUTH, buttonPanel);
              buttonPanel.add(runButton);
              buttonPanel.add(stopButton);
              stopButton.setEnabled(false);
          this.add(BorderLayout.CENTER, view);
    }
    
    private class Strobe extends TimerTask {
        public void run() {
            model.setLimits(view.getWidth(), view.getHeight());
            model.makeOneStep();
        }
    }
}
