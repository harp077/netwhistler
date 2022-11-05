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

import java.awt.*;
import java.awt.event.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import nnm.inet.IPCalc;
import nnm.inet.Service;
import nnm.inet.ValidIP;
import nnm.inet.Wizard;
import nnm.util.ConfirmDialog;
import nnm.util.FixedLengthTextField;
import nnm.util.IPDocument;
import nnm.util.MessageDialog;


// Referenced classes of package nnm:
//            NetworkManagerGUI, Network, MapTree, Graph, 
//            Node, NetworkManager

public class NetPanel extends JPanel
    implements MouseListener, MouseMotionListener
{
	public static boolean Discover;
    public static Vector nets;
    private String type;
    public JComboBox typeBox;
    public static Point p;
    public static JPopupMenu netMenu;
    public static JPopupMenu addMenu;
    public static JPopupMenu linePopupMenu, RoutPopupMenu;
    public static String maskVal;
    public static String community;
    public static String nettmp;
    private Network dragNet;
    public static JMenuItem discItem,delItem,linkItem,pingItem;
    public static Network startNet;
    private Point dragPoint;
    public static Color backgroundColor=new Color(235,235,235);
    private Point elasticEndLocation;
	public Rectangle currentRect;
	public static boolean MODE = true;
    public NetPanel()
    {
        nets = new Vector();
       
        addEventHandlers();
        setBackground(backgroundColor);
        ToolTipManager manager = ToolTipManager.sharedInstance();
        manager.setDismissDelay(0x36ee80);
        setLayout(new BorderLayout());
        setBackground(NetworkManagerGUI.backgroundColor);
        netMenu = new JPopupMenu();
        addMenu = new JPopupMenu();
        discItem = new JMenuItem("Discover");
        pingItem = new JMenuItem("Ping");
        delItem = new JMenuItem("Delete");
        discItem.setFont(NetworkManagerGUI.baseFont);
        pingItem.setFont(NetworkManagerGUI.baseFont);
        delItem.setFont(NetworkManagerGUI.baseFont);
        linkItem = new JMenuItem("Connect To ...");
		linkItem.setFont(NetworkManagerGUI.baseFont);
		linkItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Network sNet = networkAt(p);
				MODE = false;
				startNet = sNet;
				dragNet = sNet;
				elasticEndLocation = p;
			}
		});
		pingItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Network sNet = networkAt(p);
				FPinger pinger = new FPinger();
				boolean stat=sNet.getBadStatus();
				String addr = sNet.getNetwork();
				if (sNet.getType()==1){
				if (pinger.Fping(addr)) {
					JDialog tmp = new JDialog();
					new MessageDialog(tmp, " " + sNet.getNetwork() + " is alive " + "("
							+ FPinger.pingTm + " ms)", "Fping results");
					sNet.setBadStatus(false);
										
				} else {
					JDialog tmp = new JDialog();
					new MessageDialog(tmp, " " + sNet.getNetwork() + " is down ",
							"Fping results");
					sNet.setBadStatus(true);
				}	
				}	
				NetworkManagerGUI.netPanel.repaint();
				NetworkManagerGUI.treePanel.refresh();
			}
		});
		discItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                Network sNet = networkAt(NetPanel.p);
                NetworkManager.currentNetwork = sNet.getNetwork();
                NetworkManagerGUI.wizCommunity = sNet.getCommunity();
                new Wizard(1, sNet.getNetwork());
            }

        });
        delItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                Network sNet = networkAt(NetPanel.p);
                ConfirmDialog dlg = new ConfirmDialog(null, "Are you sure?", "Delete Network");
                boolean yes = dlg.getAction();
                if(yes)
                    deleteNetwork(sNet);
            }

        });
        netMenu.add(discItem);
        netMenu.add(pingItem);
        netMenu.add(linkItem);
        netMenu.add(delItem);
        
        JMenuItem menuLineDel = new JMenuItem("Delete");
		menuLineDel.setFont(NetworkManagerGUI.baseFont);
		linePopupMenu = new JPopupMenu();
		linePopupMenu.add(menuLineDel);
		menuLineDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EdgeN sEdgeN = edgeNAt(p);
				deleteEdge(sEdgeN);
				// updateFrame();
				repaint();
			}
		});
        JMenuItem addItem = new JMenuItem("Add Network");
        addItem.setFont(NetworkManagerGUI.baseFont);
        addItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                addNetworkDialog();
            }

        });
        addMenu.add(addItem);
        JMenuItem addRItem = new JMenuItem("Add Router/Firewall");
		addRItem.setFont(NetworkManagerGUI.baseFont);
		addRItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                addRouterDialog();
            }

        });
		 addMenu.add(addRItem);
		 JMenuItem backItem = new JMenuItem("Background Color");
	        backItem.setFont(NetworkManagerGUI.baseFont);
	        backItem.addActionListener(new ActionListener() {

	            public void actionPerformed(ActionEvent e)
	            {
	            	
	    				JColorChooser colorChooser = new JColorChooser(backgroundColor);
	    				JDialog Coldialog = JColorChooser.createDialog(null,
	    						"Select a Background Color", true, colorChooser, null, null);
	    				NetworkManagerGUI.recursivelySetFonts(Coldialog,
	    						NetworkManagerGUI.baseFont);
	    				Coldialog.setResizable(false);
	    				Coldialog.setVisible(true);
	    				Color c = colorChooser.getColor();
	    				if (c != null) {
	    					backgroundColor = c;
	    					setBackground(backgroundColor);
	    					repaint();
	    				}
	    			
	            }

	        });
	        addMenu.add(backItem);
    }

    public void addNetworks()
    {
        for(int i = 0; i < nets.size(); i++)
        {
            Network network = (Network)nets.get(i);
            MapTree.ipnet.add(new DefaultMutableTreeNode(network.getNetwork()));
        }

        MapTree.refresh();
    }

    public void addNetwork(Network net)
    {
        nets.add(net);
        MapTree.ipnet.add(new DefaultMutableTreeNode(net.getNetwork()));
        repaint();
        MapTree.refresh();
    }
    public static Vector getEdges() {
		Vector edges = new Vector();
		Enumeration allNets = nets.elements();
		while (allNets.hasMoreElements()) {

			Enumeration someEdges = ((Network) allNets.nextElement())
					.incidentEdges().elements();
			while (someEdges.hasMoreElements()) {
				EdgeN anEdgeN = (EdgeN) someEdges.nextElement();
				if (!edges.contains(anEdgeN)) {
					edges.add(anEdgeN);
				}
			}
		}
		return edges;
	}

    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
		Graphics2D g2d = (Graphics2D) g;
        Dimension frameSize = Toolkit.getDefaultToolkit().getScreenSize();
        int d = frameSize.height;
        int z = frameSize.width;
        int x = 80;
        int y = 50;
        Vector edges = getEdges();
		// Draw the edges
		for (int i = 0; i < edges.size(); i++) {
			EdgeN edgeN = (EdgeN) edges.get(i);
			edgeN.draw(g);
		}
		// Draw nets
        for(int i = 0; i < nets.size(); i++)
        {
            Network net = (Network)nets.get(i);
            net.draw(g);
        }
		
        g2d.setColor(NetworkManagerGUI.selColor);
		Stroke stroke = g2d.getStroke();
		float[] dashPattern = { 20, 5, 20, 5 };
		g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10, dashPattern, 0));
		if (!MODE) {
		if (dragNet != null) {
				// if (!dragNode.isSelected()) {
				g2d.drawLine(dragNet.getLocation().x,
						dragNet.getLocation().y, elasticEndLocation.x,
						elasticEndLocation.y);
				// }
			}
		}
		Dimension sd = size();
		g2d.setStroke(stroke);
		// If currentRect exists, paint a rectangle on top.
		if (currentRect != null) {
			Rectangle box = getDrawableRect(currentRect, sd);
			g2d.drawRect(box.x, box.y, box.width - 1, box.height - 1);

		}
    }

    public void deleteNetwork(Network aNet)
    {
    	if (Discover && aNet.getType()==0){
    	JDialog tmp = new JDialog();
    		new MessageDialog(tmp, "Discovering in progress", "Delete Network");
    	} else {
    	if 	(aNet.getType()==0){
    		for(int i = 0; i < Graph.nodes.size(); i++)
        	{
            Node node = (Node)Graph.nodes.get(i);
            if(node.getNetwork().equals(aNet.getNetwork()))
                Graph.nodes.removeElementAt(i);
        	}
        }
        Enumeration someEdges = aNet.incidentEdges().elements();
		while (someEdges.hasMoreElements()) {
			EdgeN anEdgeN = (EdgeN) someEdges.nextElement();
			anEdgeN.otherEndFrom(aNet).incidentEdges().remove(anEdgeN);
		}
        for(int i = 0; i < nets.size(); i++)
        {
            Network net = (Network)nets.get(i);
            if(net.getNetwork().equals(aNet.getNetwork()))
                nets.removeElementAt(i);
        }

        repaint();
        MapTree.removeNetwork(aNet);
    	}
    	}

    public  Network networkAt(Point p)
    {
        for(int i = 0; i < nets.size(); i++)
        {
            Network aNetwork = (Network)nets.get(i);
            int distance = (p.x - aNetwork.getLocation().x) * (p.x - aNetwork.getLocation().x) + (p.y - aNetwork.getLocation().y) * (p.y - aNetwork.getLocation().y);
            if(distance <= Node.RADIUS * Node.RADIUS)
                return aNetwork;
        }

        return null;
    }

    public void mouseClicked(MouseEvent event)
    {
    	if (MODE) {
        Network aNetwork = networkAt(event.getPoint());
        EdgeN anEdgeN = edgeNAt(event.getPoint());
        if(aNetwork == null )
        {
        	if (anEdgeN == null) {
        		        		
        	}else {
				anEdgeN.toggleSelected();
			}
        	
        } else if (event.getClickCount() == 2)
         {
        	 aNetwork.toggleSelected();
            NetworkManagerGUI.topcards.show(NetworkManagerGUI.cardsPanel, "Third");
            NetworkManager.currentNetwork = aNetwork.getNetwork();
        }
    	}
        update();
    }

    public void mousePressed(MouseEvent event)
    {
    	dragPoint = event.getPoint();
        if(event.isPopupTrigger() || event.isMetaDown())
        {
            p = new Point(event.getX(), event.getY());
            Network aNetwork = networkAt(p);
            EdgeN anEdgeN = edgeNAt(p);
           if(aNetwork != null)
           {	
        	   if (aNetwork.getType()==1)
        	   {
        		   discItem.setVisible(false);
           	    	pingItem.setVisible(true);
           		} else {
        	    discItem.setVisible(true);
           		pingItem.setVisible(false);
           		}
           		netMenu.show(event.getComponent(), event.getX(), event.getY());
           		
           }
                else if (anEdgeN != null) {
				linePopupMenu.show(event.getComponent(), event.getX(), event
						.getY());
            }  else
                addMenu.show(event.getComponent(), event.getX(), event.getY());
        } else
        {
            Network aNetwork = networkAt(event.getPoint());
            if(aNetwork != null) {
                setCursor(Cursor.getPredefinedCursor(12));
                dragNet = aNetwork;
				aNetwork.toggleSelected();
            }
            else {
				currentRect = new Rectangle(event.getX(), event.getY(), 0, 0);
			}
        }
    }

    public void mouseReleased(MouseEvent event)
    {
    	if (!event.isPopupTrigger() || !event.isMetaDown()) {
			Network aNet = networkAt(event.getPoint());

			/*
			 * if ((aNode != null) && (aNode != dragNode) &&
			 * !NetworkManager.MODE) { if (dragNode != null) {
			 * aGraph.addEdge(dragNode, aNode); } }
			 */
			if ((aNet != null) && aNet != startNet && !MODE) {
				if (dragNet != null) {
					addEdgeN(aNet.getNetwork(),startNet, aNet);

				}
			}
			if (dragNet != null && dragNet.isSelected()) {
				dragNet.toggleSelected();
			}
			
			
			for (int c = 0; c < nets.size(); c++) {
				Network cNet = (Network) nets.get(c);
				if (currentRect != null) {
					if (currentRect.contains(cNet.getLocation())) {
						cNet.toggleSelected();

					}
				}
			}
			MODE = true;
			currentRect = null;
			dragNet = null;
			startNet = null;
			
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			update();
		}
        
    }

    public void mouseOver(MouseEvent mouseevent)
    {
    }

    public void mouseEntered(MouseEvent mouseevent)
    {
    }

    public void mouseExited(MouseEvent mouseevent)
    {
    }

    public void mouseMoved(MouseEvent event)
    {
        Network aNetwork = networkAt(event.getPoint());
        if (!MODE) {
			elasticEndLocation = event.getPoint();
			repaint();
		}
        
		if (aNetwork != null) {
			
			String ToolTip = "<html><body bgcolor=#FFFFE1><center><b>"; //
			 ToolTip = ToolTip + aNetwork.getNetwork();
			 ToolTip = ToolTip + "</b></center>";
			 int bad = 0;
			 int warn = 0;
			 int all = 0;
				for (int i = 0; i < Graph.nodes.size(); i++) {
					Node aNode = (Node) Graph.nodes.get(i);
					if (aNode.getNetwork().equals(aNetwork.getNetwork())){
					if (aNode.getnodeType().equals("hub")
							|| aNode.getnodeType().equals("network-cloud")) {
						;
					} else {
						all++;
						if (!aNode.getBadStatus()) {
							int badserv = 0;
							for (int s = 0; s < aNode.getCheckPorts().size(); s++) {
								Service t = (Service) aNode.getCheckPorts().get(s);
								if (!t.getStatus())
									badserv = 1;
							}
							if (badserv == 1)
								warn++;
						} else {
							bad++;
						}
					}
					}
				}
                if (aNetwork.getType()==0 && Graph.nodes.size()!=0){ 
				ToolTip = ToolTip + "<table><tr><td>Responding (OK)</td><td>"+ (all - bad - warn)+"</td></tr>";
				ToolTip = ToolTip + "<tr><td>With failed service (WARNING)</td><td>"+ warn+"</td></tr>";
				ToolTip = ToolTip + "<tr><td>Unresponding Nodes (DOWN)</td><td>"+ bad+"</td></tr></table>";
                } else if (aNetwork.getType()==1){
                  boolean state = aNetwork.getBadStatus();
                  if (!state)
                	ToolTip = ToolTip + "<table><tr><td><b>Status:</b></td><td><b>OK</b></td></tr></table>";
                  else
                	  ToolTip = ToolTip + "<table><tr><td><b><font color=red>Status:</font></b></td><td><b><font color=red>DOWN</font></b></td></tr></table>"; 
                }
				setToolTipText(ToolTip);
		} else {
			setToolTipText(null);
		}
    }
    public void deleteEdge(EdgeN anEdgeN) {
		anEdgeN.getStartNet().incidentEdges().remove(anEdgeN);
		anEdgeN.getEndNet().incidentEdges().remove(anEdgeN);
	}
    public Network netNamed(String aLabel) {
		for (int i = 0; i < nets.size(); i++) {
			Network aNet = (Network) nets.get(i);
			if (aNet.getNetwork().equals(aLabel)) {
				return aNet;
			}
		}
		return null;
	}
   
	public void addEdgeN(String net,Network start, Network end) {
		EdgeN anEdgeN = new EdgeN(net,start, end);
		start.addIncidentEdgeN(anEdgeN);
		end.addIncidentEdgeN(anEdgeN);
	}
    public void addEdgeN(String net,String startLabel, String endLabel) {
		Network start, end;

		start = netNamed(startLabel);
		end = netNamed(endLabel);
		if ((start != null) && (end != null)) {
			addEdgeN(net,start, end);
		}
	}
    public void update()
    {
        requestFocus();
        removeEventHandlers();
        repaint();
        addEventHandlers();
    }

    public void addEventHandlers()
    {
        addMouseMotionListener(this);
        addMouseListener(this);
    }

    public void removeEventHandlers()
    {
        removeMouseMotionListener(this);
        removeMouseListener(this);
    }

    public void mouseDragged(MouseEvent event)
    {
    	if (MODE) {
			if (dragNet != null) {
				if (dragNet.isSelected()) {
					for (int n = 0; n < selectedNets().size(); n++) {
						Network aNet = (Network) selectedNets().get(n);
						aNet.getLocation().translate(
								event.getPoint().x - dragPoint.x,
								event.getPoint().y - dragPoint.y);
					}
					dragPoint = event.getPoint();

				} else {
					dragNet.setLocation(event.getPoint());
				}
			}
			if (currentRect != null) {
				currentRect.resize(event.getX() - currentRect.x, event.getY()
						- currentRect.y);
			}
		} else {
			elasticEndLocation = event.getPoint();
		}
		Rectangle r = new Rectangle(event.getX(), event.getY(), 1, 1);
		scrollRectToVisible(r);
		// We have changed the model, so now update
		update();
    }
    public static Vector selectedNets() {
		Vector selected = new Vector();
		Enumeration allNets = nets.elements();

		while (allNets.hasMoreElements()) {
			Network aNet = (Network) allNets.nextElement();
			if (aNet.isSelected()) {
				selected.add(aNet);
			}
		}
		return selected;
	}
    public EdgeN edgeNAt(Point p) {
		Vector edges = getEdges();
		int midPointX, midPointY;

		for (int i = 0; i < edges.size(); i++) {
			EdgeN anEdgeN = (EdgeN) edges.get(i);
			midPointX = (anEdgeN.getStartNet().getLocation().x + anEdgeN
					.getEndNet().getLocation().x) / 2;
			midPointY = (anEdgeN.getStartNet().getLocation().y + anEdgeN
					.getEndNet().getLocation().y) / 2;
			int distance = (p.x - midPointX) * (p.x - midPointX)
					+ (p.y - midPointY) * (p.y - midPointY);
			if (distance <= (Node.RADIUS * Node.RADIUS)) {
				
				return anEdgeN;
			}
		}
		return null;
	}
    public void addNetworkDialog()
    {
        final JDialog dialog = new JDialog();
        dialog.setTitle("Add Network");
        dialog.setSize(260, 190);
        dialog.setResizable(false);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension optSize = dialog.getSize();
        if(optSize.height > screenSize.height)
            optSize.height = screenSize.height;
        if(optSize.width > screenSize.width)
            optSize.width = screenSize.width;
        dialog.setLocation((screenSize.width - optSize.width) / 2, (screenSize.height - optSize.height) / 2);
        dialog.setDefaultCloseOperation(1);
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        JPanel wizardPanel = new JPanel();
        wizardPanel.setBorder(new EtchedBorder(1));
        wizardPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(1, 1, 1, 1);
        c.anchor = 17;
        c.fill = 2;
        JLabel mapLab = new JLabel("Network ");
        mapLab.setFont(NetworkManagerGUI.baseFont);
        c.gridx = 0;
        c.gridy = 0;
        wizardPanel.add(mapLab, c);
        final JTextField nettf = new FixedLengthTextField(15);
        c.gridx = 0;
        c.gridy = 2;
        IPDocument.setDocument(nettf);
        String ip = "";
        try
        {
            InetAddress addr = InetAddress.getLocalHost();
            String hostname = addr.getHostName();
            ip = InetAddress.getByName(hostname).getHostAddress();
        }
        catch(UnknownHostException unknownhostexception) { }
        String net = "127.0.0.0";
        if(NetworkManagerGUI.textHasContent(ip))
        {
            int temp = ip.trim().lastIndexOf('.');
            net = ip.substring(0, temp) + ".0";
        }
        nettf.setText(net);
        wizardPanel.add(nettf, c);
        JLabel maskLab = new JLabel("Mask ");
        maskLab.setFont(NetworkManagerGUI.baseFont);
        c.gridx = 0;
        c.gridy = 4;
        wizardPanel.add(maskLab, c);
        JComboBox maskBox = new JComboBox();
        maskBox.setPreferredSize(new Dimension(100, 20));
        maskBox.setMinimumSize(new Dimension(100, 20));
        maskBox.setBackground(NetworkManagerGUI.sysBackColor);
        maskBox.addItem("255.0.0.0");
        maskBox.addItem("255.128.0.0");
        maskBox.addItem("255.192.0.0");
        maskBox.addItem("255.224.0.0");
        maskBox.addItem("255.240.0.0");
        maskBox.addItem("255.248.0.0");
        maskBox.addItem("255.252.0.0");
        maskBox.addItem("255.254.0.0");
        maskBox.addItem("255.255.0.0");
        maskBox.addItem("255.255.128.0");
        maskBox.addItem("255.255.192.0");
        maskBox.addItem("255.255.224.0");
        maskBox.addItem("255.255.240.0");
        maskBox.addItem("255.255.248.0");
        maskBox.addItem("255.255.252.0");
        maskBox.addItem("255.255.254.0");
        maskBox.addItem("255.255.255.0");
        maskBox.addItem("255.255.255.128");
        maskBox.addItem("255.255.255.192");
        maskBox.addItem("255.255.255.224");
        maskBox.addItem("255.255.255.240");
        maskBox.addItem("255.255.255.248");
        maskBox.addItem("255.255.255.252");
        maskBox.setFont(NetworkManagerGUI.baseFont);
        maskBox.setBackground(NetworkManagerGUI.sysBackColor);
        c.gridx = 0;
        c.gridy = 6;
        maskBox.setSelectedItem("255.255.255.0");
        maskBox.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e)
            {
                NetPanel.maskVal = (String)e.getItem();
            }

        });
        wizardPanel.add(maskBox, c);
        final Hashtable map = new Hashtable();
        map.put("255.0.0.0", "/8");
        map.put("255.128.0.0", "/9");
        map.put("255.192.0.0", "/10");
        map.put("255.224.0.0", "/11");
        map.put("255.240.0.0", "12");
        map.put("255.248.0.0", "/13");
        map.put("255.252.0.0", "/14");
        map.put("255.254.0.0", "/15");
        map.put("255.255.0.0", "/16");
        map.put("255.255.128.0", "/17");
        map.put("255.255.192.0", "/18");
        map.put("255.255.224.0", "/19");
        map.put("255.255.240.0", "/20");
        map.put("255.255.248.0", "/21");
        map.put("255.255.252.0", "/22");
        map.put("255.255.254.0", "/23");
        map.put("255.255.255.0", "/24");
        map.put("255.255.255.128", "/25");
        map.put("255.255.255.192", "/26");
        map.put("255.255.255.224", "/27");
        map.put("255.255.255.240", "/28");
        map.put("255.255.255.248", "/29");
        map.put("255.255.255.252", "/30");
        map.put("255.255.255.254", "/31");
        JLabel comLab = new JLabel("SNMP Community ");
        comLab.setFont(NetworkManagerGUI.baseFont);
        c.gridx = 0;
        c.gridy = 8;
        wizardPanel.add(comLab, c);
        final JTextField comtf = new JTextField(20);
        comtf.setText(NetworkManagerGUI.wizCommunity);
        comtf.setFont(NetworkManagerGUI.baseFont);
        c.gridx = 0;
        c.gridy = 10;
        wizardPanel.add(comtf, c);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(2));
        JButton ok = new JButton("OK");
        ok.setBackground(NetworkManagerGUI.sysBackColor);
        ok.setFont(NetworkManagerGUI.baseFont);
        JButton cancel = new JButton("Cancel");
        cancel.setBackground(NetworkManagerGUI.sysBackColor);
        buttonPanel.add(ok);
        buttonPanel.add(cancel);
        cancel.setFont(NetworkManagerGUI.baseFont);
        community = null;
        nettmp = null;
        ok.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                NetPanel.community = comtf.getText().trim();
                NetPanel.nettmp = nettf.getText().replaceAll(" ", "");
                if(!ValidIP.isValidIp(NetPanel.nettmp))
                {
                    new MessageDialog(dialog, "Enter valid IP address, please", "Network IP address");
                } else
                {
                    if(NetPanel.maskVal == null || !NetworkManagerGUI.textHasContent(NetPanel.maskVal))
                        NetPanel.maskVal = "255.255.255.0";
                    String mask = map.get(NetPanel.maskVal).toString();
                    Network network = new Network(NetPanel.nettmp + mask, new Point(100, 100), NetPanel.community,0,0);
                    IPCalc ipcalc = new IPCalc(network.getNetwork());
                    if(!ipcalc.isThisNetwork())
                    {
                        new MessageDialog(dialog, "Not Valid Network", "Network Address");
                    } else
                    {
                        boolean ok = true;
                        for(int i = 0; i < NetPanel.nets.size(); i++)
                        {
                            Network ntmp = (Network)NetPanel.nets.get(i);
                            if(ntmp.getNetwork().equals(network.getNetwork()))
                                ok = false;
                        }

                        if(!ok)
                            new MessageDialog(dialog, "Duplicate Network", "Network Address");
                        else
                        if(ok)
                        {
                            addNetwork(network);
                            NetPanel.maskVal = "";
                            dialog.setVisible(false);
                            dialog.dispose();
                        }
                    }
                }
            }

        });
        cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                dialog.setVisible(false);
                dialog.dispose();
            }

        });
        container.add(wizardPanel, "Center");
        container.add(buttonPanel, "South");
        dialog.getContentPane().add(container);
        dialog.setVisible(true);
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
	
	
	public void addRouterDialog()
    {
        
		final JDialog dialog = new JDialog();
        dialog.setTitle("Add Router/Firewall");
        dialog.setSize(220, 130);
        dialog.setResizable(false);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension optSize = dialog.getSize();
        if(optSize.height > screenSize.height)
            optSize.height = screenSize.height;
        if(optSize.width > screenSize.width)
            optSize.width = screenSize.width;
        dialog.setLocation((screenSize.width - optSize.width) / 2, (screenSize.height - optSize.height) / 2);
        dialog.setDefaultCloseOperation(1);
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        JPanel wizardPanel = new JPanel();
        wizardPanel.setBorder(new EtchedBorder(1));
        wizardPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(1, 1, 1, 1);
        c.anchor = 17;
        c.fill = 2;
        typeBox = new JComboBox();
        typeBox.setPreferredSize(new Dimension(100, 20));
        typeBox.setMinimumSize(new Dimension(100, 20));
        typeBox.setBackground(NetworkManagerGUI.sysBackColor);
        typeBox.setFont(NetworkManagerGUI.baseFont);
        typeBox.addItem("router");
        typeBox.addItem("firewall");
        typeBox.setSelectedItem("router");
        typeBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e)
            {
                type = (String)e.getItem();
            }
        });
        c.gridx = 0;
        c.gridy = 0;
        wizardPanel.add(typeBox, c);
        JLabel mapLab = new JLabel("IP Address");
        mapLab.setFont(NetworkManagerGUI.baseFont);
        c.gridx = 0;
        c.gridy = 2;
        wizardPanel.add(mapLab, c);
        final JTextField nettf = new FixedLengthTextField(15);
        c.gridx = 0;
        c.gridy = 4;
        IPDocument.setDocument(nettf);
        wizardPanel.add(nettf, c);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(2));
        JButton ok = new JButton("OK");
        ok.setBackground(NetworkManagerGUI.sysBackColor);
        ok.setFont(NetworkManagerGUI.baseFont);
        JButton cancel = new JButton("Cancel");
        cancel.setBackground(NetworkManagerGUI.sysBackColor);
        buttonPanel.add(ok);
        buttonPanel.add(cancel);
        cancel.setFont(NetworkManagerGUI.baseFont);
       
        ok.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                type = (String)typeBox.getSelectedItem();
                int icon;
                if (type.equals("router"))
                	icon=1;
                else
                	icon=2;
            	String ip = nettf.getText().trim().replaceAll(" ","");
                if(!ValidIP.isValidIp(ip))
                {
                    new MessageDialog(dialog, "Enter valid IP address, please", "IP Address");
                } else 	{
                        boolean ok = true;
                        for(int i = 0; i < NetPanel.nets.size(); i++)
                        {
                            Network ntmp = (Network)NetPanel.nets.get(i);
                            if(ntmp.getNetwork().equals(ip))
                                ok = false;
                        }

                        if(!ok)
                            new MessageDialog(dialog, "Duplicate IP Address", "IP Address");
                        else
                        if(ok)
                        {
                        	Network network = new Network(ip, new Point(100, 100), "public",1,icon);
                            
                        	addNetwork(network);
                            dialog.setVisible(false);
                            dialog.dispose();
                        }
                    }
                }
        });
        cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                dialog.setVisible(false);
                dialog.dispose();
            }

        });
        container.add(wizardPanel, "Center");
        container.add(buttonPanel, "South");
        dialog.getContentPane().add(container);
        dialog.setVisible(true);
    }
}
