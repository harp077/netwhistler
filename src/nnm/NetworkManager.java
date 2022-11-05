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

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.io.*;
import javax.swing.border.EtchedBorder;
import nnm.snmp.OidToCheck;
import nnm.snmp.SNMPIdentify;
import nnm.snmp.SNMPget;
import nnm.snmp.MIBbrowser.MIBview;
import nnm.snmp.util.GetOidsOne;
import nnm.snmp.util.OidView;
import nnm.util.*;
import nnm.util.report.showStatus;
import nnm.inet.*;
import nnm.inet.telnet.*;
import nnm.inet.vnc.VncViewer;
import nnm.inet.portscan.ScanFrame;


public class NetworkManager extends JPanel implements MouseListener,
		KeyListener, MouseMotionListener {

	public static Graph aGraph;

	private Node dragNode;

	private Shape dragShape;

	public static Node sNode;
    
	public static Node nNode;

	public static Node startNode;

	public static boolean MODE = true;
	public static String currentNetwork;
	public static JMenu toolsMenu, monitorMenu, menuServices, menuExtCmd,
			OidsMenu;

	public static JPopupMenu nodePopupMenu, linePopupMenu, mapPopupMenu,
			shapePopupMenu;

	public static Point p;

	public static Point mousePoint;

	private Point dragPoint;

	public static String ipAddrStr;

	private Point elasticEndLocation;

	public Rectangle currentRect;

	private static JMenuItem menuStatus, menuConfig, menuFPing, menuTrace,
			SNMPMenu, linkMenu, menuDelete, menuLineDel, menuShapeDel,
			menuShapeOpt, menuMapNode, menuMapBox, menuMapOpt, menuShapeMove,
			menuShapeResize;

	public static String oidVal;

	public static DefaultListModel portsmodel, oidmodel;

	public static JList portsList, oidList;

	public static String curservice = "";

	public static Vector<Service> ports;

	public static Vector oids;

	public static ImageIcon icon;

	public static String iconPath;
	
	public static JLabel wnodeicon;

	public static JLabel bnodeicon;
	public static JLabel nodeicon;
	public static  boolean loadIcon=false;
	
	public NetworkManager() {
		this(new Graph());
		
	}

	public NetworkManager(Graph g) {
		aGraph = g;
		// setOpaque(false);
		setBackground(NetworkManagerGUI.backgroundColor);
		addEventHandlers();
		ToolTipManager manager = ToolTipManager.sharedInstance();
		manager.setDismissDelay(3600000);
		// popup
		toolsMenu = new JMenu("Tools");
		final JMenuItem telItem = new JMenuItem("Telnet");
		final JMenuItem wwwItem = new JMenuItem("WWW");
		final JMenuItem scanItem = new JMenuItem("Scan");
		final JMenuItem vncItem = new JMenuItem("VNCviewer");
		telItem.setFont(NetworkManagerGUI.baseFont);
		wwwItem.setFont(NetworkManagerGUI.baseFont);
		scanItem.setFont(NetworkManagerGUI.baseFont);
		vncItem.setFont(NetworkManagerGUI.baseFont);

		toolsMenu.add(telItem);
		toolsMenu.add(wwwItem);
		toolsMenu.add(scanItem);
		toolsMenu.add(vncItem);
		toolsMenu.setFont(NetworkManagerGUI.baseFont);
		menuStatus = new JMenuItem("Status");
		// menuStatus.setFont(NetworkManagerGUI.baseFont);
		linkMenu = new JMenuItem("Connect To ...");
		linkMenu.setFont(NetworkManagerGUI.baseFont);
		menuConfig = new JMenuItem("Properties");
		menuConfig.setFont(NetworkManagerGUI.baseFont);
		menuFPing = new JMenuItem("Ping");
		menuFPing.setFont(NetworkManagerGUI.baseFont);
		menuTrace = new JMenuItem("Trace");
		menuTrace.setFont(NetworkManagerGUI.baseFont);
		monitorMenu = new JMenu("Monitoring");
		final JMenuItem monItem = new JMenuItem("Disable");
		monitorMenu.setFont(NetworkManagerGUI.baseFont);
		monItem.setFont(NetworkManagerGUI.baseFont);
		monitorMenu.add(monItem);
		menuServices = new JMenu("Network Services");
		menuServices.setFont(NetworkManagerGUI.baseFont);
		final JMenuItem portsItem = new JMenuItem("Check Now");
		portsItem.setFont(NetworkManagerGUI.baseFont);
		menuServices.add(portsItem);
		monitorMenu.add(menuServices);
		OidsMenu = new JMenu("SNMP");
		OidsMenu.setFont(NetworkManagerGUI.baseFont);
		final JMenuItem monIdentItem = new JMenuItem("Get OIDs Now");
		monIdentItem.setFont(NetworkManagerGUI.baseFont);
		OidsMenu.add(monIdentItem);
		monitorMenu.add(OidsMenu);

		SNMPMenu = new JMenu("SNMP");
		final JMenuItem mibSNMPItem = new JMenuItem("MIB Browser");
		mibSNMPItem.setFont(NetworkManagerGUI.baseFont);
		SNMPMenu.add(mibSNMPItem);
		final JMenuItem mibIdentItem = new JMenuItem("Identify");
		mibIdentItem.setFont(NetworkManagerGUI.baseFont);
		SNMPMenu.add(mibIdentItem);
		SNMPMenu.setFont(NetworkManagerGUI.baseFont);
		menuDelete = new JMenuItem("Delete");
		menuDelete.setFont(NetworkManagerGUI.baseFont);
		menuExtCmd = new JMenu("Exeternal Commands");
		menuExtCmd.setFont(NetworkManagerGUI.baseFont);

		nodePopupMenu = new JPopupMenu();
		nodePopupMenu.add(menuStatus);
		nodePopupMenu.addSeparator();
		nodePopupMenu.add(linkMenu);
		nodePopupMenu.add(menuExtCmd);
		nodePopupMenu.add(toolsMenu);
		nodePopupMenu.addSeparator();
		nodePopupMenu.add(menuFPing);
		nodePopupMenu.add(menuTrace);
		nodePopupMenu.add(monitorMenu);
		nodePopupMenu.add(SNMPMenu);
		nodePopupMenu.add(menuDelete);
		nodePopupMenu.addSeparator();
		nodePopupMenu.add(menuConfig);

		nodePopupMenu.addPopupMenuListener(new PopupMenuListener() {

			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				// lname.setVisible(false);
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				// lname.setVisible(true);
			}

			public void popupMenuCanceled(PopupMenuEvent e) {
				// lname.setVisible(true);
			}
		});
		menuStatus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sNode = aGraph.nodeAt(p);
				new showStatus(sNode);
			}
		});
		linkMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sNode = aGraph.nodeAt(p);
				MODE = false;
				startNode = sNode;
				dragNode = sNode;
				elasticEndLocation = p;
			}
		});
		menuConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				sNode = aGraph.nodeAt(p);
				propertDialog(sNode, false);

			}
		});

		menuDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*
				 * int val = JOptionPane.showConfirmDialog(null, "Are you
				 * sure?", "Delete node", JOptionPane.YES_NO_OPTION);
				 */
				ConfirmDialog dlg = new ConfirmDialog(null, "Are you sure?",
						"Delete Node");
				boolean yes = dlg.getAction();

				if (yes) {
					// setBounds(0, 0, 0, 0);
					sNode = aGraph.nodeAt(p);
					// Graph.nodes.remove(sNode);
					// updateFrame();
					Enumeration someEdges = sNode.incidentEdges().elements();
					while (someEdges.hasMoreElements()) {
						Edge anEdge = (Edge) someEdges.nextElement();
						anEdge.otherEndFrom(sNode).incidentEdges().remove(
								anEdge);
					}
					// Remove the node now
					Graph.nodes.remove(sNode);
					MapTree.removeNode(sNode);
					StatusPanel.delNode(sNode);
					IfacePanel.delNode(sNode);
				}
				// updateFrame();
				repaint();
			}
		});

		//
		menuFPing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sNode = aGraph.nodeAt(p);
				new pingOne(1, sNode);
				repaint();

			}
		});

		menuTrace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sNode = aGraph.nodeAt(p);
				NetworkManagerGUI.statusArea.setText("");
				NetworkManagerGUI.statusP.setVisible(true);
				new TraceWizard(1, sNode);

			}
		});
		portsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FPinger pinger = new FPinger();
				sNode = aGraph.nodeAt(p);
				String addr = sNode.getIP();

				if (pinger.Fping(addr)) {
					new CheckServicesOne(1, sNode);
					repaint();
				}
			}
		});
		monItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sNode = aGraph.nodeAt(p);
				if (sNode.getMonitor()) {
					sNode.setMonitor(false);
				} else if (!sNode.getMonitor()) {
					sNode.setMonitor(true);
				}
			}
		});
		monitorMenu.addMenuListener(new MenuListener() {
			public void menuCanceled(MenuEvent e) {
			}

			public void menuDeselected(MenuEvent e) {
			}

			public void menuSelected(MenuEvent e) {
				sNode = aGraph.nodeAt(p);
				if (sNode.getMonitor()) {
					monItem.setText("Disable");
				} else if (!sNode.getMonitor()) {
					monItem.setText("Enable");
				}
			}
		});
		mibSNMPItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sNode = aGraph.nodeAt(p);
				new MIBview(sNode);

			}
		});
		mibIdentItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sNode = aGraph.nodeAt(p);
				new SNMPIdentify(1, sNode);

			}
		});
		monIdentItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sNode = aGraph.nodeAt(p);
				new GetOidsOne(1, sNode);
			}
		});

		telItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sNode = aGraph.nodeAt(p);
				JTerm.telnet(sNode.getIP());
			}
		});

		wwwItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sNode = aGraph.nodeAt(p);
				OpenURL.displayURL("http://" + sNode.getIP());
			}
		});
		scanItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sNode = aGraph.nodeAt(p);
				ScanFrame.show(sNode.getIP());
			}
		});
		vncItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sNode = aGraph.nodeAt(p);
				String[] arg = { "host", sNode.getIP(), "port", "5901" };
				VncViewer.main(arg);
			}
		});
		// line popup menu
		menuLineDel = new JMenuItem("Delete");
		menuLineDel.setFont(NetworkManagerGUI.baseFont);
		linePopupMenu = new JPopupMenu();
		linePopupMenu.add(menuLineDel);
		menuLineDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Edge sEdge = aGraph.edgeAt(p);
				aGraph.deleteEdge(sEdge);
				// updateFrame();
				repaint();
			}
		});

		// box popup menu
		menuShapeDel = new JMenuItem("Delete");
		menuShapeDel.setFont(NetworkManagerGUI.baseFont);
		menuShapeDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Shape aShape = aGraph.shapeAt(p);
				aGraph.deleteShape(aShape);
				// updateFrame();
				repaint();
			}
		});
		menuShapeMove = new JMenuItem("Move");
		menuShapeMove.setFont(NetworkManagerGUI.baseFont);
		menuShapeMove.setSelected(true);
		menuShapeMove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Shape aShape = aGraph.shapeAt(p);
				aShape.SHAPE_MOVE = true;
			}
		});
		menuShapeResize = new JMenuItem("Resize");
		menuShapeResize.setFont(NetworkManagerGUI.baseFont);
		menuShapeResize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Shape aShape = aGraph.shapeAt(p);
				aShape.SHAPE_MOVE = false;
			}
		});
		menuShapeOpt = new JMenuItem("Properties");
		menuShapeOpt.setFont(NetworkManagerGUI.baseFont);
		shapePopupMenu = new JPopupMenu();
		shapePopupMenu.add(menuShapeMove);
		shapePopupMenu.add(menuShapeResize);
		shapePopupMenu.add(menuShapeOpt);
		shapePopupMenu.add(menuShapeDel);
		menuShapeOpt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Shape aShape = aGraph.shapeAt(p);
				NetworkManagerGUI.optShapeDialog(aShape, false, null);
				// updateFrame();
				repaint();
			}
		});

		// map popup menu
		menuMapNode = new JMenuItem("Add Node");
		menuMapNode.setFont(NetworkManagerGUI.baseFont);
		menuMapBox = new JMenuItem("Add Box");
		menuMapBox.setFont(NetworkManagerGUI.baseFont);

		mapPopupMenu = new JPopupMenu();

		menuMapOpt = new JMenuItem("Map Options");
		menuMapOpt.setFont(NetworkManagerGUI.baseFont);
		mapPopupMenu.add(menuMapNode);
		mapPopupMenu.add(menuMapBox);
		mapPopupMenu.addSeparator();
		mapPopupMenu.add(menuMapOpt);
		menuMapNode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// NetworkManagerGUI.newNodeDialog();
				NetworkManager.propertDialog(null, true);
			}
		});
		menuMapBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Shape aShape = aGraph.shapeAt(p);
				NetworkManagerGUI.optShapeDialog(null, true, p);
			}
		});
		menuMapOpt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NetworkManagerGUI.optionsDialog();
			}
		});

	}

	public void paintComponent(Graphics aPen) {
		super.paintComponent(aPen);
		Graphics2D g2d = (Graphics2D) aPen;
		aGraph.draw(g2d);
		g2d.setColor(NetworkManagerGUI.selColor);
		Stroke stroke = g2d.getStroke();
		float[] dashPattern = { 20, 5, 20, 5 };
		g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10, dashPattern, 0));
		if (!MODE) {
			if (dragNode != null) {
				// if (!dragNode.isSelected()) {
				g2d.drawLine(dragNode.getLocation().x,
						dragNode.getLocation().y, elasticEndLocation.x,
						elasticEndLocation.y);
				// }
			}

		}

		Dimension d = size();
		g2d.setStroke(stroke);
		// If currentRect exists, paint a rectangle on top.
		if (currentRect != null) {
			Rectangle box = getDrawableRect(currentRect, d);
			g2d.drawRect(box.x, box.y, box.width - 1, box.height - 1);

		}
	}

	public void mouseClicked(MouseEvent event) {
		// if (event.getClickCount() == 2)
		if (MODE) {
			Node aNode = aGraph.nodeAt(event.getPoint());
			Edge anEdge = aGraph.edgeAt(event.getPoint());
			Shape anShape = aGraph.shapeAt(event.getPoint());

			if (aNode == null) {
				if (anEdge == null) {
					// aGraph.addNode(new Node(event.getPoint()));
					if (anShape == null) {
						;
					} else {
						anShape.toggleSelected();
					}
				} else {
					anEdge.toggleSelected();
				}
				for (int n = 0; n < Graph.selectedNodes().size(); n++) {
					Node sNode = (Node) Graph.selectedNodes().get(n);
					sNode.toggleSelected();
					
					
				}	
			} else {
				if (event.getClickCount() == 2) {
					aNode.toggleSelected();

				}
			}
		}
		update();

	}

	public void mousePressed(MouseEvent event) {
		dragPoint = event.getPoint();
		if (event.isPopupTrigger() || event.isMetaDown()) {
			p = new Point(event.getX(), event.getY());
			Node aNode = aGraph.nodeAt(p);
			Edge anEdge = aGraph.edgeAt(p);
			Shape anShape = aGraph.shapeAt(new Point(p.x - 1, p.y - 1));
			// if (aNode != null && aNode.getSnmp()) {
			// menuSnmp.setVisible(true);
			// }
			// else {
			// menuSnmp.setVisible(false);
			// }
			if (aNode != null && aNode.getNetwork().equals(currentNetwork)) {
				if (aNode.getnodeType().equals("hub")
						|| aNode.getnodeType().equals("network-cloud")) {
					menuStatus.setVisible(false);
					SNMPMenu.setVisible(false);
					menuFPing.setVisible(false);
					menuTrace.setVisible(false);
					toolsMenu.setVisible(false);
					monitorMenu.setVisible(false);
					menuExtCmd.setVisible(false);
				} else {
					menuStatus.setVisible(true);
					menuFPing.setVisible(true);
					menuTrace.setVisible(true);
					toolsMenu.setVisible(true);
					monitorMenu.setVisible(true);
					if (NetworkManagerGUI.extCommands.size() != 0)
						menuExtCmd.setVisible(true);
					else
						menuExtCmd.setVisible(false);
					if (aNode.getSnmp()) {
						OidsMenu.setVisible(true);
						SNMPMenu.setVisible(true);
					} else {
						OidsMenu.setVisible(false);
						SNMPMenu.setVisible(false);
					}
					ExtCmd.IPaddress = aNode.getIP();
				}
			}

			if (aNode != null) {
				nodePopupMenu.show(event.getComponent(), event.getX(), event
						.getY());
			} else if (anEdge != null) {
				linePopupMenu.show(event.getComponent(), event.getX(), event
						.getY());
			} else if (anShape != null) {
				shapePopupMenu.show(event.getComponent(), event.getX(), event
						.getY());
			}

			else {
				mapPopupMenu.show(event.getComponent(), event.getX(), event
						.getY());
			}

		} else {
			Node aNode = aGraph.nodeAt(event.getPoint());
			// Shape aShape = aGraph.shapeAt(new Point(p.x+5,p.y+5));
			Shape aShape = aGraph.shapeAt(event.getPoint());
			if (aNode != null) {
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				dragNode = aNode;
				NetworkManagerGUI.treePanel.selectNode(aNode);
				NetworkManagerGUI.treePanel.expandTree();
				 aNode.toggleSelected();
			} else if (aShape != null) {
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				dragShape = aShape;
				aShape.toggleSelected();
			} else {
				currentRect = new Rectangle(event.getX(), event.getY(), 0, 0);
			}
		}
	}

	public void mouseDragged(MouseEvent event) {
		
		if (MODE) {
			if (dragNode != null) {
				int oldx = dragNode.getLocation().x;
				int oldy = dragNode.getLocation().y;
				if (dragNode.isSelected()) {
					for (int n = 0; n < Graph.selectedNodes().size(); n++) {
						Node aNode = (Node) Graph.selectedNodes().get(n);
						aNode.getLocation().translate(
								event.getPoint().x - dragPoint.x,
								event.getPoint().y - dragPoint.y);
						
					}
					dragPoint = event.getPoint();

				} else {
					dragNode.setLocation(event.getPoint());
					// multiply drag
					int newx=dragNode.getLocation().x;
					int newy=dragNode.getLocation().y;
					for (int n = 0; n < Graph.selectedNodes().size(); n++) {
						Node dNode = (Node) Graph.selectedNodes().get(n);
						int a = 0;
						int b = 0;
						a=dNode.getLocation().x + newx - oldx;
						b=dNode.getLocation().y + newy - oldy;
						dNode.setLocation(a,b);
					}
				}		
			} else if (dragShape != null) {

				dragShape.setSize(event.getPoint());
			}
			if (currentRect != null) {
				currentRect.resize(event.getX() - currentRect.x, event.getY() - currentRect.y);
			}
		} else {
			elasticEndLocation = event.getPoint();
		}
		Rectangle r = new Rectangle(event.getX(), event.getY(), 1, 1);
		scrollRectToVisible(r);
		// We have changed the model, so now update
		
		update();
	}

	public void mouseReleased(MouseEvent event) {
		if (!event.isPopupTrigger() || !event.isMetaDown()) {
			Node aNode = aGraph.nodeAt(event.getPoint());

			/*
			 * if ((aNode != null) && (aNode != dragNode) &&
			 * !NetworkManager.MODE) { if (dragNode != null) {
			 * aGraph.addEdge(dragNode, aNode); } }
			 */
			if ((aNode != null) && (aNode != startNode) && !NetworkManager.MODE) {
				if (dragNode != null) {
					aGraph.addEdge(currentNetwork,startNode, aNode);

				}
			}
			if (dragNode != null && dragNode.isSelected()) {
				dragNode.toggleSelected();
			}

			if (dragShape != null && dragShape.isSelected()) {
				dragShape.toggleSelected();
			}
			for (int c = 0; c < Graph.nodes.size(); c++) {
				Node cNode = (Node) Graph.nodes.get(c);
				if (currentRect != null) {
					if (currentRect.contains(cNode.getLocation())) {
						cNode.toggleSelected();

					}
				}
			}
			MODE = true;
			currentRect = null;
			dragNode = null;
			startNode = null;
			dragShape = null;
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			update();
		}
	}

	public void mouseOver(MouseEvent event) {

	}

	public void mouseEntered(MouseEvent event) {

		// Node aNode = aGraph.nodeAt(event.getPoint());
		// if (aNode != null) {
		// aNode.showToolTip();
		// }//
		// setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	public void mouseExited(MouseEvent event) {

	}

	// ToolTips
	public void mouseMoved(MouseEvent event) {
		mousePoint = event.getPoint();
		if (!MODE) {
			elasticEndLocation = event.getPoint();
			repaint();
		}
		Node aNode = aGraph.nodeAt(event.getPoint());
		if (aNode != null) {
			String image = "";
			int useImage = 0;
			if (NetworkManagerGUI.textHasContent(aNode.getTipImage())
					&& !aNode.getTipImage().equals("null")) {
				image = "file:" + aNode.getTipImage();
				useImage = 1;
			}
			String ToolTip = "<html>";
			ToolTip = ToolTip + "<body bgcolor=#FFFFE1><center><b>"; //
			if (aNode.getnodeType().equals("hub")
					|| aNode.getnodeType().equals("network-cloud")) {
				ToolTip = ToolTip + aNode.getLabel();
			} else if (NetworkManagerGUI.textHasContent(aNode.getDNSname())) {
				ToolTip = ToolTip + aNode.getDNSname();
				if (!aNode.getIP().equals(aNode.getDNSname())) {
					ToolTip = ToolTip + " (" + aNode.getIP() + ")";
				}
			} else {
				ToolTip = ToolTip + aNode.getIP();
			}
			ToolTip = ToolTip + "</b></center>";
			ToolTip = ToolTip + "<table><tr><td>";
			if (useImage == 1) {
				ToolTip = ToolTip + "<img src=" + image + ">";
			}
			ToolTip = ToolTip + "</td><td>";
			String[] Info = aNode.getInfo();
			if (Info != null) {
				if (Info.length != 0) {
					for (int i = 0; i < Info.length; i++) {
						if (NetworkManagerGUI.textHasContent(Info[i])) {
							ToolTip = ToolTip + Info[i] + "<br>";
						} else {
							;
						}
					}
				}
			}
			ToolTip = ToolTip + "</td></tr></table>";
			/*
			 * ToolTip = ToolTip + "<table><tr><td>Type:</td><td>" +
			 * aNode.getnodeType() + "</td></tr>"; if
			 * (aNode.getnodeType().equals("hub") ||
			 * aNode.getnodeType().equals("network-cloud")) { ToolTip = ToolTip + "<tr><td>Name:</td><td>" +
			 * aNode.getLabel() + "</td></tr>"; } else { ToolTip = ToolTip + "<tr><td>IP
			 * address:</td><td>" + aNode.getIP() + "</td></tr>"; }
			 */
			if (aNode.getnodeType().equals("hub")
					|| aNode.getnodeType().equals("network-cloud")) {
				;
			} else {
				ToolTip = ToolTip + "<hr>";
				/*
				 * if (NetworkManagerGUI.textHasContent(aNode.getMACaddress())) {
				 * ToolTip = ToolTip + "<tr><td>MAC address:</td><td>" +
				 * aNode.getMACaddress() + "</td></tr>"; } if
				 * (aNode.getSnmp()) { ToolTip = ToolTip + "<tr><td>SNMP
				 * Managable:</td><td>Yes</td></tr>"; } if
				 * (aNode.getMonitor()) { ToolTip = ToolTip + "<tr><td>Monitoring:</td><td>Enabled</td></tr>"; }
				 * else { ToolTip = ToolTip + "<tr><td>Monitoring:</td><td>Disabled</td></tr>"; }
				 */
				if (aNode.getMonitor()) { // if monitoring
					String status = "";
					if (aNode.getBadStatus()) {
						status = "DOWN";
						ToolTip = ToolTip
								+ "<tr><td><b><font color=red>Status:</font></b></td><td><b><font color=red>"
								+ status + "</font></b></td></tr>";
					} else {
						boolean warn = false;
						for (int i = 0; i < aNode.getCheckPorts().size(); i++) {
							Service t = (Service) aNode.getCheckPorts().get(i);
							if (!t.getStatus()) {
								warn = true;
							}
						}
						if (warn) {
							status = "WARNING";
							ToolTip = ToolTip
									+ "<tr><td><b><font color=#FF9933>Status</font></b></td><td><b><font color=#FF9933>"
									+ status + "</font></b></td></tr>";
						} else {
							status = "OK";
							ToolTip = ToolTip
									+ "<tr><td><b>Status:</b></td><td><b>"
									+ status + "</b></td></tr>";
						}
					}
					if (!aNode.getBadStatus()
							&& NetworkManagerGUI.textHasContent(aNode
									.getResponse())) {
						ToolTip = ToolTip + "<tr><td>Response:</td><td>"
								+ aNode.getResponse() + " ms</font></td></tr>";
					}
					if (!aNode.getBadStatus()) {
						String up = "";
						if (aNode.getSnmp())
							up = secondsToString(aNode.getUpTime());
						else
							up = pingTimer.millisecondsToString(aNode
									.getUpTime());
						ToolTip = ToolTip + "<tr><td>Uptime:</td><td>" + up
								+ "</td></tr>";
					} else {
						ToolTip = ToolTip
								+ "<tr><td>Downtime:</td><td>"
								+ pingTimer.millisecondsToString(aNode
										.getDownTime()) + "</td></tr>";

					}
					if (!aNode.getBadStatus()) {
						for (int i = 0; i < aNode.getCheckPorts().size(); i++) {
							Service t = (Service) aNode.getCheckPorts().get(i);
							if (!t.getStatus()) {
								ToolTip = ToolTip
										+ "<tr><td>Service "
										+ t.getServiceName()
										+ ":</td><td><font color=red>Not Responding</font></td></tr>";
							}
						}
					}
				}
			}
			ToolTip = ToolTip + " </table></html>";
			setToolTipText(ToolTip);
		} else {
			setToolTipText(null);
		}
	}

	public void keyTyped(KeyEvent event) {
	}

	public void keyReleased(KeyEvent event) {
	}

	public void keyPressed(KeyEvent event) {
		/*
		 * if (event.getKeyCode() == KeyEvent.VK_DELETE) { Enumeration
		 * highlightedEdges = aGraph.selectedEdges().elements(); while
		 * (highlightedEdges.hasMoreElements()) { aGraph.deleteEdge((Edge)
		 * highlightedEdges.nextElement()); } Enumeration highlightedNodes =
		 * aGraph.selectedNodes().elements(); while
		 * (highlightedNodes.hasMoreElements()) { aGraph.deleteNode((Node)
		 * highlightedNodes.nextElement()); } Enumeration highlightedShapes =
		 * aGraph.selectedShapes().elements(); while
		 * (highlightedShapes.hasMoreElements()) { aGraph.deleteShape((Shape)
		 * highlightedShapes.nextElement()); } update(); }
		 */
	}

	public void update() {
		requestFocus();
		removeEventHandlers();
		repaint();
		addEventHandlers();
	}

	public void addEventHandlers() {
		addMouseMotionListener(this);
		addMouseListener(this);
		addKeyListener(this);
	}

	public void removeEventHandlers() {
		removeMouseMotionListener(this);
		removeMouseListener(this);
		removeKeyListener(this);
	}

	public static void propertDialog(Node aNode, boolean addNode) {
		final JDialog dialog = new JDialog();
		sNode = aNode;
		
		final boolean add = addNode;
		if (!add) {
			dialog.setTitle("Properties for " + sNode.getLabel());
		} else {
			dialog.setTitle("Add Node");
		}
		dialog.setSize(340, 310);
		dialog.setResizable(false);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension optSize = dialog.getSize();
		if (optSize.height > screenSize.height) {
			optSize.height = screenSize.height;
		}
		if (optSize.width > screenSize.width) {
			optSize.width = screenSize.width;
		}
		dialog.setLocation((screenSize.width - optSize.width) / 2,
				(screenSize.height - optSize.height) / 2);
		dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
		JTabbedPane tabs = new JTabbedPane();
		JPanel opt = new JPanel();
		opt.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		opt.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(1, 1, 1, 1);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		final JLabel hubLab = new JLabel();
		final JLabel ipLab = new JLabel();
		final JTextField hubFil = new FixedLengthTextField(15);
		final JTextField ipFil = new FixedLengthTextField(15);
		final JTextField rcomFil = new FixedLengthTextField(25);
		final JTextField wcomFil = new FixedLengthTextField(25);
		final JLabel rcomlb = new JLabel();
		final JLabel wcomlb = new JLabel();
		final JLabel macLab = new JLabel();
		final JLabel disLab = new JLabel();
		final JComboBox dnslabbox, portsbox;
		final JLabel monLab = new JLabel();
		final JCheckBox monBox = new JCheckBox();
		final JCheckBox snBox = new JCheckBox();
		final JTextArea textArea;
		final JComboBox typeBox = new JComboBox();
		final String[] dnsnames = new String[5];
		final JPanel mainP = new JPanel();
		final JPanel iconPanel = new JPanel();
		iconPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		iconPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		iconPanel.setBackground(NetworkManagerGUI.sysBackColor);
		nodeicon = new JLabel();
		wnodeicon = new JLabel();
		bnodeicon = new JLabel();
		
		dnsnames[0] = "<IP Address>";
		dnsnames[1] = "<DNS Name>";
		dnsnames[2] = "<DNS Name (IP Address)>";
		dnsnames[3] = "<Short DNS Name>";
		dnsnames[4] = "<SNMP System Name>";
		String[] name_entries = { dnsnames[0], dnsnames[1], dnsnames[2],
				dnsnames[3], dnsnames[4] };
		DefaultComboBoxModel nameModel = new DefaultComboBoxModel();
		for (int i = 0; i < name_entries.length; i++) {
			nameModel.addElement(name_entries[i]);
		}
		dnslabbox = new JComboBox(nameModel);
		dnslabbox.setBackground(NetworkManagerGUI.sysBackColor);
		dnslabbox.setPreferredSize(new Dimension(100, 20));
		dnslabbox.setMinimumSize(new Dimension(100, 20));
		if (!add) {
			iconPath ="pics/" + sNode.getnodeType() + ".gif";
		} else {
			iconPath ="pics/workstation.gif";
		}
		Image img = null;
		try {
			File sfile = new File(iconPath);
			img = ImageIO.read(sfile);
		} catch (IOException ex){
			
		}
		if (img!=null){
		loadIcon=true;	
		Image rImage = img.getScaledInstance(48, 48, Image.SCALE_DEFAULT);
		icon = new ImageIcon(rImage);
		nodeicon.setVerticalTextPosition(SwingConstants.BOTTOM);
		nodeicon.setHorizontalTextPosition(SwingConstants.CENTER);
		nodeicon.setIcon(icon);
		nodeicon.setText("OK");
		ImageIcon wicon = new ImageIcon(ColorIcon.colorize(icon.getImage(),
				new Color(255, 128, 64)));
		ImageIcon bicon = new ImageIcon(ColorIcon.colorize(icon.getImage(),
				Color.red));
		wnodeicon.setIcon(wicon);
		wnodeicon.setText("WARNING");
		wnodeicon.setForeground(new Color(255, 128, 64));
		wnodeicon.setVerticalTextPosition(SwingConstants.BOTTOM);
		wnodeicon.setHorizontalTextPosition(SwingConstants.CENTER);
		bnodeicon.setIcon(bicon);
		bnodeicon.setText("DOWN");
		bnodeicon.setForeground(Color.red);
		bnodeicon.setVerticalTextPosition(SwingConstants.BOTTOM);
		bnodeicon.setHorizontalTextPosition(SwingConstants.CENTER);
		
		iconPanel.add(nodeicon, "west");
		iconPanel.add(Box.createHorizontalStrut(30));
		iconPanel.add(wnodeicon, "center");
		iconPanel.add(Box.createHorizontalStrut(30));
		iconPanel.add(bnodeicon, "east");
		} else {
			loadIcon=false;
		iconPanel.removeAll();
			JLabel errLab = new JLabel("Can't load " + iconPath);	
		errLab.setFont(NetworkManagerGUI.baseFont);
		iconPanel.add(errLab, "center");
		iconPanel.repaint();
		
		}
		// opt Panel
		JLabel typeLab = new JLabel("Type  ");
		typeLab.setFont(NetworkManagerGUI.baseFont);
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 2;
		opt.add(typeLab, c);
		opt.add(Box.createHorizontalStrut(20));
		// typeBox
		for (int i = 0; i < NetworkManagerGUI.nodeTypes.size(); i++) {
			String type = (String) NetworkManagerGUI.nodeTypes.get(i);
			typeBox.addItem(type);
		}
		/*
		typeBox.addItem("3com");
		typeBox.addItem("access-point");
		typeBox.addItem("ats");
		typeBox.addItem("bridge");
		typeBox.addItem("catalyst");
		typeBox.addItem("cisco");
		typeBox.addItem("d-link");
		typeBox.addItem("firewall");
		typeBox.addItem("freebsd-server");
		typeBox.addItem("hp-server");
		typeBox.addItem("hub");
		typeBox.addItem("ibm");
		typeBox.addItem("juniper");
		typeBox.addItem("linux-server");
		typeBox.addItem("linux-workstation");
		typeBox.addItem("lucent");
		typeBox.addItem("macosx");
		typeBox.addItem("mail-server");
		typeBox.addItem("mainframe");
		typeBox.addItem("managable-hub");
		typeBox.addItem("modem");
		typeBox.addItem("netbsd-server");
		typeBox.addItem("network-cloud");
		typeBox.addItem("network-printer");
		typeBox.addItem("network-printserver");
		typeBox.addItem("notebook");
		typeBox.addItem("novell-server");
		typeBox.addItem("olencom");
		typeBox.addItem("openbsd-server");
		typeBox.addItem("openvms-server");
		typeBox.addItem("pix");
		typeBox.addItem("redhat-server");
		typeBox.addItem("router");
		typeBox.addItem("server");
		typeBox.addItem("sql-server");
		typeBox.addItem("sun-server");
		typeBox.addItem("sun-workstation");
		typeBox.addItem("sun");
		typeBox.addItem("suse-server");
		typeBox.addItem("switch");
		typeBox.addItem("terminal");
		typeBox.addItem("webcam");
		typeBox.addItem("web-server");
		typeBox.addItem("wi-fi");
		typeBox.addItem("windows-server");
		typeBox.addItem("windows-workstation");
		typeBox.addItem("workstation");
		typeBox.addItem("unix");
		typeBox.addItem("ups");
		*/
		typeBox.setPreferredSize(new Dimension(100, 20));
		typeBox.setMinimumSize(new Dimension(100, 20));
		typeBox.setFont(NetworkManagerGUI.baseFont);
		typeBox.setBackground(NetworkManagerGUI.sysBackColor);
		c.gridx = 4;
		c.gridy = 2;
		if (!add) {
			typeBox.setSelectedItem(sNode.getnodeType());
			if (sNode.getnodeType().equals("hub")
					|| sNode.getnodeType().equals("network-cloud")) {
				hubLab.setVisible(true);
				ipLab.setVisible(false);
				hubFil.setVisible(true);
				ipFil.setVisible(false);
			} else {
				hubLab.setVisible(false);
				ipLab.setVisible(true);
				hubFil.setVisible(false);
				ipFil.setVisible(true);
			}
		} else {
			typeBox.setSelectedItem("workstation");
			hubLab.setVisible(false);
			ipLab.setVisible(true);
			hubFil.setVisible(false);
			ipFil.setVisible(true);
		}

		opt.add(typeBox, c);
		hubLab.setText("Short Name ");
		c.gridx = 0;
		c.gridy = 4;
		hubLab.setFont(NetworkManagerGUI.baseFont);
		// hubLab.setVisible(false);
		opt.add(hubLab, c);

		ipLab.setText("IP Address ");
		c.gridx = 0;
		c.gridy = 4;
		ipLab.setFont(NetworkManagerGUI.baseFont);
		// ipLab.setVisible(true);
		opt.add(ipLab, c);
		// hubFil = new FixedLengthTextField(15);
		c.gridx = 4;
		c.gridy = 4;
		if (!add) {
			hubFil.setText(sNode.getLabel());
		} else {
			hubFil.setText("");
		}
		// hubFil.setVisible(false);
		opt.add(hubFil, c);
		// ipFil = new FixedLengthTextField(15);
		c.gridx = 4;
		c.gridy = 4;
		// ipFil.setVisible(true);
		IPDocument.setDocument(ipFil);

		if (!add) {
			ipFil.setText(sNode.getIP());
			ipFil.setEditable(false);
		} else {
			String ip = " ";
			/*try {
				InetAddress addr = InetAddress.getLocalHost();
				// Get IP Address
				// byte[] ipAddr = addr.getAddress();
				// Get hostname
				String hostname = addr.getHostName();
				ip = java.net.InetAddress.getByName(hostname).getHostAddress();
			} catch (UnknownHostException e) {
			}
			int temp = ip.trim().lastIndexOf('.');
			String net = ip.substring(0, temp) + ".";
			*/
			IPCalc ipcalc = new IPCalc(currentNetwork);
			ipFil.setText(ipcalc.getHostMin());
		}
		opt.add(ipFil, c);
		/*macLab.setText("MAC Address ");
		c.gridx = 0;
		c.gridy = 6;
		macLab.setFont(NetworkManagerGUI.baseFont);
		opt.add(macLab, c);

		JTextField hwf = new JTextField();
		hwf.setPreferredSize(new Dimension(100, 20));
		hwf.setMinimumSize(new Dimension(100, 20));
		if (!add) {
			if (sNode.getnodeType().equals("hub")
					|| sNode.getnodeType().equals("network-cloud")) {
				hwf.setText("");
			} else {
				hwf.setText(sNode.getMACaddress());
			}
			c.gridx = 4;
			c.gridy = 6;
			hwf.setFont(NetworkManagerGUI.baseFont);
			hwf.setEditable(false);
		} else {
			hwf = new JTextField();
			c.gridx = 4;
			c.gridy = 6;
			hwf.setFont(NetworkManagerGUI.baseFont);
			hwf.setEnabled(false);
			hwf.setBackground(Color.lightGray);

		}
		opt.add(hwf, c);
		*/
		// opt.add(Box.createVerticalStrut(40));
		disLab.setText("Display Name ");
		c.gridx = 0;
		c.gridy = 8;
		disLab.setFont(NetworkManagerGUI.baseFont);
		opt.add(disLab, c);
		// dnslabel
		dnslabbox.setFont(NetworkManagerGUI.baseFont);
		dnslabbox.setBackground(NetworkManagerGUI.sysBackColor);
		c.gridx = 4;
		c.gridy = 8;
		opt.add(dnslabbox, c);

		if (!add) {
			dnslabbox.setSelectedItem(dnsnames[sNode.getDnslabel()]);

		} else {
			dnslabbox.setSelectedItem(dnsnames[2]);
		}
		//
		monLab.setText("Monitoring ");
		c.gridx = 0;
		c.gridy = 10;
		monLab.setFont(NetworkManagerGUI.baseFont);
		opt.add(monLab, c);

		monBox.setText("Yes");
		monBox.setFont(NetworkManagerGUI.baseFont);
		c.gridx = 4;
		c.gridy = 10;
		if (!add) {
			if (sNode.getMonitor()) {
				monBox.setSelected(true);
			}
		} else {
			monBox.setSelected(true);
		}
		opt.add(monBox, c);
		c.anchor = GridBagConstraints.SOUTH;
		if (!add) {
			if (sNode.getnodeType().equals("hub")
					|| sNode.getnodeType().equals("network-cloud")) {
				if (loadIcon){
				wnodeicon.setEnabled(false);
				bnodeicon.setEnabled(false);
				}
				macLab.setEnabled(false);
				disLab.setEnabled(false);
				dnslabbox.setEnabled(false);
				monLab.setEnabled(false);
				monBox.setSelected(false);
				monBox.setEnabled(false);
				snBox.setEnabled(false);
				snBox.setSelected(false);
				rcomlb.setEnabled(false);
				wcomlb.setEnabled(false);
				rcomFil.setEnabled(false);
				wcomFil.setEnabled(false);
			} else {
				if (loadIcon){
				wnodeicon.setEnabled(true);
				bnodeicon.setEnabled(true);
				}
				macLab.setEnabled(true);
				disLab.setEnabled(true);
				dnslabbox.setEnabled(true);
				monLab.setEnabled(true);
				if (sNode.getMonitor()) {
					monBox.setSelected(true);
				} else {
					monBox.setSelected(false);
				}
				monBox.setEnabled(true);
				snBox.setEnabled(true);
				if (sNode.getSnmp()) {
					snBox.setSelected(true);
				} else {
					snBox.setSelected(false);
				}
				rcomlb.setEnabled(true);
				wcomlb.setEnabled(true);
				rcomFil.setEnabled(true);
				wcomFil.setEnabled(true);
			}
		}
		// end opt
		JPanel snmpContP = new JPanel();
		snmpContP.setLayout(new BorderLayout());
		final JPanel snmpP = new JPanel();
		snmpP.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		snmpP.setLayout(new GridBagLayout());
		// snmpP.add(Box.createHorizontalStrut(20));
		snBox.setText("SNMP Managable Node");
		snBox.setFont(NetworkManagerGUI.baseFont);

		c.gridx = 0;
		c.gridy = 8;
		snmpP.add(snBox, c);
		rcomlb.setText("Read Community ");
		rcomlb.setFont(NetworkManagerGUI.baseFont);
		c.gridx = 0;
		c.gridy = 10;
		snmpP.add(rcomlb, c);
		rcomFil.setFont(NetworkManagerGUI.baseFont);
		if (!add && NetworkManagerGUI.textHasContent(sNode.getRcommunity())) {
			rcomFil.setText(sNode.getRcommunity());
		} else {
			rcomFil.setText("public");
		}
		c.gridx = 0;
		c.gridy = 12;
		snmpP.add(rcomFil, c);
		wcomlb.setText("Write Community ");
		wcomlb.setFont(NetworkManagerGUI.baseFont);
		c.gridx = 0;
		c.gridy = 14;
		snmpP.add(wcomlb, c);
		wcomFil.setFont(NetworkManagerGUI.baseFont);
		if (!add && NetworkManagerGUI.textHasContent(sNode.getWcommunity())) {
			wcomFil.setText(sNode.getWcommunity());
		} else {
			wcomFil.setText("private");
		}
		c.gridx = 0;
		c.gridy = 16;
		snmpP.add(wcomFil, c);
		// oids
		//		
		if (!add) {
			oids = sNode.getSNMPOids();
		} else {
			oids = new Vector();
		}
		final JLabel oidLab = new JLabel("Monitoring SNMP Oids ");
		c.gridx = 0;
		c.gridy = 18;
		oidLab.setFont(NetworkManagerGUI.baseFont);
		snmpP.add(oidLab, c);
		oidmodel = new DefaultListModel();
		oidList = new JList(oidmodel);
		oidList.setBackground(NetworkManagerGUI.sysBackColor);
		oidList.setFont(NetworkManagerGUI.baseFont);
		if (!add) {
			for (int i = 0; i < sNode.getSNMPOids().size(); i++) {
				OidToCheck oid = (OidToCheck) sNode.getSNMPOids().get(i);
				oidmodel.addElement(oid.getOidName());
			}
		}
		JScrollPane oidscroll = new JScrollPane(oidList);
		c.gridx = 0;
		c.gridy = 20;
		oidscroll.setPreferredSize(new Dimension(250, 60));
		oidscroll.setMinimumSize(new Dimension(250, 60));
		snmpP.add(oidscroll, c);
		JPanel oidbutP = new JPanel();
		oidbutP.setLayout(new FlowLayout(FlowLayout.CENTER));
		final JButton addoidBut = new JButton("Add");
		addoidBut.setBackground(NetworkManagerGUI.sysBackColor);
		java.net.URL imageURL = NetworkManagerGUI.class
				.getResource("icons/add.gif");
		ImageIcon oidicon = new ImageIcon(imageURL);
		addoidBut.setIcon(oidicon);
		addoidBut.setVerticalTextPosition(SwingConstants.CENTER);
		addoidBut.setHorizontalTextPosition(SwingConstants.RIGHT);
		final JButton deloidBut = new JButton("Delete");
		deloidBut.setBackground(NetworkManagerGUI.sysBackColor);
		imageURL = NetworkManagerGUI.class.getResource("icons/del.gif");
		oidicon = new ImageIcon(imageURL);
		deloidBut.setIcon(oidicon);
		deloidBut.setVerticalTextPosition(SwingConstants.CENTER);
		deloidBut.setHorizontalTextPosition(SwingConstants.RIGHT);
		addoidBut.setFont(NetworkManagerGUI.baseFont);
		oidbutP.add(addoidBut);
		addoidBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new OidView();
			}
		});

		deloidBut.setFont(NetworkManagerGUI.baseFont);
		oidbutP.add(deloidBut);

		deloidBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = oidList.getSelectedIndex();
				String oidname = String.valueOf(oidList.getSelectedValue());
				OidToCheck oid = null;
				if (index != -1) {
					for (int i = 0; i < oids.size(); i++) {
						OidToCheck oidt = (OidToCheck) oids.get(i);
						if (oidt.getOidName().equals(oidname))
							oid = oidt;
					}
					oidmodel.remove(index);
					oids.remove(oid);
				}
			}
		});
		oidmodel.addListDataListener(new ListDataListener() {
			public void contentsChanged(ListDataEvent lde) {
				if (oidmodel.size() != 0) {
					deloidBut.setEnabled(true);
				} else {
					deloidBut.setEnabled(false);
				}
				snmpP.repaint();
			}

			public void intervalAdded(ListDataEvent arg0) {
				if (oidmodel.size() != 0) {
					deloidBut.setEnabled(true);
					snmpP.repaint();
				}
			}

			public void intervalRemoved(ListDataEvent arg0) {
				if (oidmodel.size() == 0) {
					deloidBut.setEnabled(false);
					snmpP.repaint();
				}
			}
		});
		snmpContP.add(snmpP, BorderLayout.CENTER);
		snmpContP.add(oidbutP, BorderLayout.SOUTH);

		// services panel
		JPanel portsContP = new JPanel();
		portsContP.setLayout(new BorderLayout());
		final JPanel servP = new JPanel();
		servP.setLayout(new GridBagLayout());
		// servP.add(Box.createHorizontalStrut(20));
		JLabel servlb = new JLabel("Check Node Services ");
		servlb.setFont(NetworkManagerGUI.baseFont);
		c.gridx = 0;
		c.gridy = 0;
		// servP.add(servlb, c);
		Vector<String> portnames = new Vector<String>();
		portnames.add("FTP");
		portnames.add("SSH");
		portnames.add("SMTP");
		portnames.add("POP3");
		portnames.add("IMAP");
		portnames.add("HTTP");
		portnames.add("SAMBA");
		portnames.add("CITRIX");

		final DefaultComboBoxModel pModel = new DefaultComboBoxModel();
		if (!add) {
			for (int i = 0; i < sNode.getCheckPorts().size(); i++) {
				Service t = (Service) sNode.getCheckPorts().get(i);
				portnames.remove(t.getServiceName());
			}
			for (int i = 0; i < portnames.size(); i++) {
				pModel.addElement(portnames.get(i).toString());
			}
		} else {
			for (int i = 0; i < portnames.size(); i++) {
				pModel.addElement(portnames.get(i).toString());
			}
		}
		portsbox = new JComboBox(pModel);
		portsbox.setBackground(NetworkManagerGUI.sysBackColor);
		portsbox.setPreferredSize(new Dimension(100, 20));
		portsbox.setMinimumSize(new Dimension(100, 20));
		c.gridx = 0;
		c.gridy = 2;

		portsbox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				curservice = (String) e.getItem();

			}
		});
		portsbox.setFont(NetworkManagerGUI.baseFont);
		curservice = (String) portsbox.getSelectedItem();
		servP.add(portsbox, c);
		portsmodel = new DefaultListModel();
		portsList = new JList(portsmodel);
		portsList.setBackground(NetworkManagerGUI.sysBackColor);
		portsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		portsList.setFont(NetworkManagerGUI.baseFont);
		if (!add) {
			for (int i = 0; i < sNode.getCheckPorts().size(); i++) {
				Service t = (Service) sNode.getCheckPorts().get(i);
				portsmodel.addElement(t.getServiceName());
			}
		}
		JScrollPane cmdscroll = new JScrollPane(portsList);
		cmdscroll.setPreferredSize(new Dimension(250, 120));
		cmdscroll.setMinimumSize(new Dimension(250, 120));
		c.gridx = 0;
		c.gridy = 4;
		servP.add(cmdscroll, c);
		if (!add) {
			ports = sNode.getCheckPorts();
		} else {
			ports = new Vector();
		}
		JPanel pbutP = new JPanel();
		pbutP.setLayout(new FlowLayout(FlowLayout.CENTER));
		final JButton addpBut = new JButton("Add");
		addpBut.setBackground(NetworkManagerGUI.sysBackColor);
		imageURL = NetworkManagerGUI.class.getResource("icons/add.gif");
		ImageIcon cmdicon = new ImageIcon(imageURL);
		addpBut.setIcon(cmdicon);
		addpBut.setVerticalTextPosition(SwingConstants.CENTER);
		addpBut.setHorizontalTextPosition(SwingConstants.RIGHT);
		final JButton delpBut = new JButton("Delete");
		delpBut.setBackground(NetworkManagerGUI.sysBackColor);
		imageURL = NetworkManagerGUI.class.getResource("icons/del.gif");
		cmdicon = new ImageIcon(imageURL);
		delpBut.setIcon(cmdicon);
		delpBut.setVerticalTextPosition(SwingConstants.CENTER);
		delpBut.setHorizontalTextPosition(SwingConstants.RIGHT);
		addpBut.setFont(NetworkManagerGUI.baseFont);
		pbutP.add(addpBut);
		addpBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = portsbox.getSelectedIndex();
				int size = pModel.getSize();
				if (size != 0) {
					portsmodel.addElement(curservice);
					ports.add(new Service(curservice, true));
					pModel.removeElementAt(index);
				}
				if (pModel.getSize() == 0)
					addpBut.setEnabled(false);
			}
		});

		delpBut.setFont(NetworkManagerGUI.baseFont);
		pbutP.add(delpBut);
		if (!add && sNode.getCheckPorts().size() == 0)
			delpBut.setEnabled(false);
		else if (add)
			delpBut.setEnabled(false);
		delpBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = portsList.getSelectedIndex();
				if (index != -1) {
					String port = String.valueOf(portsList.getSelectedValue());
					for (int i = 0; i < ports.size(); i++) {
						Service t = (Service) ports.get(i);
						if (t.getServiceName().equals(port))
							ports.remove(i);
					}
					portsmodel.remove(index);
					pModel.addElement(port);
					if (pModel.getSize() != 0)
						addpBut.setEnabled(true);
					;
				}
			}
		});
		portsmodel.addListDataListener(new ListDataListener() {
			public void contentsChanged(ListDataEvent lde) {
				if (portsmodel.size() != 0) {
					delpBut.setEnabled(true);
				} else {
					delpBut.setEnabled(false);
				}
				servP.repaint();
			}

			public void intervalAdded(ListDataEvent arg0) {
				if (portsmodel.size() != 0) {
					delpBut.setEnabled(true);
				}
				servP.repaint();
			}

			public void intervalRemoved(ListDataEvent arg0) {
				if (portsmodel.size() == 0) {
					delpBut.setEnabled(false);
				}
				servP.repaint();
			}
		});
		portsContP.add(servP, BorderLayout.CENTER);
		portsContP.add(pbutP, BorderLayout.SOUTH);

		typeBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				String s = (String) e.getItem();
				iconPath ="pics/" + s + ".gif";
				Image img = null;
				try {
					File sfile = new File(iconPath);
					img = ImageIO.read(sfile);
				} catch (IOException ex){
					
				}
				if (img!=null){
					
				loadIcon=true;	
				Image rImage = img.getScaledInstance(48, 48, Image.SCALE_DEFAULT);
				ImageIcon icon = new ImageIcon(rImage);
				ImageIcon wicon = new ImageIcon(ColorIcon.colorize(icon
						.getImage(), Color.orange));
				ImageIcon bicon = new ImageIcon(ColorIcon.colorize(icon
						.getImage(), Color.red));
				nodeicon.setIcon(icon);
				wnodeicon.setIcon(wicon);
				bnodeicon.setIcon(bicon);
				if (s.equals("hub") || s.equals("network-cloud")) {
					wnodeicon.setEnabled(false);
					bnodeicon.setEnabled(false);
					hubLab.setVisible(true);
					ipLab.setVisible(false);
					hubFil.setVisible(true);
					ipFil.setVisible(false);
					macLab.setEnabled(false);
					disLab.setEnabled(false);
					dnslabbox.setEnabled(false);
					monLab.setEnabled(false);
					monBox.setSelected(false);
					monBox.setEnabled(false);
					snBox.setSelected(false);
					snBox.setEnabled(false);
					rcomlb.setEnabled(false);
					wcomlb.setEnabled(false);
					rcomFil.setEnabled(false);
					wcomFil.setEnabled(false);
					portsmodel.removeAllElements();
					portsList.setEnabled(false);
					addpBut.setEnabled(false);
					delpBut.setEnabled(false);
					portsbox.setEnabled(false);
					oidLab.setEnabled(false);
					addoidBut.setEnabled(false);
					deloidBut.setEnabled(false);
					oidmodel.removeAllElements();
					oidList.setEnabled(false);
				} else {
					wnodeicon.setEnabled(true);
					bnodeicon.setEnabled(true);
					hubLab.setVisible(false);
					ipLab.setVisible(true);
					hubFil.setVisible(false);
					ipFil.setVisible(true);
					macLab.setEnabled(true);
					disLab.setEnabled(true);
					dnslabbox.setEnabled(true);
					monLab.setEnabled(true);
					monBox.setEnabled(true);
					snBox.setEnabled(true);
					rcomlb.setEnabled(true);
					wcomlb.setEnabled(true);
					rcomFil.setEnabled(true);
					wcomFil.setEnabled(true);
					portsList.setEnabled(true);
					addpBut.setEnabled(true);
					delpBut.setEnabled(true);
					portsbox.setEnabled(true);
					addoidBut.setEnabled(true);
					deloidBut.setEnabled(true);
					oidLab.setEnabled(true);
					oidList.setEnabled(true);
				}
				iconPanel.removeAll();
				nodeicon.setVerticalTextPosition(SwingConstants.BOTTOM);
				nodeicon.setHorizontalTextPosition(SwingConstants.CENTER);
				nodeicon.setIcon(icon);
				nodeicon.setText("OK");
				wicon = new ImageIcon(ColorIcon.colorize(icon.getImage(),
						new Color(255, 128, 64)));
				bicon = new ImageIcon(ColorIcon.colorize(icon.getImage(),
						Color.red));
				wnodeicon.setIcon(wicon);
				wnodeicon.setText("WARNING");
				wnodeicon.setForeground(new Color(255, 128, 64));
				wnodeicon.setVerticalTextPosition(SwingConstants.BOTTOM);
				wnodeicon.setHorizontalTextPosition(SwingConstants.CENTER);
				bnodeicon.setIcon(bicon);
				bnodeicon.setText("DOWN");
				bnodeicon.setForeground(Color.red);
				bnodeicon.setVerticalTextPosition(SwingConstants.BOTTOM);
				bnodeicon.setHorizontalTextPosition(SwingConstants.CENTER);
				iconPanel.add(nodeicon, "west");
				iconPanel.add(Box.createHorizontalStrut(30));
				iconPanel.add(wnodeicon, "center");
				iconPanel.add(Box.createHorizontalStrut(30));
				iconPanel.add(bnodeicon, "east");
				iconPanel.repaint();
				mainP.repaint();
				}else{
					loadIcon=false;
					iconPanel.removeAll();
					JLabel errLab = new JLabel("Can't load " + iconPath);	
					errLab.setFont(NetworkManagerGUI.baseFont);
					iconPanel.add(errLab, "center");
					iconPanel.repaint();
					
					
					
				}
			}
		});
		if (!add) {
			if (sNode.getnodeType().equals("hub")
					|| sNode.getnodeType().equals("network-cloud")) {
				portsmodel.removeAllElements();
				portsList.setEnabled(false);
				addpBut.setEnabled(false);
				delpBut.setEnabled(false);
				portsbox.setEnabled(false);
			} else {
				portsList.setEnabled(true);
				if (pModel.getSize() == 0) {
					addpBut.setEnabled(false);
				} else {
					addpBut.setEnabled(true);
				}
				if (portsmodel.getSize() == 0) {
					delpBut.setEnabled(false);
				} else {
					delpBut.setEnabled(true);
				}
				portsbox.setEnabled(true);
			}
		}
		if (!add) {
			if (sNode.getnodeType().equals("hub")
					|| sNode.getnodeType().equals("network-cloud")
					|| !sNode.getSnmp()) {
				snBox.setSelected(false);
				rcomlb.setEnabled(false);
				wcomlb.setEnabled(false);
				rcomFil.setEnabled(false);
				wcomFil.setEnabled(false);
				addoidBut.setEnabled(false);
				deloidBut.setEnabled(false);
				oidLab.setEnabled(false);
				oidList.setEnabled(false);
			} else {
				snBox.setEnabled(true);
				snBox.setSelected(true);
				rcomFil.setText(sNode.getRcommunity());
				wcomFil.setText(sNode.getWcommunity());
				addoidBut.setEnabled(true);
				// deloidBut.setEnabled(true);
				oidLab.setEnabled(true);
				oidList.setEnabled(true);
			}
		} else {
			snBox.setSelected(false);
			rcomlb.setEnabled(false);
			wcomlb.setEnabled(false);
			rcomFil.setEnabled(false);
			wcomFil.setEnabled(false);
			addoidBut.setEnabled(false);
			deloidBut.setEnabled(false);
			oidLab.setEnabled(false);
			oidList.setEnabled(false);
		}
		snBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (snBox.isSelected()) {
					addoidBut.setEnabled(true);
					// deloidBut.setEnabled(true);
					oidLab.setEnabled(true);
					oidList.setEnabled(true);
					rcomlb.setEnabled(true);
					wcomlb.setEnabled(true);
					rcomFil.setEnabled(true);
					wcomFil.setEnabled(true);
				} else {
					addoidBut.setEnabled(false);
					deloidBut.setEnabled(false);
					oidLab.setEnabled(false);
					oidList.setEnabled(false);
					rcomlb.setEnabled(false);
					wcomlb.setEnabled(false);
					rcomFil.setEnabled(false);
					wcomFil.setEnabled(false);
				}
			}
		});
		if (!add && oids.size() == 0) {
			deloidBut.setEnabled(false);
		} else if (add) {
			deloidBut.setEnabled(false);
		}
		// ////////////////////
		// info panel
		JPanel infoP = new JPanel();
		JPanel btnP = new JPanel();
		JPanel imgP = new JPanel();
		btnP.setLayout(new FlowLayout(FlowLayout.LEFT));
		imgP.setLayout(new FlowLayout(FlowLayout.LEFT));
		infoP.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		infoP.setLayout(new BorderLayout());
		textArea = new JTextArea(10, 5);
		textArea.setDocument(new FixedLengthPlainDocument(300));
		textArea.setCaretPosition(textArea.getDocument().getLength());
		if (!add) {
			String[] Info = sNode.getInfo();
			if (Info != null) {
				for (int i = 0; i < Info.length; i++) {
					if (NetworkManagerGUI.textHasContent(Info[i]))
						textArea.append(Info[i] + "\n");
				}
			}
		}
		JScrollPane scroll = new JScrollPane(textArea);
		JLabel infolb = new JLabel("Node Description and Hint Image");
		infolb.setFont(NetworkManagerGUI.baseFont);
		// image
		final JTextField imgtf = new JTextField(20);
		imgtf.setFont(NetworkManagerGUI.baseFont);
		// if (!add && sNode.getTipImage() != null) {
		if (!add && NetworkManagerGUI.textHasContent(sNode.getTipImage())) {
			imgtf.setText(sNode.getTipImage());
		}
		imgP.add(imgtf);
		final JButton selBut = new JButton("Image");
		selBut.setBackground(NetworkManagerGUI.sysBackColor);
		selBut.setFont(NetworkManagerGUI.baseFont);
		imgP.add(selBut);

		selBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				NetworkManagerGUI.recursivelySetFonts(fc,
						NetworkManagerGUI.baseFont);
				fc.addChoosableFileFilter(new ImageFilter());
				if (!add
						&& NetworkManagerGUI
								.textHasContent(sNode.getTipImage())) {
					fc.setCurrentDirectory(new File(sNode.getTipImage()));
				}

				fc.setAcceptAllFileFilterUsed(false);
				int retval = fc.showOpenDialog(null);
				if (retval == JFileChooser.APPROVE_OPTION) {
					File imgFile = fc.getSelectedFile();
					imgtf.setText(imgFile.getAbsolutePath());
					dialog.toFront();
				}
			}
		});

		//
		btnP.add(infolb);
		infoP.add(btnP, BorderLayout.NORTH);
		infoP.add(scroll, BorderLayout.CENTER);
		infoP.add(imgP, BorderLayout.SOUTH);
		
		mainP.setLayout(new BorderLayout());
		mainP.add(iconPanel, BorderLayout.NORTH);
		mainP.add(opt, BorderLayout.CENTER);
		tabs.setFont(NetworkManagerGUI.baseFont);
		tabs.setOpaque(false);
		tabs.addTab("Main", null, mainP);
		tabs.addTab("Info", null, infoP);
		tabs.addTab("SNMP", null, snmpContP);
		tabs.addTab("Services", null, portsContP);

		// ok cancel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton ok = new JButton("OK");
		ok.setBackground(NetworkManagerGUI.sysBackColor);
		ok.setFont(NetworkManagerGUI.baseFont);
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String[] info = new String[300];
				Element root = textArea.getDocument().getDefaultRootElement();
				for (int i = 0; i < root.getElementCount(); i++) {
					Element row = root.getElement(i);
					int start = row.getStartOffset();
					int end = row.getEndOffset();
					try {
						info[i] = textArea.getDocument().getText(start,
								end - start);
						if (info[i].length() > 0) { // remove last character
							info[i] = info[i]
									.substring(0, info[i].length() - 1);
						}
					} catch (BadLocationException ex) {
					}
				}
				String rcom = rcomFil.getText().trim();
				String wcom = wcomFil.getText().trim();
				String toolImg = imgtf.getText().trim();

				String type = (String) typeBox.getSelectedItem();
				if (type.equals("hub") || type.equals("network-cloud")) {
					String name = hubFil.getText().trim().replaceAll(" ", "");
					if (!NetworkManagerGUI.textHasContent(name)) {
						// JOptionPane.showMessageDialog(dialog,
						new MessageDialog(dialog, "Enter short name, please",
								"Node name");
					} else {
						// String type = (String) typeBox.getSelectedItem();

						if (!add) {
							sNode.setLabel(name);
							sNode.setnodeType(type);
							sNode.setMonitor(false);
							sNode.setDnslabel(0);
							sNode.setNetwork(NetworkManager.currentNetwork);
							sNode.setSnmp(false);
							sNode.setRcommunity("");
							sNode.setWcommunity("");
							sNode.setCheckPorts(new Vector());
							sNode.setSNMPOids(new Vector());
							sNode.setInfo(info);
							sNode.setTipImage(toolImg);
						} else {
							sNode = new Node(name, NetworkManager.currentNetwork,"", new Point(100, 100),
									type, 0, false, false, "", "",
									new Vector(), new Vector(), 0, 0, 0, 0, "",
									info, toolImg);
							NetworkManager.aGraph.addNode(sNode);
						}
						dialog.setVisible(false);
						dialog.dispose();
						NetworkManagerGUI.manager.repaint();
					}
				} else {
					String name = ipFil.getText().replaceAll(" ", "");
					if (!ValidIP.isValidIp(name)) {
						// JOptionPane.showMessageDialog(dialog,
						new MessageDialog(dialog,
								"Enter valid IP address, please",
								"Node IP address");
					} else {
						boolean dup = false;
						StringTokenizer inputStringTokens = new StringTokenizer(currentNetwork);
					    String tmpip = inputStringTokens.nextToken("/");
						String mask = inputStringTokens.nextToken("/");
						IPCalc ipcalc = new IPCalc(name + "/"+mask);
						boolean isThisNet = ipcalc.checkNetwork();
						if (!isThisNet){
							new MessageDialog(dialog,
									"Not Valid Network",
									"Node IP address");
						}
						for (int i = 0; i < Graph.nodes.size(); i++) {
							Node fNode = (Node) Graph.nodes.get(i);
							if (NetworkManagerGUI.textHasContent(fNode.getIP())
									&& fNode.getIP().equals(name)&& add) 
									dup = true;
									
								
						}
						if (dup)
						new MessageDialog(dialog,
								"Duplicate IP address",
								"Node IP address");
						else if (!dup && isThisNet) {
							int dnslab = 1;
							boolean monitor = monBox.isSelected();
							String s = (String) dnslabbox.getSelectedItem();
							if (s.equals(dnsnames[4]))
								dnslab = 4;
							else if (s.equals(dnsnames[3]))
								dnslab = 3;
							else if (s.equals(dnsnames[2]))
								dnslab = 2;
							else if (s.equals(dnsnames[1]))
								dnslab = 1;
							else
								dnslab = 0;
							boolean snmp = snBox.isSelected();
							if (!add && !dup) {
								String oldIP = sNode.getIP();
								sNode.setLabel(name);
								sNode.setIP(name);
							
								if (!oldIP.equals(name))
									sNode.setMACaddress();// update
								// mac-address
								sNode.setNetwork(NetworkManager.currentNetwork);
								sNode.setnodeType(type);
								sNode.setMonitor(monitor);
								sNode.setDnslabel(dnslab);
								sNode.setSnmp(snmp);
								sNode.setRcommunity(rcom);
								sNode.setWcommunity(wcom);
								sNode.setMACaddress();
								sNode.setCheckPorts(ports);
								sNode.setSNMPOids(oids);
								sNode.setInfo(info);
								sNode.setTipImage(toolImg);
								
							} else {
								sNode = new Node(name, NetworkManager.currentNetwork,"", new Point(100, 100),
										type, dnslab, monitor, snmp, rcom,
										wcom, ports, oids, 0, 0, 0, 0, "",
										info, toolImg);
								NetworkManager.aGraph.addNode(sNode);
								if (NetworkManagerGUI.MONITORING) {
									sNode.setStartTime();
								}

							}
							dialog.setVisible(false);
							dialog.dispose();
							NetworkManagerGUI.manager.repaint();
							if (sNode.getDnslabel() == 4) {
								if (sNode.getSnmp()) {
									String snmplabel = SNMPget.get(sNode,
											"1.3.6.1.2.1.1.5.0"); // SysName
									if (NetworkManagerGUI
											.textHasContent(snmplabel)) {
										sNode.setLabel(snmplabel);
									}
								}
							} else if (sNode.getDnslabel() == 1
									|| sNode.getDnslabel() == 2
									|| sNode.getDnslabel() == 3) {
								// sNode.setLabel(sNode.getHostname());
								new setBindName(1, sNode, true);
							} else {
								new setBindName(1, sNode, false);
							}
							if (sNode.getSnmp()) {
								String snmpType = SNMPget.get(sNode,
										"1.3.6.1.2.1.1.2.0"); // type
								if (NetworkManagerGUI.textHasContent(snmpType)
										&& !snmpType.equals("null")) {
									boolean ok = snmpType.startsWith("1.3.6");
									if (!ok) {
										sNode.setSnmp(false);
										NetworkManagerGUI.manager.repaint();
										new MessageDialog(
												dialog,
												"Can't identify SNMP SysObjectID",
												"SNMP Identification");
										sNode.setSnmp(false);
									}
								} else {
									sNode.setSnmp(false);
									NetworkManagerGUI.manager.repaint();
									new MessageDialog(dialog,
											"Can't identify SNMP SysObjectID",
											"SNMP Identification");
									sNode.setSnmp(false);
								}
							}
							if (sNode.getSnmp())
								new SNMPIdentify(1, sNode);

						}
					}
				}
			}
		});

		buttonPanel.add(ok);
		JButton cancel = new JButton("Cancel");
		cancel.setBackground(NetworkManagerGUI.sysBackColor);
		buttonPanel.add(cancel);
		cancel.setFont(NetworkManagerGUI.baseFont);
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
				dialog.dispose();
			}
		});

		container.add(tabs, BorderLayout.CENTER);
		container.add(buttonPanel, BorderLayout.SOUTH);
		dialog.getContentPane().add(container);
		dialog.setVisible(true);
		
	}

	public static String secondsToString(long time) {
		long hseconds, seconds, minutes, hours, days;
		long tt = time;

		days = tt / 8640000;
		tt %= 8640000;

		hours = tt / 360000;
		tt %= 360000;

		minutes = tt / 6000;
		tt %= 6000;

		seconds = tt / 100;
		tt %= 100;

		hseconds = tt;

		Long[] values = new Long[5];
		values[0] = new Long(days);
		values[1] = new Long(hours);
		values[2] = new Long(minutes);
		values[3] = new Long(seconds);
		values[4] = new Long(hseconds);
		String secondsStr = (seconds < 10 ? "0" : "") + seconds;
		String minutesStr = (minutes < 10 ? "0" : "") + minutes;
		String hoursStr = (hours < 10 ? "0" : "") + hours;

		return new String("(" + days + " days) " + hoursStr + ": " + minutesStr
				+ ":" + secondsStr);

	}

	public static int getLineCount(JTextArea _textArea) {
		boolean lineWrapHolder = _textArea.getLineWrap();
		_textArea.setLineWrap(false);
		double height = _textArea.getPreferredSize().getHeight();
		_textArea.setLineWrap(lineWrapHolder);
		double rowSize = height / _textArea.getLineCount();
		return (int) (_textArea.getPreferredSize().getHeight() / rowSize);
	}

	Rectangle getDrawableRect(Rectangle originalRect, Dimension drawingArea) {
		int x = originalRect.x;
		int y = originalRect.y;
		int width = originalRect.width;
		int height = originalRect.height;

		// Make sure rectangle width and height are positive.
		if (width < 0) {
			width = 0 - width;
			x = x - width + 1;
			if (x < 0) {
				width += x;
				x = 0;
			}
		}
		if (height < 0) {
			height = 0 - height;
			y = y - height + 1;
			if (y < 0) {
				height += y;
				y = 0;
			}
		}

		// The rectangle shouldn't extend past the drawing area.
		if ((x + width) > drawingArea.width) {
			width = drawingArea.width - x;
		}
		if ((y + height) > drawingArea.height) {
			height = drawingArea.height - y;
		}

		return new Rectangle(x, y, width, height);
	}

	public static JMenuItem AddCmdItem() {
		JMenuItem menuItem = null;

		for (int i = 0; i < NetworkManagerGUI.extCommands.size(); i++) {
			final ExtCmd cmdt = (ExtCmd) NetworkManagerGUI.extCommands
					.elementAt(i);
			String menuEntry = cmdt.getCmdName();
			menuItem = new JMenuItem(menuEntry);
			menuItem.setFont(NetworkManagerGUI.baseFont);
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JMenuItem source = (JMenuItem) (e.getSource());
					if (cmdt.getCmdName().equals(source.getActionCommand()))
						cmdt.Execute();
				}

			});
		}
		return menuItem;
	}
	


}