// FrontEnd Plus GUI for JAD
// DeCompiled : MouseXY.class

package nnm.util;

import java.awt.Point;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import nnm.NetworkManager;
import nnm.NetworkManagerGUI;

public class MouseXY extends Thread
{

    JLabel ta;

    public MouseXY(JLabel tb)
    {
        ta = tb;
    }

    public void run()
    {
        do
        {
            Point p;
            do
            {
                try
                {
                    sleep(100L);
                }
                catch(InterruptedException interruptedexception) { }
                p = NetworkManager.mousePoint;
            } while(p == null || !NetworkManagerGUI.jsp.isShowing());
            ta.setText("XY: " + p.x + ":" + p.y);
        } while(true);
    }
}
