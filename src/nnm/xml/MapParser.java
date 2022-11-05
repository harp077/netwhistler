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

import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.awt.Point;
import java.awt.Color;
import javax.swing.JDialog;
import nnm.snmp.OidToCheck;
import nnm.util.ExtCmd;
import nnm.util.MessageDialog;
import nnm.inet.Service;
import nnm.Graph;
import nnm.NetPanel;
import nnm.Network;
import nnm.NetworkManager;
import nnm.NetworkManagerGUI;
import nnm.Node;
import nnm.Shape;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import java.awt.Image;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import java.awt.Font;
import java.awt.FlowLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MapParser {
	public static int status;

	public static void parse(File aFile) {
		File xmlFile = aFile;
		Graph.loadDefaults();
		Graph aGraph = null;
		int bad = 0;
		// progress
		final JFrame progress = new JFrame();

		java.net.URL imageURL = NetworkManagerGUI.class
				.getResource("icons/nw.gif");
		ImageIcon frameIcon = new ImageIcon(imageURL);
		Image image = frameIcon.getImage();
		progress.setIconImage(image);
		JFrame.setDefaultLookAndFeelDecorated(true);

		progress.setResizable(false);
		// int op = progress.getDefaultCloseOperation(); // HIDE_ON_CLOSE

		// Set to ignore the button
		progress.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		progress.setTitle("Open map");
		progress.setSize(310, 120);
		progress.setResizable(false);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension optSize = progress.getSize();
		if (optSize.height > screenSize.height) {
			optSize.height = screenSize.height;
		}
		if (optSize.width > screenSize.width) {
			optSize.width = screenSize.width;
		}
		progress.setLocation((screenSize.width - optSize.width) / 2,
				(screenSize.height - optSize.height) / 2);

		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
		JPanel topPanel = new JPanel();

		topPanel.setPreferredSize(new Dimension(310, 120));
		progress.getContentPane().add(topPanel);

		// Create a label and progress bar
		JLabel statusLabel = new JLabel("Please wait...");
		statusLabel.setFont(NetworkManagerGUI.baseFont);
		// statusLabel.setPreferredSize(new Dimension(280, 24));
		topPanel.add(statusLabel);

		JProgressBar bar = new JProgressBar();
		bar.setPreferredSize(new Dimension(250, 30));
		bar.setFont(new Font(null, Font.PLAIN, 14));

		bar.setStringPainted(true);
		bar.setBorderPainted(true);
		bar.setForeground(new Color(58, 110, 165));
		bar.setBounds(20, 35, 200, 20);
		topPanel.add(bar);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		JButton ok = new JButton("Ok");
		buttonPanel.add(ok);
		ok.setFont(NetworkManagerGUI.baseFont);
		ok.setVisible(false);
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				progress.setVisible(false);
				progress.dispose();
			}
		});
		container.add(topPanel, BorderLayout.CENTER);
		container.add(buttonPanel, BorderLayout.SOUTH);
		progress.getContentPane().add(container);

		progress.setVisible(true);

		//
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document xmlDocument = builder.parse(xmlFile);
			Element rootNode = xmlDocument.getDocumentElement();
			// map base
			NodeList List = rootNode.getElementsByTagName("map");
			for (int i = 0; i < List.getLength(); i++) {
				org.w3c.dom.Node xmlmap = List.item(i);
				NamedNodeMap map = xmlmap.getAttributes();
				aGraph = new Graph(map.getNamedItem("name").getNodeValue());
				String mapversa = map.getNamedItem("version").getNodeValue();
				// check map version
				if (!mapversa.equals(NetworkManagerGUI.version)) {
					JDialog tmp = new JDialog();
					new MessageDialog(tmp, " Incorrect map version! "
							+ mapversa, "Open map");
				}
			}
			// networks
			NodeList netList = rootNode.getElementsByTagName("network");
			bar.setMinimum(0);
			bar.setMaximum(netList.getLength());
			Vector nets = new Vector();
			for (int i = 0; i < netList.getLength(); i++) {
				bar.setValue(i);
				statusLabel.setText("Loading networks...");
				bar.paintImmediately(bar.getBounds());

				org.w3c.dom.Node xmlnode = netList.item(i);
				NamedNodeMap map = xmlnode.getAttributes();
				String aNet = map.getNamedItem("net").getNodeValue();
				Point p = new Point(Integer.parseInt(map.getNamedItem(
						"locationX").getNodeValue()), Integer
						.parseInt(map.getNamedItem("locationY")
								.getNodeValue()));
				String com = map.getNamedItem("community").getNodeValue();
				int type = Integer.parseInt(map.getNamedItem("type").getNodeValue());
				int icon = Integer.parseInt(map.getNamedItem("icon").getNodeValue());
				Network  net = new Network(aNet,p,com,type,icon);
				nets.add(net);
				net.setBadStatus(Boolean.valueOf(
						map.getNamedItem("status").getNodeValue())
						.booleanValue());
			}
			
			NetworkManagerGUI.netPanel.nets = nets;
			NetworkManagerGUI.netPanel.addNetworks();
