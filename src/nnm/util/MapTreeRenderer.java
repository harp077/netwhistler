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
package nnm.util;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

import nnm.NetPanel;
import nnm.Network;
import nnm.NetworkManagerGUI;
import nnm.Graph;
import nnm.Node;
import nnm.MapTree;
import nnm.inet.Service;

public class MapTreeRenderer extends DefaultTreeCellRenderer {
	private static ImageIcon PC_ICON;

	private static ImageIcon SNMP_ICON;

	private static ImageIcon TREE_ICON;

	private static ImageIcon CLOSED_ICON;

	private static ImageIcon BAD_PC_ICON;
	private static ImageIcon WARN_PC_ICON;
	private static ImageIcon BAD_SNMP_ICON;
	private static ImageIcon WARN_SNMP_ICON;
	private static ImageIcon ROUTER_ICON;
	private static ImageIcon BAD_ROUTER_ICON;
	
	static {

		java.net.URL pcimg = NetworkManagerGUI.class
				.getResource("icons/box.gif");
		ImageIcon pcicon = new ImageIcon(pcimg);
		PC_ICON = new ImageIcon(ColorIcon.colorize(pcicon.getImage(),
				Color.green));
		BAD_PC_ICON = new ImageIcon(ColorIcon.colorize(pcicon.getImage(),
				Color.red));
		WARN_PC_ICON = new ImageIcon(ColorIcon.colorize(pcicon.getImage(),
				new Color(255, 128, 64)));
		java.net.URL snmpimg = NetworkManagerGUI.class
				.getResource("icons/snbox.gif");
		ImageIcon snmpicon = new ImageIcon(snmpimg);
		SNMP_ICON = new ImageIcon(ColorIcon.colorize(snmpicon.getImage(),
				Color.green));
		BAD_SNMP_ICON = new ImageIcon(ColorIcon.colorize(snmpicon.getImage(),
				Color.red));
		WARN_SNMP_ICON = new ImageIcon(ColorIcon.colorize(snmpicon.getImage(),
				new Color(255, 128, 64)));
		java.net.URL statimg = NetworkManagerGUI.class
				.getResource("icons/status.gif");
		TREE_ICON = new ImageIcon(statimg);
		java.net.URL routerimg = NetworkManagerGUI.class.getResource("icons/router.png");
		ROUTER_ICON = new ImageIcon(routerimg);
		BAD_ROUTER_ICON = new ImageIcon(ColorIcon.colorize(ROUTER_ICON.getImage(),
				Color.red));
		java.net.URL closimg = NetworkManagerGUI.class
				.getResource("icons/folder.gif");
		
		CLOSED_ICON = new ImageIcon(closimg);
		
				
	}

	public MapTreeRenderer() {
		setLeafIcon(PC_ICON);
		setClosedIcon(CLOSED_ICON);
		setOpenIcon(TREE_ICON);

	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		 if (MapTree.currentNode != null)
         {
          if (value.hashCode() == MapTree.currentNode.hashCode())
           sel = true;
       }
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Object nodeObj = node.getUserObject();
		for (int i = 0; i < Graph.nodes.size(); i++) {
			Node aNode = (Node) Graph.nodes.get(i);
			if (nodeObj.equals(aNode)) {
				setText(aNode.getIP());
				if (MapTree.LOOK == 0) {
					setText(aNode.getIP());
				} else if (MapTree.LOOK == 1) {
					if (NetworkManagerGUI.textHasContent(aNode.getDNSname())) {
						if (!aNode.getDNSname().equals(aNode.getIP()))
							setText(aNode.getDNSname());
						else
							setText(aNode.getIP());
					} else
						setText(aNode.getIP());
				} else if (MapTree.LOOK == 2) {
					if (NetworkManagerGUI.textHasContent(aNode.getMACaddress()))
						setText(aNode.getMACaddress());
					else
						setText(aNode.getIP());
				} else {
					setText(aNode.getIP());
				}
				if (!aNode.getBadStatus()) {
					if (aNode.getCheckPorts().size()!=0){
						for (int p = 0; p < aNode.getCheckPorts().size(); p++) {
						Service t = (Service) aNode.getCheckPorts().get(p);
						if (!t.getStatus()) {
							setForeground(new Color(255, 128, 64));
							if (aNode.getSnmp())
								setIcon(WARN_SNMP_ICON);
							else 
								setIcon(WARN_PC_ICON);
							} else {
							 setForeground(Color.black);
							 if (aNode.getSnmp())
						 		setIcon(SNMP_ICON);
						 		else setIcon(PC_ICON);
							}
						}
					} else {
						 if (aNode.getSnmp())
						 		setIcon(SNMP_ICON);
						 	else 
						 		setIcon(PC_ICON);
					}
				} else {
					setForeground(Color.red);
					if (aNode.getSnmp())
						setIcon(BAD_SNMP_ICON);
					else
						setIcon(BAD_PC_ICON);
					
				}
			}
			
		}
		if (NetPanel.nets.size()!=0){
		for (int i = 0; i < NetPanel.nets.size(); i++) {
			Network net = (Network) NetPanel.nets.get(i);
			if (net.getType()==1){
			if (node.toString().equals(net.getNetwork())) {
				if (!net.getBadStatus())
				setIcon(ROUTER_ICON);
				else
					setIcon(BAD_ROUTER_ICON);	
			}
				}
			else setLeafIcon(TREE_ICON);
			}
		
		}
		/*if (node.toString().equals("Root")) 
			setIcon(TREE_ICON);	
		if (node.toString().equals("IP Network")) 
			setIcon(TREE_ICON);	*/
		updateUI();
		//
		return this;
	}
}
