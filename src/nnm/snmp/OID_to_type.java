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

import java.io.*;
import javax.xml.parsers.*;

import nnm.NetworkManagerGUI;

import org.w3c.dom.*;



public class OID_to_type {
	public static void oid_to_type(String oid) {
		// System.out.println("Starting...");

		try {
			String fileName = "snmp/oids/oids.xml";

			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();

			// Could use an InputStream instead of a File
			Document xmlDocument = builder.parse(new File(fileName));
			Element rootNode = xmlDocument.getDocumentElement();

			NodeList nodeList = rootNode.getElementsByTagName("node");
			String newoid = null;
			String type = null;
			String descr = null;
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				NamedNodeMap map = node.getAttributes();
				if (map.getNamedItem("oid") != null) {
					newoid = map.getNamedItem("oid").getNodeValue();
					descr = map.getNamedItem("name").getNodeValue();
					// Node typeNode = node.getParentNode();
					// NamedNodeMap typemap = typeNode.getAttributes();
					type = map.getNamedItem("type").getNodeValue();
					if (newoid.equals(oid)) {
						SNMPdiscover.dNode.setnodeType(type);
						//System.out.println("TYPE: "+type);
						// set snmp
						SNMPdiscover.dNode.setSnmp(true);
						long uptime = Long.parseLong(SNMPget.get(
								SNMPdiscover.dNode, "1.3.6.1.2.1.1.3.0")); // uptime
						SNMPdiscover.dNode.setUpTime(uptime);
						

					}
				}

			}
		} catch (Exception e) {
			NetworkManagerGUI.logger.info(" Error parsing oids.xml");
			 //e.printStackTrace();
		}
	}
}
/*
 * String[] info = new String[300]; info[0] = "SysDescr: " +
 * SNMPget.get(SNMPdiscover.dNode, "1.3.6.1.2.1.1.1.0"); // Description info[1] =
 * "SysObjectID: " + descr; long uptime =
 * Long.parseLong(SNMPget.get(SNMPdiscover.dNode,"1.3.6.1.2.1.1.3.0")); info[2] =
 * "SysUpTime: " + NetworkManager.secondsToString(uptime); // SysUptime info[3] =
 * "SysContact: " + SNMPget.get(SNMPdiscover.dNode, "1.3.6.1.2.1.1.4.0"); //
 * SysContact info[4] = "SysName: " + SNMPget.get(SNMPdiscover.dNode,
 * "1.3.6.1.2.1.1.5.0"); // SysName info[5] = "SysLocation: " +
 * SNMPget.get(SNMPdiscover.dNode, "1.3.6.1.2.1.1.6.0"); // SysLocation info[6] =
 * "SysServices: " + SNMPget.get(SNMPdiscover.dNode, "1.3.6.1.2.1.1.7.0"); //
 * SysLocation
 * 
 * SNMPdiscover.dNode.setInfo(info);
 */