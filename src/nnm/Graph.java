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

import java.util.Vector;
import java.util.Enumeration;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Image;
import javax.swing.ImageIcon;
import nnm.util.setNIC;


public class Graph {
	public static String label;

	public static Vector nodes;

	public static Vector alerts;

	public static Vector shapes;
	
	public static String backImage;

	

	public Graph() {
		this("", new Vector(), new Vector());
	}

	public Graph(String aLabel) {
		this(aLabel, new Vector(), new Vector());
	}

	public Graph(String aLabel, Vector initialNodes, Vector initialShapes) {
		label = aLabel;
		nodes = initialNodes;
		shapes = initialShapes;
	}

	public Graph getGraph() {
		return this;
	}

	public String getLabel() {
		return label;
	}

	public Vector getNodes() {
		return nodes;
	}

	public Vector getShapes() {
		return shapes;
	}

	public void setLabel(String newLabel) {
		label = newLabel;
	}

	public static Vector getEdges() {
		Vector edges = new Vector();
		Enumeration allNodes = nodes.elements();
		while (allNodes.hasMoreElements()) {

			Enumeration someEdges = ((Node) allNodes.nextElement())
					.incidentEdges().elements();
			while (someEdges.hasMoreElements()) {
				Edge anEdge = (Edge) someEdges.nextElement();
				if (!edges.contains(anEdge)) {
					edges.add(anEdge);
				}
			}
		}
		return edges;
	}

	
	public void addNode(Node aNode) {
		boolean ok = true;
		boolean mac = false;
		if (aNode.getnodeType().equals("hub")
				|| aNode.getnodeType().equals("network-cloud")) {
			nodes.add(aNode);
		} else {
			if (nodes != null) {

				for (int i = 0; i < nodes.size(); i++) {
					Node fNode = (Node) nodes.get(i);
					if (NetworkManagerGUI.textHasContent(fNode.getIP())){
					if (fNode.getIP().equals(aNode.getIP())) { // check
						// duplicate
						// ip
						// System.out.println("Duplicate ip address");
						ok = false;
					}}
						
				}
			} else {
				nodes.add(aNode);
				mac = true;
			}
			if (ok) {
				nodes.add(aNode);
				mac = true;
			}
		}
		if (mac) {
			if (aNode.getnodeType().equals("hub")
					|| aNode.getnodeType().equals("network-cloud")) {
				;
			} else {
				aNode.setMACaddress();
				if (NetworkManagerGUI.textHasContent(aNode.getMACaddress()))
				new setNIC(1,aNode,aNode.getMACaddress());
			}
		}
		if (ok) {
			if (aNode.getnodeType().equals("hub")
					|| aNode.getnodeType().equals("network-cloud")) {
				;
			} else {
			NetworkManagerGUI.treePanel.addNode(NetworkManagerGUI.treePanel.ipnet,aNode);
			NetworkManagerGUI.statPanel.addNode(aNode);
			NetworkManagerGUI.ifacePanel.addNode(aNode);
			}
			
		}
	}

	public void addShape(Shape aShape) {
		shapes.add(aShape);
	}

	public void addEdge(String net,Node start, Node end) {
		Edge anEdge = new Edge(net,start, end);
		start.addIncidentEdge(anEdge);
		end.addIncidentEdge(anEdge);
	}

	public void addEdge(String net,String startLabel, String endLabel) {
		Node start, end;

		start = nodeNamed(startLabel);
		end = nodeNamed(endLabel);
		if ((start != null) && (end != null)) {
			addEdge(net,start, end);
		}
	}

	public void deleteEdge(Edge anEdge) {
		anEdge.getStartNode().incidentEdges().remove(anEdge);
		anEdge.getEndNode().incidentEdges().remove(anEdge);
	}

	public void deleteNode(Node aNode) {
		Enumeration someEdges = aNode.incidentEdges().elements();
		while (someEdges.hasMoreElements()) {
			Edge anEdge = (Edge) someEdges.nextElement();
			anEdge.otherEndFrom(aNode).incidentEdges().remove(anEdge);
		}
		nodes.remove(aNode);
 	    NetworkManagerGUI.treePanel.removeNode(aNode);
 	    NetworkManagerGUI.statPanel.delNode(aNode);
 	    NetworkManagerGUI.ifacePanel.delNode(aNode);
	}

	public void deleteShape(Shape aShape) {
		shapes.remove(aShape);
	}

	public Node nodeNamed(String aLabel) {
		for (int i = 0; i < nodes.size(); i++) {
			Node aNode = (Node) nodes.get(i);
			if (aNode.getLabel().equals(aLabel)) {
				return aNode;
			}
		}
		return null;
	}

	public Node nodeAt(Point p) {
		for (int i = 0; i < nodes.size(); i++) {
			Node aNode = (Node) nodes.get(i);
			int distance = (p.x - aNode.getLocation().x)
					* (p.x - aNode.getLocation().x)
					+ (p.y - aNode.getLocation().y)
					* (p.y - aNode.getLocation().y);
			if (distance <= (Node.RADIUS * Node.RADIUS)) {
				if (aNode.getNetwork().equals(NetworkManager.currentNetwork))
				return aNode;
			}
		}
		return null;
	}

