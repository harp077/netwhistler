//
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
//
package nnm.snmp;

import java.util.Timer;
import java.util.TimerTask;
import nnm.*;
import nnm.inet.FindLinks;

public class SNMPIdentify {
	Timer timer;

	Node sNode;

	public SNMPIdentify(int seconds, Node aNode) {
		sNode = aNode;
		timer = new Timer();
		timer.schedule(new RemindTask(), seconds * 1000);
	}

	class RemindTask extends TimerTask {
		public void run() {
			SNMPdiscover.discover(sNode);
			FindLinks.find(sNode);
			NetworkManagerGUI.manager.repaint();
			timer.cancel(); // Terminate the timer thread
		}
	}
}