//			nlines
			NodeList nlineList = rootNode.getElementsByTagName("netlinks");
			bar.setMinimum(0);
			bar.setMaximum(nlineList.getLength());
			
			for (int i = 0; i < nlineList.getLength(); i++) {
				bar.setValue(i);
				statusLabel.setText("Loading network links...");
				bar.paintImmediately(bar.getBounds());

				org.w3c.dom.Node xmlnline = nlineList.item(i);
				NamedNodeMap map = xmlnline.getAttributes();
				// Edge anEdge;
				String label = map.getNamedItem("network").getNodeValue();
				Network start = NetworkManagerGUI.netPanel.networkAt(new Point(Integer.parseInt(map
						.getNamedItem("startX").getNodeValue()), Integer
						.parseInt(map.getNamedItem("startY").getNodeValue())));
				Network end = NetworkManagerGUI.netPanel.networkAt(new Point(Integer.parseInt(map
						.getNamedItem("endX").getNodeValue()), Integer
						.parseInt(map.getNamedItem("endY").getNodeValue())));
				NetworkManagerGUI.netPanel.addEdgeN(label,start, end);
				
			} 

			NetworkManagerGUI.netPanel.repaint();
			
			// nodes

			NodeList nodeList = rootNode.getElementsByTagName("node");
			bar.setMinimum(0);
			bar.setMaximum(nodeList.getLength());

			for (int i = 0; i < nodeList.getLength(); i++) {
				bar.setValue(i);
				statusLabel.setText("Loading nodes...");
				bar.paintImmediately(bar.getBounds());

				org.w3c.dom.Node xmlnode = nodeList.item(i);
				NamedNodeMap map = xmlnode.getAttributes();
				Node aNode = new Node();
				aNode.setIP(map.getNamedItem("ip").getNodeValue());
				aNode.setLabel(map.getNamedItem("label").getNodeValue());
				aNode.setNetwork(map.getNamedItem("network").getNodeValue());
				aNode.setDNSname(map.getNamedItem("hostname").getNodeValue());
				aNode.MACaddr = map.getNamedItem("hwaddress").getNodeValue();
				aNode
						.setLocation(Integer.parseInt(map.getNamedItem(
								"locationX").getNodeValue()), Integer
								.parseInt(map.getNamedItem("locationY")
										.getNodeValue()));
				aNode.setnodeType(map.getNamedItem("type").getNodeValue());
				aNode.setMonitor(Boolean.valueOf(
						map.getNamedItem("monitor").getNodeValue())
						.booleanValue());
				aNode.setDnslabel(Integer.parseInt(map.getNamedItem("dnslabel")
						.getNodeValue()));
				aNode
						.setSnmp(Boolean.valueOf(
								map.getNamedItem("snmp").getNodeValue())
								.booleanValue());
				aNode.setRcommunity(map.getNamedItem("rcommunity")
						.getNodeValue());
				aNode.setWcommunity(map.getNamedItem("wcommunity")
						.getNodeValue());
				aNode.setBadStatus(Boolean.valueOf(
						map.getNamedItem("status").getNodeValue())
						.booleanValue());
				aNode.setUpTime(Long.parseLong(map.getNamedItem("uptime")
						.getNodeValue()));
				aNode.setDownTime(Long.parseLong(map.getNamedItem("downtime")
						.getNodeValue()));
				Vector ports = new Vector();
				if (NetworkManagerGUI.textHasContent(map.getNamedItem("ports")
						.getNodeValue())) {
					String[] tmp = map.getNamedItem("ports").getNodeValue()
							.split("#");
					for (int t = 0; t < tmp.length; t++) {
						String[] ptmp = tmp[t].split(":");
						ports.add(new Service(ptmp[0],Boolean.valueOf(ptmp[1]).booleanValue()));
					}
				}
				aNode.setCheckPorts(ports);
				
				Vector oids = new Vector();
				if (NetworkManagerGUI.textHasContent(map.getNamedItem("oids")
						.getNodeValue())) {
					String[] tmp = map.getNamedItem("oids").getNodeValue()
							.split("#");
					for (int t = 0; t < tmp.length; t++) {
						String[] otmp = tmp[t].split(":");
						oids.add(new OidToCheck(otmp[0],otmp[1],""));
					}
				}
				aNode.setSNMPOids(oids);
				
				String[] info = map.getNamedItem("infoTxt").getNodeValue()
						.replaceAll("#", "\n").split("\n");
				aNode.setInfo(info);

				aNode.setTipImage(map.getNamedItem("hintImg").getNodeValue());
				NetworkManager.currentNetwork = aNode.getNetwork();
				aGraph.addNode(aNode);
			}

			// lines
			NodeList lineList = rootNode.getElementsByTagName("links");
			bar.setMinimum(0);
			bar.setMaximum(lineList.getLength());
			
			for (int i = 0; i < lineList.getLength(); i++) {
				bar.setValue(i);
				statusLabel.setText("Loading nodes links...");
				bar.paintImmediately(bar.getBounds());

				org.w3c.dom.Node xmlline = lineList.item(i);
				NamedNodeMap map = xmlline.getAttributes();
				// Edge anEdge;
				String label = map.getNamedItem("network").getNodeValue();
				NetworkManager.currentNetwork = label;
				Node start = aGraph.nodeAt(new Point(Integer.parseInt(map
						.getNamedItem("startX").getNodeValue()), Integer
						.parseInt(map.getNamedItem("startY").getNodeValue())));
				Node end = aGraph.nodeAt(new Point(Integer.parseInt(map
						.getNamedItem("endX").getNodeValue()), Integer
						.parseInt(map.getNamedItem("endY").getNodeValue())));
				aGraph.addEdge(label,start, end);
				
			} 

			// shapes
			NodeList shapeList = rootNode.getElementsByTagName("shape");
			bar.setMinimum(0);
			bar.setMaximum(shapeList.getLength());
			for (int i = 0; i < shapeList.getLength(); i++) {
				bar.setValue(i);
				statusLabel.setText("Loading shapes...");
				bar.paintImmediately(bar.getBounds());

				org.w3c.dom.Node xmlshape = shapeList.item(i);
				NamedNodeMap map = xmlshape.getAttributes();
				//
				Shape aShape = new Shape();
				aShape.setLabel(map.getNamedItem("label").getNodeValue());
				aShape.setNetwork(map.getNamedItem("network").getNodeValue());
				aShape.setXY(new Point(Integer.parseInt(map.getNamedItem(
						"locationX").getNodeValue()), Integer.parseInt(map
						.getNamedItem("locationY").getNodeValue())));
				aShape.setWidth(Integer.parseInt(map.getNamedItem("width")
						.getNodeValue()));
				aShape.setHeight(Integer.parseInt(map.getNamedItem("height")
						.getNodeValue()));
				// boxColor
				String[] boxcolor = map.getNamedItem("boxColor").getNodeValue()
						.split(":");
				int red = Integer.parseInt(boxcolor[0]);
				int green = Integer.parseInt(boxcolor[1]);
				int blue = Integer.parseInt(boxcolor[2]);
				aShape.setBoxColor(new Color(red, green, blue));
				// boxBackColor
				String[] backcolor = map.getNamedItem("backColor")
						.getNodeValue().split(":");
				red = Integer.parseInt(backcolor[0]);
				green = Integer.parseInt(backcolor[1]);
				blue = Integer.parseInt(backcolor[2]);
				aShape.setBackColor(new Color(red, green, blue));
				String[] txtcolor = map.getNamedItem("txtColor").getNodeValue()
						.split(":");
				red = Integer.parseInt(txtcolor[0]);
				green = Integer.parseInt(txtcolor[1]);
				blue = Integer.parseInt(txtcolor[2]);
				aShape.setTitleColor(new Color(red, green, blue));
				String[] shadcolor = map.getNamedItem("shadColor")
						.getNodeValue().split(":");
				red = Integer.parseInt(shadcolor[0]);
				green = Integer.parseInt(shadcolor[1]);
				blue = Integer.parseInt(shadcolor[2]);
				// boxShadColor
				aShape.setShadColor(new Color(red, green, blue));
				// shadow
				aShape.setShadow(Boolean.valueOf(
						map.getNamedItem("shadow").getNodeValue())
						.booleanValue());
				// text
				String[] info = map.getNamedItem("infoTxt").getNodeValue()
						.replaceAll("#", "\n").split("\n");
				aShape.setText(info);
				NetworkManager.currentNetwork = aShape.getNetwork();
				aGraph.addShape(aShape);
				
			}

			// monitor

			NodeList monList = rootNode.getElementsByTagName("monitor");
			bar.setMinimum(0);
			bar.setMaximum(monList.getLength());
			for (int i = 0; i < monList.getLength(); i++) {
				bar.setValue(i);
				statusLabel.setText("Loading monitor...");
				bar.paintImmediately(bar.getBounds());

				org.w3c.dom.Node xmlmon = monList.item(i);
				NamedNodeMap map = xmlmon.getAttributes();
				NetworkManagerGUI.timeoutMon = Integer.parseInt(map
						.getNamedItem("timeout").getNodeValue());
				NetworkManagerGUI.monRetries = Integer.parseInt(map
						.getNamedItem("retries").getNodeValue());
				NetworkManagerGUI.replyTime = Integer.parseInt(map
						.getNamedItem("reply").getNodeValue());
				NetworkManagerGUI.timeoutMonServices = Integer.parseInt(map
						.getNamedItem("timeoutServices").getNodeValue());
				NetworkManagerGUI.timeoutMonSNMP = Integer.parseInt(map
						.getNamedItem("timeoutSNMP").getNodeValue());
				// alerts
				NetworkManagerGUI.alerts = Boolean.valueOf(
						map.getNamedItem("popupAlert").getNodeValue())
						.booleanValue();
				NetworkManagerGUI.email = Boolean.valueOf(
						map.getNamedItem("emailAlert").getNodeValue())
						.booleanValue();
				NetworkManagerGUI.alertcmd = Boolean.valueOf(
						map.getNamedItem("cmdAlert").getNodeValue())
						.booleanValue();
				NetworkManagerGUI.alertCommand = map.getNamedItem("alertCom").getNodeValue();
				NetworkManagerGUI.alertAddress = map.getNamedItem(
						"alertAddress").getNodeValue();
				NetworkManagerGUI.smtpAddress = map.getNamedItem("smtpAddress")
						.getNodeValue();
				NetworkManagerGUI.htmlAlert = Boolean.valueOf(
						map.getNamedItem("htmlAlert").getNodeValue())
						.booleanValue();
//				 scripts
				NetworkManagerGUI.customPing = Boolean.valueOf(
						map.getNamedItem("customPing").getNodeValue())
						.booleanValue();
				NetworkManagerGUI.customMac = Boolean.valueOf(
						map.getNamedItem("customMac").getNodeValue())
						.booleanValue();
				NetworkManagerGUI.customSnmp = Boolean.valueOf(
						map.getNamedItem("customSnmp").getNodeValue())
						.booleanValue();
				NetworkManagerGUI.cpingCommand = map.getNamedItem("cpingCom")
				.getNodeValue();
				NetworkManagerGUI.cmacCommand = map.getNamedItem("cmacCom")
				.getNodeValue();
				NetworkManagerGUI.snmpScript = map.getNamedItem("snmpCom")
				.getNodeValue();
				
			}
			// snmp
			NodeList snmpList = rootNode.getElementsByTagName("snmp");
			bar.setMinimum(0);
			bar.setMaximum(snmpList.getLength());
			for (int i = 0; i < snmpList.getLength(); i++) {
				bar.setValue(i);
				statusLabel.setText("Loading snmp...");
				bar.paintImmediately(bar.getBounds());

				org.w3c.dom.Node xmlsn = snmpList.item(i);
				NamedNodeMap map = xmlsn.getAttributes();
				NetworkManagerGUI.wizCommunity = map.getNamedItem("wizCom")
						.getNodeValue();
				NetworkManagerGUI.trapCommunity = map.getNamedItem("trapCom")
						.getNodeValue();
			}