	public Edge edgeAt(Point p) {
		Vector edges = getEdges();
		int midPointX, midPointY;

		for (int i = 0; i < edges.size(); i++) {
			Edge anEdge = (Edge) edges.get(i);
			midPointX = (anEdge.getStartNode().getLocation().x + anEdge
					.getEndNode().getLocation().x) / 2;
			midPointY = (anEdge.getStartNode().getLocation().y + anEdge
					.getEndNode().getLocation().y) / 2;
			int distance = (p.x - midPointX) * (p.x - midPointX)
					+ (p.y - midPointY) * (p.y - midPointY);
			if (distance <= (Node.RADIUS * Node.RADIUS)) {
				if (anEdge.getLabel().equals(NetworkManager.currentNetwork))
				return anEdge;
			}
		}
		return null;
	}

	public Shape shapeAt(Point p) {
		for (int i = 0; i < shapes.size(); i++) {
			Shape aShape = (Shape) shapes.get(i);
			Rectangle r = aShape.getRectangle();
			// don't think about this :)
			if (p.x > r.x && p.x < r.x + r.width && p.y > r.y
					&& p.y < r.y + r.height) {
				if (aShape.getNetwork().equals(NetworkManager.currentNetwork))
				return aShape;
			}
		}
		return null;
	}

	public static Vector selectedNodes() {
		Vector selected = new Vector();
		Enumeration allNodes = nodes.elements();

		while (allNodes.hasMoreElements()) {
			Node aNode = (Node) allNodes.nextElement();
			if (aNode.isSelected()) {
				selected.add(aNode);
			}
		}
		return selected;
	}

	public Vector selectedEdges() {
		Vector selected = new Vector();
		Enumeration allEdges = getEdges().elements();
		while (allEdges.hasMoreElements()) {
			Edge anEdge = (Edge) allEdges.nextElement();
			if (anEdge.isSelected()) {
				selected.add(anEdge);
			}
		}
		return selected;
	}

	public Vector selectedShapes() {
		Vector selected = new Vector();
		Enumeration allShapes = shapes.elements();

		while (allShapes.hasMoreElements()) {
			Shape aShape = (Shape) allShapes.nextElement();
			if (aShape.isSelected()) {
				selected.add(aShape);
			}
		}
		return selected;
	}

