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
package nnm.inet;

import java.util.Timer;
import java.util.TimerTask;
import nnm.inet.Service;
import nnm.NetworkManagerGUI;
import nnm.Graph;
import nnm.Node;

public class CheckServices {
	Timer timer;

	int delay;

	byte[] bytes;

	// public static Vector downServNodes;
	public CheckServices(int seconds) {
		timer = new Timer();
		delay = seconds;
		timer.schedule(new CheckTask(), seconds * 1000);
	}

	class CheckTask extends TimerTask {

		public void run() {
			if (NetworkManagerGUI.MONITORING) {
				for (int i = 0; i < Graph.nodes.size(); i++) {
					Node aNode = (Node) Graph.nodes.get(i);
					if (!aNode.getBadStatus()) {
						for (int p = 0; p < aNode.getCheckPorts().size(); p++) {
							Service t = (Service) aNode.getCheckPorts().get(p);
							t.Check(aNode);

						}

						NetworkManagerGUI.manager.repaint();
					}
				}
				
			}
			if (!NetworkManagerGUI.MONITORING) {
				timer.cancel();
			} else {
				timer.schedule(new CheckTask(), delay * 1000); // new Timer
			}

		}
	}

	

}
