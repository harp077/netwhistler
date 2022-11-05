// Copyright (C) 2005 Mila NetWhistler.  All rights reserved.
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation; either version 2 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//
//For more information contact:
//   Mila NetWhistler        <netwhistler@gmail.com>
//   http://www.netwhistler.spb.ru/
//
//
package nnm.util.report;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.jrobin.mrtg.client.Poller;

//import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;
import org.apache.xalan.xsltc.runtime.Hashtable;

import nnm.Graph;
import nnm.NetworkManager;
import nnm.NetworkManagerGUI;
import nnm.Node;
import nnm.pingTimer;
import nnm.inet.IFace;
import nnm.inet.Service;
import nnm.snmp.OidToCheck;
import nnm.snmp.SNMPget;
import nnm.snmp.getLinks;
import nnm.snmp.getPorts;
import nnm.util.ColorIcon;
import nnm.util.PortsHash;


public class showStatus {
	private Node sNode = null;

	boolean snmp = false;

	private Vector allifaces;

	private IFace curenteth = null;
	private SortedMap links;
	private DefaultTableModel snmpintmodel;
	public static String[] types = new String[33];

	public showStatus(Node aNode) {
		sNode = aNode;
		run();
         
	}

	public void run() {
		types[0] = "default";
		types[1] = "other";
		types[2] = "regular1822";
		types[3] = "hdh1822";
		types[4] = "ddn_x25";
		types[5] = "rfc877_x25";
		types[6] = "ethernet_csmacd";
		types[7] = "iso88023_csmacd";
		types[8] = "iso88024_tokenBus";
		types[9] = "iso88025_tokenRing";
		types[10] = "iso88026_man";
		types[11] = "starLan";
		types[12] = "proteon_10Mbit";
		types[13] = "proteon_80Mbit";
		types[14] = "hyperchannel";
		types[15] = "fddi";
		types[16] = "lapb";
		types[17] = "sdlc";
		types[18] = "ds1";
		types[19] = "e1";
		types[20] = "basicISDN";
		types[21] = "primaryISDN";
		types[22] = "propPointToPointSerial";
		types[23] = "ppp";
		types[24] = "softwareLoopback";
		types[25] = "eon";
		types[26] = "etherne_3Mbit";
		types[27] = "nsip";
		types[28] = "slip";
		types[29] = "ultra";
		types[30] = "ds3";
		types[31] = "sip";
		types[32] = "frame_relay";
		
		final JDialog dlg = new JDialog();
		dlg.setTitle("Status for " + sNode.getIP());
		dlg.setSize(500, 310);
		dlg.setResizable(false);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension optSize = dlg.getSize();
		if (optSize.height > screenSize.height) {
			optSize.height = screenSize.height;
		}
		if (optSize.width > screenSize.width) {
			optSize.width = screenSize.width;
		}
		dlg.setLocation((screenSize.width - optSize.width) / 2,
				(screenSize.height - optSize.height) / 2);
		dlg.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
		JPanel sumP = new JPanel();
		// sumP.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		sumP.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(1, 1, 1, 1);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		DefaultTableModel model = new DefaultTableModel() {
			public boolean isCellEditable(int rowIndex, int mColIndex) {
				return false;
			}
		};
		JTable table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		// Create a couple of columns

		model.addColumn("");
		model.addColumn("");
		// Create the rows

		model.addRow(new Object[] { "IP address:", sNode.getIP() });
		String hname = sNode.getDNSname();
		if (!sNode.getIP().equals(hname)) {
			model.addRow(new Object[] { "Hostname:", hname });
		}
		if (NetworkManagerGUI.textHasContent(sNode.getMACaddress())) {
			model
					.addRow(new Object[] { "MAC address:",
							sNode.getMACaddress() });
		}
		if (NetworkManagerGUI.textHasContent(sNode.getNIC())) {
			model
					.addRow(new Object[] { "NIC vendor:",
							sNode.getNIC() });
		}
		model.addRow(new Object[] { "Type:", sNode.getnodeType() });
		if (sNode.getMonitor()) {
			model.addRow(new Object[] { "Monitoring:", "enabled" });
		} else {
			model.addRow(new Object[] { "Monitoring:", "disabled" });
		}