	public void draw(Graphics aPen) {
		if (backImage != null) {
			ImageIcon icon = new ImageIcon(backImage);
			Image backgroundImg = icon.getImage();
			aPen.drawImage(backgroundImg, 0, 0, NetworkManagerGUI.frame
						.getWidth(), NetworkManagerGUI.frame.getHeight(),
						null);
		}

		// Draw the shapes - down position
		for (int i = 0; i < shapes.size(); i++) {
			Shape shape = (Shape)shapes.get(i);
			if (shape.getNetwork().equals(NetworkManager.currentNetwork))
			shape.draw(aPen);
		}

		Vector edges = getEdges();
		// Draw the edges
		for (int i = 0; i < edges.size(); i++) {
			Edge edge = (Edge) edges.get(i);
			if (edge.getLabel().equals(NetworkManager.currentNetwork))
			edge.draw(aPen);
		}
		// Draw the nodes now
		for (int i = 0; i < nodes.size(); i++) {
			Node node = (Node) nodes.get(i);
			if (node.getNetwork().equals(NetworkManager.currentNetwork))
			node.draw(aPen);
		}
	}

	
	public static void loadDefaults() {
		Node.nodeSize = 38;
		Node.cloudradius = 60;
		Node.RADIUS = 20;
		NetPanel.backgroundColor = new Color(235,235,235);
		NetworkManagerGUI.netPanel.setBackground(NetPanel.backgroundColor);
		NetworkManagerGUI.backgroundColor = new Color(235,235,235);
		NetworkManagerGUI.manager
				.setBackground(NetworkManagerGUI.backgroundColor);
		NetworkManagerGUI.textColor = Color.black;
		NetworkManagerGUI.lineColor = new Color(0,102,153);
		NetworkManagerGUI.selColor = Color.orange;
		NetworkManager.MODE = true;
		NetworkManagerGUI.MONITORING = false;
		NetworkManagerGUI.monItem.setText("Start Monitoring");
		NetworkManagerGUI.monBut.setIcon(NetworkManagerGUI.ticonstart);
		NetworkManagerGUI.monBut.setToolTipText("Monitoring Start");
		NetworkManagerGUI.ZOOM = false;
		NetworkManagerGUI.timeoutMon = 5;
		NetworkManagerGUI.timeoutMonServices = 5;
		NetworkManagerGUI.timeoutMonSNMP = 5;
		NetworkManagerGUI.monRetries = 3;
		NetworkManagerGUI.replyTime = 1;
		NetworkManagerGUI.alerts = false;
		NetworkManagerGUI.email = false;
		NetworkManagerGUI.alertAddress = "";
		NetworkManagerGUI.alertcmd = false;
		NetworkManagerGUI.alertCommand = "";
		NetworkManagerGUI.smtpAddress = "";
		NetworkManagerGUI.htmlAlert = true;
		NetworkManagerGUI.customPing = false;
		NetworkManagerGUI.customMac = false;
		NetworkManagerGUI.customSnmp = false;
		NetworkManagerGUI.cpingCommand="scripts/fping.py";
		NetworkManagerGUI.cmacCommand = "scripts/getmac.py";
		NetworkManagerGUI.snmpScript="scripts/getsnmp.py";
		NetworkManagerGUI.wizCommunity = "public";
		NetworkManagerGUI.trapCommunity = "public";
		NetworkManagerGUI.nodeTypes.add("3com");
		NetworkManagerGUI.nodeTypes.add("access-point");
		NetworkManagerGUI.nodeTypes.add("ats");
		NetworkManagerGUI.nodeTypes.add("bridge");
		NetworkManagerGUI.nodeTypes.add("catalyst");
		NetworkManagerGUI.nodeTypes.add("cisco");
		NetworkManagerGUI.nodeTypes.add("d-link");
		NetworkManagerGUI.nodeTypes.add("firewall");
		NetworkManagerGUI.nodeTypes.add("freebsd-server");
		NetworkManagerGUI.nodeTypes.add("hp-server");
		NetworkManagerGUI.nodeTypes.add("hub");
		NetworkManagerGUI.nodeTypes.add("ibm");
		NetworkManagerGUI.nodeTypes.add("juniper");
		NetworkManagerGUI.nodeTypes.add("linux-server");
		NetworkManagerGUI.nodeTypes.add("linux-workstation");
		NetworkManagerGUI.nodeTypes.add("lucent");
		NetworkManagerGUI.nodeTypes.add("macosx");
		NetworkManagerGUI.nodeTypes.add("mail-server");
		NetworkManagerGUI.nodeTypes.add("mainframe");
		NetworkManagerGUI.nodeTypes.add("managable-hub");
		NetworkManagerGUI.nodeTypes.add("modem");
		NetworkManagerGUI.nodeTypes.add("netbsd-server");
		NetworkManagerGUI.nodeTypes.add("network-cloud");
		NetworkManagerGUI.nodeTypes.add("network-printer");
		NetworkManagerGUI.nodeTypes.add("network-printserver");
		NetworkManagerGUI.nodeTypes.add("notebook");
		NetworkManagerGUI.nodeTypes.add("novell-server");
		NetworkManagerGUI.nodeTypes.add("olencom");
		NetworkManagerGUI.nodeTypes.add("openbsd-server");
		NetworkManagerGUI.nodeTypes.add("openvms-server");
		NetworkManagerGUI.nodeTypes.add("pix");
		NetworkManagerGUI.nodeTypes.add("redhat-server");
		NetworkManagerGUI.nodeTypes.add("router");
		NetworkManagerGUI.nodeTypes.add("server");
		NetworkManagerGUI.nodeTypes.add("sql-server");
		NetworkManagerGUI.nodeTypes.add("sun-server");
		NetworkManagerGUI.nodeTypes.add("sun-workstation");
		NetworkManagerGUI.nodeTypes.add("sun");
		NetworkManagerGUI.nodeTypes.add("suse-server");
		NetworkManagerGUI.nodeTypes.add("switch");
		NetworkManagerGUI.nodeTypes.add("terminal");
		NetworkManagerGUI.nodeTypes.add("webcam");
		NetworkManagerGUI.nodeTypes.add("web-server");
		NetworkManagerGUI.nodeTypes.add("wi-fi");
		NetworkManagerGUI.nodeTypes.add("windows-server");
		NetworkManagerGUI.nodeTypes.add("windows-workstation");
		NetworkManagerGUI.nodeTypes.add("workstation");
		NetworkManagerGUI.nodeTypes.add("unix");
		NetworkManagerGUI.nodeTypes.add("ups");
		backImage = "";
		NetworkManagerGUI.zoomItem.setText("Zoom Out (-)");
		NetworkManagerGUI.status.setText(" ");
		NetworkManagerGUI.filePath = null;
		NetworkManagerGUI.extCommands=new Vector();
		NetworkManager.menuExtCmd.removeAll();
		MapTree.clear();
		NetworkManagerGUI.netPanel.nets.clear();
		NetworkManagerGUI.statPanel.clear();
		NetworkManagerGUI.ifacePanel.clear();
		NetworkManagerGUI.eventPanel.clear();
		NetworkManagerGUI.graphsPanel.Refresh();
	    NetworkManagerGUI.jsp.getViewport().setViewPosition(new Point(0,0));
		

	}
}
