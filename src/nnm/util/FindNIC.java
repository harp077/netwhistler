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
import java.io.*;
import javax.xml.parsers.*;

import nnm.NetworkManagerGUI;

import org.w3c.dom.*;


public class FindNIC {
	
public FindNIC(){
	
}

		public static String getNIC(String mac) {
			 
			String vendor=null;
			try {
				String fileName = "snmp/oids/nics.xml";

				DocumentBuilder builder = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder();

				// Could use an InputStream instead of a File
				Document xmlDocument = builder.parse(new File(fileName));
				Element rootNode = xmlDocument.getDocumentElement();

				NodeList nodeList = rootNode.getElementsByTagName("mac");
				String nic = null;
				String num = null;
				mac = mac.replaceAll(":","-");
				//System.out.println("Mac: "+mac);
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					NamedNodeMap map = node.getAttributes();
					
					if (map.getNamedItem("nic") != null) {
						nic = map.getNamedItem("nic").getNodeValue();
						num = map.getNamedItem("num").getNodeValue();
						
						if (mac.contains(num)) {
							//System.out.println("OK: "+num + "Nic:" + nic);
							vendor = nic;
							
							
							

						}
					}

				}
			} catch (Exception e) {
				NetworkManagerGUI.logger.info(" Error parsing nics.xml");
				 //e.printStackTrace();
			}
			return vendor;
		}
	}
