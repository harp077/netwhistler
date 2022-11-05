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
package nnm.xml;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import nnm.Edge;
import nnm.EdgeN;
import nnm.Graph;
import nnm.NetPanel;
import nnm.Network;
import nnm.NetworkManagerGUI;
import nnm.Node;
import nnm.Shape;
import nnm.snmp.OidToCheck;
import nnm.util.ExtCmd;
import nnm.inet.Service;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class MapWriter {
	public static void write(File aFile) throws IOException {
		File xmlfile = aFile;
		boolean zoom = false;
		if (NetworkManagerGUI.ZOOM) {
			NetworkManagerGUI.ZoomIn();
			zoom = true;
		}
		// Find the implementation
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			DOMImplementation impl = builder.getDOMImplementation();
			// Create the document
			Document doc = impl.createDocument(null, "netwhistler", null);
			// Fill the document
			org.w3c.dom.Node rootElement = doc.getDocumentElement();
			// ProcessingInstruction xmlstylesheet = doc
			// .createProcessingInstruction("xml-stylesheet",
			// "type=\"text/css\" href=\"standard.css\"");
			Comment comment = doc.createComment("NetWhistler xml map");
			doc.insertBefore(comment, rootElement);
			// doc.insertBefore(xmlstylesheet, rootElement);
			Element newElement = doc.createElement("map");
			newElement.setAttribute("name", Graph.label);
			newElement.setAttribute("version", NetworkManagerGUI.version);
			newElement.setAttribute("networks", String.valueOf(NetPanel.nets.size()));
			Vector edgesN = NetworkManagerGUI.netPanel.getEdges();
			newElement
			.setAttribute("netlinks", String.valueOf(edgesN.size()));
			newElement
					.setAttribute("nodes", String.valueOf(Graph.nodes.size()));
			Vector edges = Graph.getEdges();
			newElement
					.setAttribute("links", String.valueOf(edges.size()));
			newElement.setAttribute("shapes", String.valueOf(Graph.shapes
					.size()));
			rootElement.appendChild(newElement);
			// networks
			for (int i = 0; i < NetworkManagerGUI.netPanel.nets.size(); i++) {
				Network aNet = (Network) NetworkManagerGUI.netPanel.nets.get(i);
				newElement = doc.createElement("network");
				newElement.setAttribute("net", aNet.getNetwork());
				newElement.setAttribute("locationX", String.valueOf(aNet
						.getLocation().x));
				newElement.setAttribute("locationY", String.valueOf(aNet
						.getLocation().y));
				newElement.setAttribute("community", aNet.getCommunity());
				newElement.setAttribute("type", String.valueOf(aNet.getType()));
				newElement.setAttribute("icon", String.valueOf(aNet.getNetworkIcon()));
				newElement.setAttribute("status", String.valueOf(aNet.getBadStatus()));
				rootElement.appendChild(newElement);
			}
//			 Output the edgesN
			
			for (int i = 0; i < edgesN.size(); i++) {
				EdgeN nEdgeN = (EdgeN) edgesN.get(i);
				newElement = doc.createElement("netlinks");
				newElement.setAttribute("network", nEdgeN.getLabel());
				newElement.setAttribute("startX", String.valueOf(nEdgeN
						.getStartNet().getLocation().x));
				newElement.setAttribute("startY", String.valueOf(nEdgeN
						.getStartNet().getLocation().y));
				newElement.setAttribute("endX", String.valueOf(nEdgeN
						.getEndNet().getLocation().x));
				newElement.setAttribute("endY", String.valueOf(nEdgeN
						.getEndNet().getLocation().y));
				rootElement.appendChild(newElement);
			}	
			// nodes
			for (int i = 0; i < Graph.nodes.size(); i++) {
				Node aNode = (Node) Graph.nodes.get(i);
				newElement = doc.createElement("node");
				newElement.setAttribute("ip", aNode.getIP());
				newElement.setAttribute("label", aNode.getLabel());
				newElement.setAttribute("network", aNode.getNetwork());
				newElement.setAttribute("hostname", aNode.getDNSname());
				String mac = "n/a";
				if (NetworkManagerGUI.textHasContent(aNode.getMACaddress())) {
					mac = aNode.getMACaddress();
				}
				newElement.setAttribute("hwaddress", mac);
				newElement.setAttribute("locationX", String.valueOf(aNode
						.getLocation().x));
				newElement.setAttribute("locationY", String.valueOf(aNode
						.getLocation().y));
				newElement.setAttribute("type", aNode.getnodeType());
				newElement.setAttribute("monitor", String.valueOf(aNode
						.getMonitor()));
				newElement.setAttribute("dnslabel", String.valueOf(aNode
						.getDnslabel()));
				newElement
						.setAttribute("snmp", String.valueOf(aNode.getSnmp()));
				newElement.setAttribute("rcommunity", aNode.getRcommunity());
				newElement.setAttribute("wcommunity", aNode.getWcommunity());
				newElement.setAttribute("status", String.valueOf(aNode
						.getBadStatus()));
				newElement.setAttribute("uptime", String.valueOf(aNode
						.getUpTime()));
				newElement.setAttribute("downtime", String.valueOf(aNode
						.getDownTime()));
				String ports = "";
				for (int p = 0; p < aNode.getCheckPorts().size(); p++) {
					Service t = (Service) aNode.getCheckPorts().get(p);
						ports = ports + t.getServiceName()+":" +  t.getStatus() + "#";
				}
				newElement.setAttribute("ports", ports);
				String oids = "";
				for (int p = 0; p < aNode.getSNMPOids().size(); p++) {
					OidToCheck oid = (OidToCheck) aNode.getSNMPOids().get(p);
					    oids = oids + oid.getOidName()+":" +  oid.getOidtoCheck() + "#";
				}
				newElement.setAttribute("oids", oids);
				String info = "";
				if (aNode.getInfo() != null) {
					if (aNode.getInfo().length != 0) {
						for (int n = 0; n < aNode.getInfo().length; n++) {
							if (NetworkManagerGUI.textHasContent(aNode
									.getInfo()[n]))
								info = info + aNode.getInfo()[n] + "#";
						}
					}
				}
				newElement.setAttribute("infoTxt", info);
				newElement.setAttribute("hintImg", aNode.getTipImage());
				rootElement.appendChild(newElement);
			}
			// Output the edges

			for (int i = 0; i < edges.size(); i++) {
				Edge nEdge = (Edge) edges.get(i);
				newElement = doc.createElement("links");
				newElement.setAttribute("network", nEdge.getLabel());
				newElement.setAttribute("startX", String.valueOf(nEdge
						.getStartNode().getLocation().x));
				newElement.setAttribute("startY", String.valueOf(nEdge
						.getStartNode().getLocation().y));
				newElement.setAttribute("endX", String.valueOf(nEdge
						.getEndNode().getLocation().x));
				newElement.setAttribute("endY", String.valueOf(nEdge
						.getEndNode().getLocation().y));
				rootElement.appendChild(newElement);
			}
			// Output the shapes
			for (int i = 0; i < Graph.shapes.size(); i++) {
				Shape aShape = (Shape) Graph.shapes.get(i);
				newElement = doc.createElement("shape");
				newElement.setAttribute("label", aShape.getLabel());
				Point p = aShape.getXY();
				newElement.setAttribute("network", aShape.getNetwork());
				newElement.setAttribute("locationX", String.valueOf(p.x));
				newElement.setAttribute("locationY", String.valueOf(p.y));
				newElement.setAttribute("width", String.valueOf(aShape
						.getWidth()));
				newElement.setAttribute("height", String.valueOf(aShape
						.getHeight()));
				// boxColor
				int red = aShape.getBoxColor().getRed();
				int green = aShape.getBoxColor().getGreen();
				int blue = aShape.getBoxColor().getBlue();
				newElement.setAttribute("boxColor", String.valueOf(red) + ":"
						+ String.valueOf(green) + ":" + String.valueOf(blue));
				red = aShape.getBackColor().getRed();
				green = aShape.getBackColor().getGreen();
				blue = aShape.getBackColor().getBlue();
				newElement.setAttribute("backColor", String.valueOf(red) + ":"
						+ String.valueOf(green) + ":" + String.valueOf(blue));
				red = aShape.getTitleColor().getRed();
				green = aShape.getTitleColor().getGreen();
				blue = aShape.getTitleColor().getBlue();
				newElement.setAttribute("txtColor", String.valueOf(red) + ":"
						+ String.valueOf(green) + ":" + String.valueOf(blue));
				red = aShape.getShadColor().getRed();
				green = aShape.getShadColor().getGreen();
				blue = aShape.getShadColor().getBlue();
				newElement.setAttribute("shadColor", String.valueOf(red) + ":"
						+ String.valueOf(green) + ":" + String.valueOf(blue));
				// shadow
				newElement.setAttribute("shadow", String.valueOf(aShape
						.getShadow()));
				// text
				String dbtext = "";
				if (aShape.getText() != null) {
					if (aShape.getText().length != 0) {
						for (int n = 0; n < aShape.getText().length; n++) {
							if (NetworkManagerGUI.textHasContent(aShape
									.getText()[n]))
								dbtext = dbtext + aShape.getText()[n] + "#";
						}
					}
				}
				newElement.setAttribute("infoTxt", dbtext);
				rootElement.appendChild(newElement);
			}
			// timeoutMon ;
			newElement = doc.createElement("monitor");
			newElement.setAttribute("timeout", String
					.valueOf(NetworkManagerGUI.timeoutMon));
			newElement.setAttribute("retries", String
					.valueOf(NetworkManagerGUI.monRetries));
			newElement.setAttribute("reply", String
					.valueOf(NetworkManagerGUI.replyTime));
			newElement.setAttribute("timeoutServices", String
					.valueOf(NetworkManagerGUI.timeoutMonServices));
			newElement.setAttribute("timeoutSNMP", String
					.valueOf(NetworkManagerGUI.timeoutMonSNMP));
			// alerts
			newElement.setAttribute("popupAlert", String
					.valueOf(NetworkManagerGUI.alerts));
			newElement.setAttribute("emailAlert", String
					.valueOf(NetworkManagerGUI.email));
			newElement.setAttribute("cmdAlert", String
					.valueOf(NetworkManagerGUI.alertcmd));
			newElement.setAttribute("alertCom", String
					.valueOf(NetworkManagerGUI.alertCommand));
			newElement.setAttribute("alertAddress",
					NetworkManagerGUI.alertAddress);
			newElement.setAttribute("smtpAddress",
					NetworkManagerGUI.smtpAddress);
			newElement.setAttribute("htmlAlert", String
					.valueOf(NetworkManagerGUI.htmlAlert));
			// cmd
			newElement.setAttribute("customPing", String
					.valueOf(NetworkManagerGUI.customPing));
			newElement.setAttribute("customMac", String
					.valueOf(NetworkManagerGUI.customMac));
			newElement.setAttribute("customSnmp", String
					.valueOf(NetworkManagerGUI.customSnmp));
			newElement.setAttribute("cpingCom", String
					.valueOf(NetworkManagerGUI.cpingCommand));
			newElement.setAttribute("cmacCom", String
					.valueOf(NetworkManagerGUI.cmacCommand));
			newElement.setAttribute("snmpCom", String
					.valueOf(NetworkManagerGUI.snmpScript));
			
			rootElement.appendChild(newElement);

			
			
			// snmp
			newElement = doc.createElement("snmp");
			newElement.setAttribute("wizCom", NetworkManagerGUI.wizCommunity);
			newElement.setAttribute("trapCom", NetworkManagerGUI.trapCommunity);
			rootElement.appendChild(newElement);
			// Commands

			for (int i = 0; i < NetworkManagerGUI.extCommands.size(); i++) {
				newElement = doc.createElement("extcmd");
				ExtCmd cmdt = (ExtCmd) NetworkManagerGUI.extCommands.get(i);
				newElement.setAttribute("name", cmdt.getCmdName());
				newElement.setAttribute("cmd", cmdt.getCmd());
				newElement.setAttribute("args", cmdt.getCmdArgs());
				rootElement.appendChild(newElement);
			}

			// node types

			for (int i = 0; i < NetworkManagerGUI.nodeTypes.size(); i++) {
				newElement = doc.createElement("nodetype");
				String type = NetworkManagerGUI.nodeTypes.get(i);
				newElement.setAttribute("type", type);
				rootElement.appendChild(newElement);
			}
			// Map Colors
			// Background
			newElement = doc.createElement("color");
			int red = NetPanel.backgroundColor.getRed();
			int green = NetPanel.backgroundColor.getGreen();
			int blue = NetPanel.backgroundColor.getBlue();
			newElement.setAttribute("NetbgColor", String.valueOf(red) + ":"
					+ String.valueOf(green) + ":" + String.valueOf(blue));
			//
			
			red = NetworkManagerGUI.backgroundColor.getRed();
			green = NetworkManagerGUI.backgroundColor.getGreen();
			blue = NetworkManagerGUI.backgroundColor.getBlue();
			newElement.setAttribute("bgColor", String.valueOf(red) + ":"
					+ String.valueOf(green) + ":" + String.valueOf(blue));
			// textColor;
			red = NetworkManagerGUI.textColor.getRed();
			green = NetworkManagerGUI.textColor.getGreen();
			blue = NetworkManagerGUI.textColor.getBlue();
			newElement.setAttribute("txtColor", String.valueOf(red) + ":"
					+ String.valueOf(green) + ":" + String.valueOf(blue));
			// lineColor;
			red = NetworkManagerGUI.lineColor.getRed();
			green = NetworkManagerGUI.lineColor.getGreen();
			blue = NetworkManagerGUI.lineColor.getBlue();
			newElement.setAttribute("lineColor", String.valueOf(red) + ":"
					+ String.valueOf(green) + ":" + String.valueOf(blue));
			// selColor;
			red = NetworkManagerGUI.selColor.getRed();
			green = NetworkManagerGUI.selColor.getGreen();
			blue = NetworkManagerGUI.selColor.getBlue();
			newElement.setAttribute("selColor", String.valueOf(red) + ":"
					+ String.valueOf(green) + ":" + String.valueOf(blue));
			// background
			newElement.setAttribute("backImg", Graph.backImage);
			rootElement.appendChild(newElement);
			
			// write xml
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer;
			transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(xmlfile);
			transformer.transform(source, result);
			result.getOutputStream().close();
			
		} catch (Exception ex) {
			//System.out.println(ex);
		}
		if (zoom) {
			NetworkManagerGUI.ZoomOut();
		}

	}
}