//			 node types

			NodeList typesList = rootNode.getElementsByTagName("nodetype");
			bar.setMinimum(0);
			NetworkManagerGUI.nodeTypes.clear();
			bar.setMaximum(typesList.getLength());
			for (int i = 0; i < typesList.getLength(); i++) {
				bar.setValue(i);
				statusLabel.setText("Loading nodes types ...");
				bar.paintImmediately(bar.getBounds());

				org.w3c.dom.Node xmlcmd = typesList.item(i);
				NamedNodeMap map = xmlcmd.getAttributes();

				String type = map.getNamedItem("type").getNodeValue();
				
				NetworkManagerGUI.nodeTypes.add(type);
				
			}
			// commands
			NodeList cmdList = rootNode.getElementsByTagName("extcmd");
			bar.setMinimum(0);
			bar.setMaximum(cmdList.getLength());
			for (int i = 0; i < cmdList.getLength(); i++) {
				bar.setValue(i);
				statusLabel.setText("Loading commands ...");
				bar.paintImmediately(bar.getBounds());

				org.w3c.dom.Node xmlcmd = cmdList.item(i);
				NamedNodeMap map = xmlcmd.getAttributes();

				String name = map.getNamedItem("name").getNodeValue();
				String cmd = map.getNamedItem("cmd").getNodeValue();
				String args = map.getNamedItem("args").getNodeValue();

				final ExtCmd cmdt = new ExtCmd(name, cmd, args);
				NetworkManagerGUI.extCommands.add(cmdt);
				JMenuItem menuItem = NetworkManager.AddCmdItem();
				NetworkManager.menuExtCmd.add(menuItem);
			}

			// map
			NodeList colorList = rootNode.getElementsByTagName("color");
			bar.setMinimum(0);
			bar.setMaximum(colorList.getLength());
			for (int i = 0; i < colorList.getLength(); i++) {
				bar.setValue(i);
				statusLabel.setText("Loading colors ...");
				bar.paintImmediately(bar.getBounds());

				org.w3c.dom.Node xmlcol = colorList.item(i);
				NamedNodeMap map = xmlcol.getAttributes();

				// Map Colors
				String[] netbgcolor = map.getNamedItem("NetbgColor").getNodeValue()
				.split(":");
				int red = Integer.parseInt(netbgcolor[0]);
				int green = Integer.parseInt(netbgcolor[1]);
				int blue = Integer.parseInt(netbgcolor[2]);
				NetPanel.backgroundColor = new Color(red, green, blue);
				NetworkManagerGUI.netPanel.setBackground(NetPanel.backgroundColor);
				String[] bgcolor = map.getNamedItem("bgColor").getNodeValue()
						.split(":");
				red = Integer.parseInt(bgcolor[0]);
				green = Integer.parseInt(bgcolor[1]);
				blue = Integer.parseInt(bgcolor[2]);
				NetworkManagerGUI.backgroundColor = new Color(red, green, blue);
				NetworkManagerGUI.manager
						.setBackground(NetworkManagerGUI.backgroundColor);
				String[] txtcolor = map.getNamedItem("txtColor").getNodeValue()
						.split(":");
				red = Integer.parseInt(txtcolor[0]);
				green = Integer.parseInt(txtcolor[1]);
				blue = Integer.parseInt(txtcolor[2]);
				NetworkManagerGUI.textColor = new Color(red, green, blue);
				String[] linecolor = map.getNamedItem("lineColor")
						.getNodeValue().split(":");
				red = Integer.parseInt(linecolor[0]);
				green = Integer.parseInt(linecolor[1]);
				blue = Integer.parseInt(linecolor[2]);
				NetworkManagerGUI.lineColor = new Color(red, green, blue);
				String[] selcolor = map.getNamedItem("selColor").getNodeValue()
						.split(":");
				red = Integer.parseInt(selcolor[0]);
				green = Integer.parseInt(selcolor[1]);
				blue = Integer.parseInt(selcolor[2]);
				NetworkManagerGUI.selColor = new Color(red, green, blue);
				// background
				String img = map.getNamedItem("backImg").getNodeValue();
				if (NetworkManagerGUI.textHasContent(img)
						&& !img.equals("null")) {
					Graph.backImage = img;
				}
				
			}

			NetworkManagerGUI.manager.repaint();
			bar.setMaximum(100);
			bar.setValue(100);
			bar.paintImmediately(bar.getBounds());
			statusLabel.setText("Map loaded successfully");
			NetworkManagerGUI.filePath=xmlFile;
			ok.setVisible(true);
			NetworkManagerGUI.graphsPanel.Refresh();
		} catch (Exception e) {
			bad = 1;
			// System.out.println(e);
		}
		if (bad == 1) {
			// JDialog tmp = new JDialog();
			// new MessageDialog(tmp," Errors while parsing map! ","Open map");
			progress.setVisible(false);
			JDialog tmp = new JDialog();
			new MessageDialog(tmp, " Errors while parsing map! ", "Open map");

		}
	}
}
