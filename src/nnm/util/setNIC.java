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
import nnm.*;

public class setNIC {
	Timer timer;
	String mac;
	String addr;
	Node sNode;
	boolean change = false;

	public setNIC(int seconds, Node aNode,String macaddr) {
		sNode= aNode;
		mac = macaddr;
		timer = new Timer();
		timer.schedule(new RemindTask(), seconds * 1000);
	}

	class RemindTask extends TimerTask {
		public void run() {
			FindNIC nic = new FindNIC();
			addr = nic.getNIC(mac);
			sNode.setNIC(addr);
			IfacePanel.updateStatus(sNode);
			timer.cancel(); // Terminate the timer thread
		}
	}
}