		if (!sNode.getBadStatus()
				&& NetworkManagerGUI.textHasContent(sNode.getResponse())) {
			model.addRow(new Object[] { "Response:", sNode.getResponse() });
		}
		if (!sNode.getBadStatus()) {
			String up = "";
			if (sNode.getSnmp())
				up = NetworkManager.secondsToString(sNode.getUpTime());
			else
				up = pingTimer.millisecondsToString(sNode.getUpTime());
			model.addRow(new Object[] { "UpTime:", up });
		} else {
			model.addRow(new Object[] { "DownTime:",
					pingTimer.millisecondsToString(sNode.getDownTime()) });
		}

		//
		table.setFont(NetworkManagerGUI.smallFont);
		JScrollPane scroll = new JScrollPane(table);
		scroll.setPreferredSize(new Dimension(440, 170));
		scroll.setMinimumSize(new Dimension(440, 170));
		String img = "";

		JLabel stLab = new JLabel("");
		//stLab.setFont(NetworkManagerGUI.baseFont);

		stLab.setVerticalTextPosition(SwingConstants.CENTER);
		stLab.setHorizontalTextPosition(SwingConstants.RIGHT);

		c.gridx = 0;
		c.gridy = 4;
		sumP.add(scroll, c);
		// services
		JPanel servP = new JPanel();
		servP.setLayout(new GridBagLayout());
		boolean warn = false;
		if (!sNode.getBadStatus()) {
			if (sNode.getCheckPorts().size() != 0) {
				DefaultTableModel portsmodel = new DefaultTableModel() {
					public boolean isCellEditable(int rowIndex, int mColIndex) {
						return false;
					}
				};
				JTable portstable = new JTable(portsmodel);
				portstable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

				// Create a couple of columns
				portsmodel.addColumn("Service");
				portsmodel.addColumn("Status");
				// Create the rows
				for (int i = 0; i < sNode.getCheckPorts().size(); i++) {
					Service t = (Service) sNode.getCheckPorts().get(i);
					String name = t.getServiceName();
					String status = t.getStatus() ? "up" : "down";
					if (status.equals("down")) {
						portsmodel
								.insertRow(i, new Object[] {
										name,
										"<html><font color=red>" + status
												+ "</font>" });
						warn = true;
					} else {
						portsmodel.insertRow(i, new Object[] { name, status });
					}
				}

				//
				portstable.setFont(NetworkManagerGUI.smallFont);
				JScrollPane portsscroll = new JScrollPane(portstable);
				portsscroll.setPreferredSize(new Dimension(440, 170));
				portsscroll.setMinimumSize(new Dimension(440, 170));
				servP.add(portsscroll);
			} else {
				JLabel portslab = new JLabel("No Monitoring Services for "
						+ sNode.getIP());
				portslab.setFont(NetworkManagerGUI.baseFont);
				c.gridx = 0;
				c.gridy = 2;
				servP.add(portslab, c);
			}
		} else {
			JLabel tmplab = new JLabel("Node is down");
			tmplab.setFont(NetworkManagerGUI.baseFont);
			c.gridx = 0;
			c.gridy = 2;
			servP.add(tmplab, c);
		}
		// icon
		if (!sNode.getBadStatus() && !warn) {
			stLab.setText("<html><table><tr><td>Status:</td><td>OK</td></tr></table>");
			
			img = "icons/monstart.gif";
		} else if (!sNode.getBadStatus() && warn) {
			stLab.setText("<html><table><tr><td><b><font color=#FF9933>Status:</font></b></td><td><b><font color=#FF9933>WARNING</font></b></td></tr></table>");
			img = "icons/monstart.gif";
		} else {
			stLab.setText("<html><tr><td><b><font color=red>Status:</font></b></td><td><b><font color=red>DOWN</font></b></td></tr></table>");
			img = "icons/monstop.gif";
		}
		java.net.URL imageURL = NetworkManagerGUI.class.getResource(img);
		ImageIcon sticon = null;
		sticon = new ImageIcon(imageURL);
		if (warn)
			sticon = new ImageIcon(ColorIcon.colorize(sticon.getImage(),
					Color.orange));
		stLab.setIcon(sticon);
		c.gridx = 0;
		c.gridy = 2;
		sumP.add(stLab, c);
		// snmp
		JPanel snmpP = new JPanel();
		// snmpP.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		snmpP.setLayout(new GridBagLayout());
		JPanel snmpintP = new JPanel();
		// snmpP.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		snmpintP.setLayout(new GridBagLayout());
		JPanel snmplinksP = new JPanel();
		// snmpP.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		snmplinksP.setLayout(new GridBagLayout());
		JPanel snmpoidsP = new JPanel();
		// snmpP.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		snmpoidsP.setLayout(new GridBagLayout());
		int ifaces=0;
		
