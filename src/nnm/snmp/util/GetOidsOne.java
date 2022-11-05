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
package nnm.snmp.util;

import java.util.Timer;
import java.util.TimerTask;
import nnm.snmp.OidToCheck;
import nnm.snmp.SNMPget;
import nnm.NetworkManagerGUI;
import nnm.Node;


public class GetOidsOne {
	Timer timer;
    Node sNode;
	int delay;

	// public static Vector downServNodes;
	public GetOidsOne(int seconds,Node aNode) {
		timer = new Timer();
		delay = seconds;
		sNode = aNode;
		timer.schedule(new CheckTask(), seconds * 1000);
	}

	class CheckTask extends TimerTask {

				public void run() {
					for (int i = 0; i < sNode.getSNMPOids().size(); i++) {
						OidToCheck oidt = (OidToCheck) sNode.getSNMPOids().get(i);
						String value = SNMPget.get(sNode, oidt.getOidtoCheck());
						if (NetworkManagerGUI.textHasContent(value))
							oidt.setOidValue(value);
					}
					
				
				timer.cancel();
			
		}
	}
	
			
	
}

