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
package nnm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import nnm.snmp.OidToCheck;
import nnm.snmp.SNMPget;


public class SNMPTimer {
	Timer snmptimer;
    Node sNode;
	int snmpdelay;

	// public static Vector downServNodes;
	public SNMPTimer(int seconds) {
		snmptimer = new Timer();
		snmpdelay = seconds;
		snmptimer.schedule(new SNMPCheckTask(), seconds * 1000);
	}

	class SNMPCheckTask extends TimerTask {

				public void run() {
					if (NetworkManagerGUI.MONITORING) {
					for (int i = 0; i < Graph.nodes.size(); i++) {
							Node aNode = (Node) Graph.nodes.get(i);	
					        if (aNode.getSnmp()){
					        	for (int s = 0; s < aNode.getSNMPOids().size(); s++) {
						OidToCheck oidt = (OidToCheck) aNode.getSNMPOids().get(s);
						String value = SNMPget.get(aNode, oidt.getOidtoCheck());
						if (NetworkManagerGUI.textHasContent(value))
							oidt.setOidValue(value);
						
					}
				updateOids(aNode);
					        }
				}	        
				if (!NetworkManagerGUI.MONITORING) {
					snmptimer.cancel();
				} else {
					snmptimer.schedule(new SNMPCheckTask(), snmpdelay * 1000); // new Timer
				}
					}
		
	}
	public void updateOids(Node sNode){
		String oids = "";
		for (int p = 0; p < sNode.getSNMPOids().size(); p++) {
			OidToCheck oid = (OidToCheck) sNode.getSNMPOids().get(p);
			    oids = oids + oid.getOidName()+":" +  oid.getOidtoCheck()+ ":" + oid.getOidValue()+ "|";
			    if (NetworkManagerGUI.customSnmp){
			    	runCustom(sNode.getIP(),oid.getOidName(),oid.getOidValue());
			    
			    }
			    }
		
	}
	}	
	public boolean runCustom(String ip,String oid, String val){
		
		Runtime r = Runtime.getRuntime();
		Process p = null;
		String cmd = NetworkManagerGUI.snmpScript + " " + ip + " "+ oid + " " + val;
		try {
			p = r.exec(cmd);
			if (p == null) {
				NetworkManagerGUI.logger.info(" Can't run " + cmd);
				return false;
				// System.out.println("Cant't");
			}
			
			return true;
		} catch (IOException ex1) {
			NetworkManagerGUI.logger.info(" Error executing " + cmd);
		}
		return false;
	}
	
}


