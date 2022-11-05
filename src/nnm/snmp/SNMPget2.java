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

import uk.co.westhawk.snmp.stack.*;
import uk.co.westhawk.snmp.pdu.*;
import nnm.NetworkManagerGUI;
import nnm.Node;

public class SNMPget2 {

	private String host;

	private int port;

	private String community;

	private String oid;

	private String socketType;

	private SnmpContextPool context;

	private BlockPdu pdu;

	static Node dNode;

	public static String VALUE;

	public SNMPget2() {
		// AsnObject.setDebug(15);

	}

	public void init(String hostIP, String comm, String OID ) {
		try {
			host = hostIP;
			port = SnmpContextBasisFace.DEFAULT_PORT;
			socketType = SnmpContextBasisFace.STANDARD_SOCKET;
			oid = OID;
			community = comm;
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
			//AsnObjectId oid = var.getOid();
			AsnObject res = var.getValue();
			if (res != null) {
				// System.out.print("OK: " + res.toString());
				VALUE = res.toString();
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

	public static String get(Node sNode, String OID) {
		dNode = sNode;
		SNMPget2 application = new SNMPget2();
		String ipaddress = dNode.getIP();
		String comm = dNode.getRcommunity();
		String noid = OID;
		application.init(ipaddress, comm, noid);
		return VALUE;
	}

}
