package Debug;

import World.Dimensions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class RenderDebug extends JPanel implements ActionListener, Dimensions {
    Timer mainTimer = new Timer(40, this);

    public RenderDebug(){
        mainTimer.start();
    }

    @Override
    public void paint(Graphics g) {
        int y = 10;
        int step = 10;

        g.setColor(Color.white);
        g.fillRect(0, 0, WORLD_X * POLYSIZE, 200);

        for (String logString : LOG.getLogs().keySet()) {
            g.setColor(Color.black);
            g.drawString(LOG.getLog(logString), 2, y);
            y = y + step;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        repaint();
    }
}
