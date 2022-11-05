// FrontEnd Plus GUI for JAD
// DeCompiled : HowNodes.class

package nnm.util;

import java.util.Vector;
import javax.swing.JLabel;
import nnm.*;
import nnm.inet.Service;

public class HowNodes extends Thread
{

    JLabel ta;

    public HowNodes(JLabel tb)
    {
        ta = tb;
    }

    public void run()
    {
        do
        {
            try
            {
                sleep(1000L);
            }
            catch(InterruptedException interruptedexception) { }
            int all = 0;
            int bad = 0;
            int warn = 0;
            if(NetworkManagerGUI.MONITORING)
            {
                for(int i = 0; i < Graph.nodes.size(); i++)
                {
                    Node aNode = (Node)Graph.nodes.get(i);
                    if(aNode.getNetwork().equals(NetworkManager.currentNetwork) && !aNode.getnodeType().equals("hub") && !aNode.getnodeType().equals("network-cloud"))
                    {
                        all++;
                        if(!aNode.getBadStatus())
                        {
                            int badserv = 0;
                            for(int s = 0; s < aNode.getCheckPorts().size(); s++)
                            {
                                Service t = (Service)aNode.getCheckPorts().get(s);
                                if(!t.getStatus())
                                    badserv = 1;
                            }

                            if(badserv == 1)
                                warn++;
                        } else
                        {
                            bad++;
                        }
                    }
                    if(NetworkManagerGUI.textHasContent(NetworkManager.currentNetwork))
                        ta.setText("Network: " + NetworkManager.currentNetwork + " Nodes: " + (all - bad - warn) + "(OK) " + warn + " (WARN) " + bad + " (BAD)");
                }

            } else
            {
                for(int i = 0; i < Graph.nodes.size(); i++)
                {
                    Node aNode = (Node)Graph.nodes.get(i);
                    if(aNode.getNetwork().equals(NetworkManager.currentNetwork) && !aNode.getnodeType().equals("hub") && !aNode.getnodeType().equals("network-cloud"))
                        all++;
                }

                if(NetworkManagerGUI.textHasContent(NetworkManager.currentNetwork))
                    ta.setText("Network: " + NetworkManager.currentNetwork + " Nodes: " + all);
            }
        } while(true);
    }
}
