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
package nnm.snmp;

import java.util.Vector;

import uk.co.westhawk.snmp.stack.*;
import uk.co.westhawk.snmp.pdu.*;
import nnm.Graph;
import nnm.NetworkManager;
import nnm.NetworkManagerGUI;
import nnm.Node;

public class SNMPdiscover {
	public final static String sysObjectId = "1.3.6.1.2.1.1.2.0";

	private String host;

	private int port;

	private static String community;

	private String oid;

	private String socketType;

	private SnmpContextPool context;

	private BlockPdu pdu;

	static Node dNode;

	public SNMPdiscover() {
		// AsnObject.setDebug(15);

	}

	public void init(String hostIP) {
		try {
			host = hostIP;
			port = SnmpContextBasisFace.DEFAULT_PORT;
			socketType = SnmpContextBasisFace.STANDARD_SOCKET;
			oid = sysObjectId;
			
			if (!NetworkManagerGUI.textHasContent(community)) {
				community = "public";
			}
			createContext(host, port, community, socketType);
			sendGetRequest(oid);
		} catch (Exception exc) {
			// exc.printStackTrace();
			// System.out.print("Exception: " + exc.getMessage());
		}
	}

	private void createContext(String host, String portStr, String comm,
			String socketType) {
		int port = SnmpContextBasisFace.DEFAULT_PORT;
		try {
			port = Integer.valueOf(portStr).intValue();
		} catch (NumberFormatException exc) {
		}
		createContext(host, port, comm, socketType);
	}

	private void createContext(String host, int port, String comm,
			String socketType) {

		if (context != null) {
			context.destroy();
		}
		try {
			context = new SnmpContextPool(host, port, socketType);
			context.setCommunity(comm);
		} catch (java.io.IOException exc) {
			// System.out.print("IOException: " + exc.getMessage());

		}
	}

	private void sendGetRequest(String oid) {
		pdu = new BlockPdu(context);
		pdu.setPduType(BlockPdu.GET);
		pdu.addOid(oid);
		// timeout !!!
		int[] retry = { 100, 200, 300 };
		pdu.setRetryIntervals(retry);
		sendRequest(pdu);
	}

	private void sendRequest(BlockPdu pdu) {

		// System.out.print("Sending request ..");
		
		try {
			varbind var = pdu.getResponseVariableBinding();
			// AsnObjectId oid = var.getOid();
			AsnObject res = var.getValue();
			if (res != null) {
				//System.out.println("Community:" + community);
				//System.out.print("OK: " + res.toString());
				OID_to_type.oid_to_type(res.toString());
			} else {
				// System.out.print("Received no aswer ");
				// System.exit(1);

			}
		} catch (PduException exc) {
			// System.out.print("PduException: " + exc.getMessage());
			// exc.printStackTrace();
		} catch (java.io.IOException exc) {
			// System.out.print("IOException: " + exc.getMessage());
			// exc.printStackTrace();
		}
	}

	public static void discover(Node sNode) {
		dNode = sNode;
		community = dNode.getRcommunity();
		SNMPdiscover application = new SNMPdiscover();
		String ipaddress = dNode.getIP();
		application.init(ipaddress);
		// get
		
	}

}