		if (!sNode.getBadStatus()) {
			if (sNode.getSnmp()) {
				String snmpType = SNMPget.get(sNode, "1.3.6.1.2.1.1.2.0"); // type
			    if (NetworkManagerGUI.textHasContent(snmpType) && !snmpType.equals("null"))
			    	snmp = snmpType.startsWith("1.3.6"); 
			    if (snmp){
				String sysName = SNMPget.get(sNode, "1.3.6.1.2.1.1.5.0"); // SysName
				JLabel snmplab = new JLabel(sysName);
				//snmplab.setFont(NetworkManagerGUI.baseFont);
				c.gridx = 0;
				c.gridy = 2;
				snmpP.add(snmplab, c);
				DefaultTableModel snmpmodel = new DefaultTableModel() {
					public boolean isCellEditable(int rowIndex, int mColIndex) {
						return false;
					}
				};
				JTable snmptable = new JTable(snmpmodel);
				snmptable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
				// Create a couple of columns
				snmpmodel.addColumn("");
				snmpmodel.addColumn("");
				// Create the rows
				String descr = SNMPget.get(sNode, "1.3.6.1.2.1.1.1.0"); // Description
				snmpmodel.addRow(new Object[] { "SysDescr:", descr });
				//String type = SNMPget.get(sNode, "1.3.6.1.2.1.1.2.0"); // type
				snmpmodel.addRow(new Object[] { "SysObjectID:", snmpType });
				//long uptime = Long.parseLong(SNMPget.get(sNode,
						//"1.3.6.1.2.1.1.3.0")); // UpTime
				snmpmodel.addRow(new Object[] { "SysUpTime:",NetworkManager.secondsToString(sNode.getUpTime()) });
						//NetworkManager.secondsToString(uptime) }); // sysuptume
				snmpmodel.addRow(new Object[] { "SysContact:",
						SNMPget.get(sNode, "1.3.6.1.2.1.1.4.0") }); // SysContact
				snmpmodel.addRow(new Object[] { "SysName:", sysName }); // SysName
				snmpmodel.addRow(new Object[] { "SysLocation:",
						SNMPget.get(sNode, "1.3.6.1.2.1.1.6.0") }); // SysLocation
				snmpmodel.addRow(new Object[] { "SysServices: ",
						SNMPget.get(sNode, "1.3.6.1.2.1.1.7.0") }); // SysLocation

				snmptable.setFont(NetworkManagerGUI.smallFont);
				JScrollPane snmpscroll = new JScrollPane(snmptable);
				snmpscroll.setPreferredSize(new Dimension(440, 170));
				snmpscroll.setMinimumSize(new Dimension(440, 170));
				c.gridx = 0;
				c.gridy = 4;
				snmpP.add(snmpscroll, c);
				
				
				// ifaces
				final DefaultComboBoxModel ifModel = new DefaultComboBoxModel();
				final JComboBox ifBox = new JComboBox(ifModel);
				ifBox.setBackground(NetworkManagerGUI.sysBackColor);
				ifBox.setFont(NetworkManagerGUI.baseFont);
				snmpintmodel = new DefaultTableModel() {
					public boolean isCellEditable(int rowIndex, int mColIndex) {
						return false;
					}
				};
				JTable snmpinttable = new JTable(snmpintmodel);
				snmpinttable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
				// Create a couple of columns
				snmpintmodel.addColumn("");
				snmpintmodel.addColumn("");
				// Create the rows
				ifaces = Integer.parseInt(SNMPget.get(sNode,"1.3.6.1.2.1.2.1.0"));
										
				if (ifaces != 0) {
					allifaces = new Vector();
	               Poller comm = null;
                     try {
                          comm = new Poller(sNode.getIP(), sNode.getRcommunity());
                         
                          links = comm.walkIfDescr();
	                       Iterator     it = links.values().iterator();
	                       while (it.hasNext()) {
	                    	   //		 Get value
	                    	   Object Value = it.next();
	                    	   //System.out.println(Value);
	                    	   int p = getIfIndexByIfDescr(Value.toString());
	                    	   //System.out.println(Value + ": " + p);
	                    	   IFace eth = new IFace(Value.toString(), p);
	                    	   allifaces.add(eth);
	                       }
                     } catch (IOException ex) {
                    	 //System.out.println("Ex: " + ex);
                     }
		             if (comm != null) {
		                  comm.close();
		             }

		             for (int i = 0; i < allifaces.size(); i++) {

		                 IFace eth = (IFace) allifaces.get(i);
		                   ifModel.addElement(eth.getDescr());

		                }
					// ifBox.setSelectedItem();
					ifBox.addItemListener(new ItemListener() {
						public void itemStateChanged(ItemEvent e) {
							String s = (String) e.getItem();
							for (int f = 0; f < allifaces.size(); f++) {
								IFace tmpif = (IFace) allifaces.get(f);
								if (tmpif.getDescr().equals(s))
									curenteth = tmpif;
							}
							//System.out.println("Eth: " + curenteth);
							//System.out.println("GET:"+SNMPget.get(sNode,"1.3.6.1.2.1.2.2.1.2." +curenteth.getNumber() ));
							if (curenteth.getNumber() != 0) {
								snmpintmodel.setRowCount(0);
								//1.3.6.1.2.1.2.2.1.2.102
								//System.out.println("GET:"+SNMPget.get(sNode,"1.3.6.1.2.1.2.2.1.2.102"));
								snmpintmodel
										.addRow(new Object[] {"ifDescr: ", curenteth.getDescr()});
								int type = Integer.parseInt(SNMPget.get(sNode,"1.3.6.1.2.1.2.2.1.3."+ curenteth.getNumber()));  
								String intftype=null;
								if (type<=32)
									intftype=types[type];
								else
									intftype=String.valueOf(type);
								snmpintmodel
										.addRow(new Object[] {
												"ifType: ", intftype });
								snmpintmodel
										.addRow(new Object[] {
												"ifSpeed: ",
												SNMPget
														.get(
																sNode,
																"1.3.6.1.2.1.2.2.1.5."
																		+ curenteth
																				.getNumber()) });
								snmpintmodel
										.addRow(new Object[] {
												"ifPhysAddress: ",
												SNMPget
														.get(
																sNode,
																"1.3.6.1.2.1.2.2.1.6."
																		+ curenteth
																				.getNumber()) });
								snmpintmodel
										.addRow(new Object[] {
												"ifOperStatus: ",
												SNMPget
														.get(
																sNode,
																"1.3.6.1.2.1.2.2.1.8."
																		+ curenteth
																				.getNumber()) });
								snmpintmodel
										.addRow(new Object[] {
												"ifOperStatus: ",
												SNMPget
														.get(
																sNode,
																"1.3.6.1.2.1.2.2.1.8."
																		+ curenteth
																				.getNumber()) });
								snmpintmodel
										.addRow(new Object[] {
												"ifInOctets: ",
												SNMPget
														.get(
																sNode,
																"1.3.6.1.2.1.2.2.1.10."
																		+ curenteth
																				.getNumber()) });
								snmpintmodel
										.addRow(new Object[] {
												"ifOutOctets: ",
												SNMPget
														.get(
																sNode,
																"1.3.6.1.2.1.2.2.1.18."
																		+ curenteth
																				.getNumber()) });
								snmpintmodel
										.addRow(new Object[] {
												"ifInErrors: ",
												SNMPget
														.get(
																sNode,
																"1.3.6.1.2.1.2.2.1.14."
																		+ curenteth
																				.getNumber()) });
								snmpintmodel
										.addRow(new Object[] {
												"ifOutErrors: ",
												SNMPget
														.get(
																sNode,
																"1.3.6.1.2.1.2.2.1.20."
																		+ curenteth
																				.getNumber()) });
							}
						}
					});
					IFace firstif = (IFace) allifaces.firstElement();
					snmpintmodel.addRow(new Object[] { "ifDescr: ",
							firstif.getDescr() });
					snmpintmodel.addRow(new Object[] { "ifType: ",
							SNMPget.get(sNode, "1.3.6.1.2.1.2.2.1.3." + firstif.getNumber()) });
					snmpintmodel.addRow(new Object[] { "ifSpeed: ",
							SNMPget.get(sNode, "1.3.6.1.2.1.2.2.1.5." + firstif.getNumber()) });
					snmpintmodel.addRow(new Object[] { "ifPhysAddress: ",
							SNMPget.get(sNode, "1.3.6.1.2.1.2.2.1.6." + firstif.getNumber()) });
					snmpintmodel.addRow(new Object[] { "ifOperStatus: ",
							SNMPget.get(sNode, "1.3.6.1.2.1.2.2.1.8." + firstif.getNumber()) });
					snmpintmodel.addRow(new Object[] { "ifOperStatus: ",
							SNMPget.get(sNode, "1.3.6.1.2.1.2.2.1.8." + firstif.getNumber()) });
					snmpintmodel.addRow(new Object[] { "ifInOctets: ",
							SNMPget.get(sNode, "1.3.6.1.2.1.2.2.1.10." + firstif.getNumber()) });
					snmpintmodel.addRow(new Object[] { "ifOutOctets: ",
							SNMPget.get(sNode, "1.3.6.1.2.1.2.2.1.18." + firstif.getNumber()) });
					snmpintmodel.addRow(new Object[] { "ifInErrors: ",
							SNMPget.get(sNode, "1.3.6.1.2.1.2.2.1.14." + firstif.getNumber()) });
					snmpintmodel.addRow(new Object[] { "ifOutErrors: ",
							SNMPget.get(sNode, "1.3.6.1.2.1.2.2.1.20." + firstif.getNumber()) });
					snmpinttable.setFont(NetworkManagerGUI.smallFont);
					JScrollPane snmpintscroll = new JScrollPane(snmpinttable);
					snmpintscroll.setPreferredSize(new Dimension(440, 170));
					snmpintscroll.setMinimumSize(new Dimension(440, 170));
					c.gridx = 0;
					c.gridy = 2;
					ifBox.setBackground(NetworkManagerGUI.sysBackColor);
					snmpintP.add(ifBox,c);
					c.gridx = 0;
					c.gridy = 4;
					snmpintP.add(snmpintscroll,c);
				} else {
					JLabel snmpintlab = new JLabel(
							"No Recognized Interfaces for " + sNode.getIP());
					snmpintlab.setFont(NetworkManagerGUI.baseFont);
					c.gridx = 0;
					c.gridy = 2;
					snmpintP.add(snmpintlab, c);
				}
				// links

				final DefaultComboBoxModel linksModel = new DefaultComboBoxModel();
				DefaultTableModel snmplinksmodel = new DefaultTableModel() {
					public boolean isCellEditable(int rowIndex, int mColIndex) {
						return false;
					}
				};
				JTable snmplinkstable = new JTable(snmplinksmodel);
				snmplinkstable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
				// Create a couple of columns
				snmplinksmodel.addColumn("Port");
				snmplinksmodel.addColumn("MAC Address");
				snmplinksmodel.addColumn("IP Address");
				int vColIndex = 0;
				TableColumn col = snmplinkstable.getColumnModel().getColumn(vColIndex);
				int width = 25;
				col.setPreferredWidth(width);
				// Create the rows
				Vector ports = getPorts.getPortsNumbers(sNode);
				Vector macs = getLinks.getConnectedNodes(sNode);
				if (macs.size()!=0 && ports.size()!=0) {
					Hashtable H = new Hashtable();
					for (int l = 0; l < macs.size(); l++) {
						 
						 String mac = (String) macs.get(l); 
						 //System.out.println("Port: " +  ports.get(l) + " mac: " + mac);
						 int p = Integer.parseInt(ports.get(l).toString());
						 H.put(mac, new Integer(p));
						 
					 }  
					
				 
				      PortsHash[] MH=new PortsHash[H.size()];
				      int j=0;
				      Enumeration k=H.keys();
				      for (Enumeration v=H.elements();v.hasMoreElements();) {
				         MH[j]=new PortsHash(((Integer)v.nextElement()).intValue(),(String)k.nextElement());
				         j++;
				      }
				      Arrays.sort(MH);
				      String dns = null;
				      for (j=0; j<MH.length; j++) { 
				    	   dns = null;
				    	  for (int n = 0; n < Graph.nodes.size(); n++) {
								Node gNode = (Node) Graph.nodes.get(n);
						         		if (NetworkManagerGUI.textHasContent(gNode.getMACaddress())){
	              			if (gNode.getMACaddress().equals(MH[j].getKey())){
	              				//if (!gNode.getIP().equals(gNode.getDNSname())) 	
	              					//dns = gNode.getDNSname()+" ("+gNode.getIP()+")";
	              				//else
	              					dns = gNode.getIP();
	              				
	              			}
	              			
	              		}
				    	  	
	              		} 
				        // System.out.println("Port: " + MH[j].getValue() + " MAC: "+ MH[j].getKey());
				         snmplinksmodel.addRow(new Object[] { ""+MH[j].getValue() , MH[j].getKey(), dns });
				      }
				
						
					snmplinkstable.setFont(NetworkManagerGUI.smallFont);
					JScrollPane snmplinksscroll = new JScrollPane(snmplinkstable);
					snmplinksscroll.setPreferredSize(new Dimension(440, 170));
					snmplinksscroll.setMinimumSize(new Dimension(440, 170));
					c.gridx = 0;
					c.gridy = 2;
					snmplinksP.add(snmplinksscroll,c);
				} else {
					JLabel snmplinkslab = new JLabel(
							"No Recognized Connections for " + sNode.getIP());
					snmplinkslab.setFont(NetworkManagerGUI.baseFont);
					c.gridx = 0;
					c.gridy = 2;
					snmplinksP.add(snmplinkslab, c);
				}

				// oids
				if (sNode.getSNMPOids().size() != 0) {
					DefaultTableModel oidsmodel = new DefaultTableModel() {
						public boolean isCellEditable(int rowIndex,
								int mColIndex) {
							return false;
						}
					};
					JTable oidstable = new JTable(oidsmodel);
					oidstable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

					// Create a couple of columns
					oidsmodel.addColumn("SNMP OID");
					oidsmodel.addColumn("OID Value");

					// Create the rows
					for (int i = 0; i < sNode.getSNMPOids().size(); i++) {
						OidToCheck oidt = (OidToCheck) sNode.getSNMPOids().get(
								i);
						String name = oidt.getOidName();
						String value = oidt.getOidValue();
						if (!NetworkManagerGUI.textHasContent(value))
							value = "No result";
						oidsmodel.insertRow(i, new Object[] { name, value });
					}

					oidstable.setFont(NetworkManagerGUI.smallFont);
					JScrollPane oidsscroll = new JScrollPane(oidstable);
					oidsscroll.setPreferredSize(new Dimension(440, 170));
					oidsscroll.setMinimumSize(new Dimension(440, 170));
					snmpoidsP.add(oidsscroll);
				} else {
					JLabel snmpoidslab = new JLabel("No Monitoring Oids for "
							+ sNode.getIP());
					snmpoidslab.setFont(NetworkManagerGUI.baseFont);
					c.gridx = 0;
					c.gridy = 2;
					snmpoidsP.add(snmpoidslab, c);

				}
			    }
			} else {
				sNode.setSnmp(false);// no reply snmp
			}		
		} else {
			JLabel stmplab = new JLabel("Node is down");
			stmplab.setFont(NetworkManagerGUI.baseFont);
			JLabel sinttmplab = new JLabel("Node is down");
			sinttmplab.setFont(NetworkManagerGUI.baseFont);
			JLabel soidstmplab = new JLabel("Node is down");
			soidstmplab.setFont(NetworkManagerGUI.baseFont);
			c.gridx = 0;
			c.gridy = 2;
			snmpP.add(stmplab, c);
			snmpintP.add(sinttmplab, c);
			snmpoidsP.add(soidstmplab, c);
		}

