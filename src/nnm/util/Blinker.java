// Copyright (C) 2005 Mila NetWhistler.  All rights reserved.
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.                                                            
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//       
// For more information contact: 
//      Mila NetWhistler        <netwhistler@gmail.com>
//      http://www.netwhistler.spb.ru/

package nnm.util;


import java.util.Timer;
import java.util.TimerTask;
import nnm.Graph;
import nnm.NetworkManagerGUI;
import nnm.Node;



public class Blinker {
	Timer btimer;
    Node sNode;
	int bdelay;
	int alpha;

	// public static Vector downServNodes;
	public Blinker(int seconds) {
		btimer = new Timer();
		bdelay = seconds;
		
		btimer.schedule(new BlinkerTask(), seconds*500);
	}

	class BlinkerTask extends TimerTask {

				public void run() {
					 
					if (NetworkManagerGUI.MONITORING) {
						
						
											
					for (int i = 0; i < Graph.nodes.size(); i++) {
							Node aNode = (Node) Graph.nodes.get(i);	
					        if (!aNode.isEnabled()){
							   aNode.setEnabled(true);	
					        }else{
					          aNode.setEnabled(false);
					        }
					       
					}
					NetworkManagerGUI.manager.repaint();	
					}
					
				if (!NetworkManagerGUI.MONITORING) {
					btimer.cancel();
				} else {
					btimer.schedule(new BlinkerTask(), bdelay * 500); // new Timer
				}
					}
		
	}
	
	}	



