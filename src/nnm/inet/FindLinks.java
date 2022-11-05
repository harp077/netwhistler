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

import java.util.Vector;

import nnm.Graph;
import nnm.NetworkManager;
import nnm.NetworkManagerGUI;
import nnm.Node;
import nnm.snmp.getLinks;

public class FindLinks {
private static Node dNode;
	public FindLinks(Node sNode){
	
    dNode = sNode;
	}
	public static void find(Node node){
	new FindLinks(node);	
	Vector macs = getLinks.getConnectedNodes(dNode);
	if (macs.size() != 0) {
		for (int l = 0; l < macs.size(); l++) {
			String mac = (String) macs.get(l);
			for (int n = 0; n < Graph.nodes.size(); n++) {
				Node gNode = (Node) Graph.nodes.get(n);
				if (NetworkManagerGUI.textHasContent(gNode.getMACaddress())) {
					if (gNode.getMACaddress().equals(mac)) {
						NetworkManagerGUI.manager.aGraph
								.addEdge(NetworkManager.currentNetwork,
										dNode, gNode);
					}
				}
			}
		}
	}
}
}