		//

		//
		JTabbedPane tabs = new JTabbedPane();
		tabs.setFont(NetworkManagerGUI.baseFont);
		tabs.addTab("Summary", null, sumP);
		tabs.addTab("Services", null, servP);
		if (sNode.getSnmp()&& snmp) {
			tabs.addTab("SNMP", null, snmpP);
			tabs.addTab("Network Interfaces (" + ifaces +")", null, snmpintP);
			tabs.addTab("Port Mapper", null, snmplinksP);
			tabs.addTab("SNMP Monitoring OIDs", null, snmpoidsP);
		}
		JPanel btnPanel = new JPanel();

		btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		String Monitor = NetworkManagerGUI.MONITORING ? "enabled" : "disabled";
		JLabel monLab = new JLabel("Map monitoring: " + Monitor);
		monLab.setFont(NetworkManagerGUI.baseFont);
		btnPanel.add(monLab, "west");
		btnPanel.add(Box.createHorizontalStrut(20));
		JButton cancel = new JButton("Close");
		cancel.setBackground(NetworkManagerGUI.sysBackColor);
		btnPanel.add(cancel, "east");
		cancel.setFont(NetworkManagerGUI.baseFont);
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dlg.setVisible(false);
				dlg.dispose();
			}
		});

		container.add(tabs, BorderLayout.CENTER);
		container.add(btnPanel, BorderLayout.SOUTH);
		dlg.getContentPane().add(container);

		// dialog.getContentPane().add(opt, BorderLayout.CENTER);
		// dialog.pack();
		dlg.setVisible(true);
	}
		   int getIfIndexByIfDescr(String ifDescr) {
               SortedMap map = links;
               Iterator it = map.keySet().iterator();
               while (it.hasNext()) {
                       Integer ix = (Integer) it.next();
                       String value = (String) map.get(ix);
                       if (value.equalsIgnoreCase(ifDescr)) {
                               return ix.intValue();
                       }
               }
               return -1;
       }

}