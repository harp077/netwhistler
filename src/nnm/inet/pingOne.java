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
//
package nnm.inet;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JDialog;
import nnm.EventPanel;
import nnm.FPinger;
import nnm.NetworkManagerGUI;
import nnm.Node;
import nnm.StatusPanel;
import nnm.util.MessageDialog;


public class pingOne {
Timer timer;
Node sNode;

public pingOne(int seconds, Node aNode) {
	sNode = aNode;
	timer = new Timer();
	timer.schedule(new RemindTask(), seconds * 1000);
}

class RemindTask extends TimerTask {
	public void run() {
		FPinger pinger = new FPinger();
		boolean stat=sNode.getBadStatus();
		String addr = sNode.getIP();
		if (pinger.Fping(addr)) {
			JDialog tmp = new JDialog();
			new MessageDialog(tmp, " " + sNode.getIP() + " ("
					+ sNode.getDNSname() + ") is alive " + "("
					+ FPinger.pingTm + " ms)", "Fping results");
			sNode.setBadStatus(false);
			sNode.setResponse(FPinger.pingTm);
			
		} else {
			JDialog tmp = new JDialog();
			new MessageDialog(tmp, " " + sNode.getIP() + " is down ",
					"Fping results");
			sNode.setBadStatus(true);
			sNode.setResponse("");
			
			if (!stat){
				Date now = new Date();
				  Format formatter = new SimpleDateFormat("MM.dd.yyyy HH.mm.ss");
				  EventPanel.updateStatus( sNode.getIP() + "#"	+ sNode.getDNSname() +"#"+ formatter.format(now)+ "#Down"); // log
				 
			}
		}
		StatusPanel.updateStatus(sNode);
		NetworkManagerGUI.manager.repaint();
		timer.cancel(); // Terminate the timer thread
	}
}
}
