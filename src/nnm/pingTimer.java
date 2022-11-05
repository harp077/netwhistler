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

import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.text.Format;
import java.text.SimpleDateFormat;
import nnm.inet.*;
import nnm.util.*;
import javax.swing.border.EtchedBorder;
import javax.mail.MessagingException;
import nnm.snmp.SNMPget;


public class pingTimer {
	Timer timer;

	int delay;

	public static Vector downNodes;

	public static int nosend = 0;

	public pingTimer(int seconds) {
		timer = new Timer();
		delay = seconds;
		timer.schedule(new PingerTask(), seconds * 1000);
	}

	class PingerTask extends TimerTask {

		public void run() {
			if (NetworkManagerGUI.MONITORING) {
				Graph.alerts = new Vector();
				NetworkManagerGUI.monNodes = 0;
				Date now = new Date();
				// System.out.print("Starttime:" + NetworkManagerGUI.startTime);
				downNodes = new Vector();
				Format formatter = new SimpleDateFormat("MM.dd.yyyy HH.mm.ss");
				NetworkManagerGUI.lastResponse = formatter.format(now);
				NetworkManagerGUI.status.setText(" Pinging...");
				for (int i = 0; i < Graph.nodes.size(); i++) {
					Node aNode = (Node) Graph.nodes.get(i);
					// InetAddress address = null;
					if (aNode.getMonitor()) {
						NetworkManagerGUI.monNodes++;
						String addr = aNode.getIP();
						// System.out.println("Pinging " + address);
						FPinger pinger = new FPinger();
						boolean ping = true;
						if (NetworkManagerGUI.monRetries == 3) {
							ping = pinger.Fping3(addr);
						} else {
							ping = pinger.Fping(addr);
						}
						if (ping) {
							// System.out.print(address + " is ok\n");
							if (aNode.getBadStatus()) {
								EventPanel.updateStatus(
										aNode.getIP() + "#"
										+ aNode.getDNSname() + "#"+formatter.format(now)+"#Ok"); // log
								NetworkManagerGUI.plogger.info(aNode.getIP()+" now is up");
								aNode.setStartTime();
								aNode.setDownTime(0);
								if (aNode.getSnmp()) {
									// snmp uptime
									String s = SNMPget.get(aNode,
											"1.3.6.1.2.1.1.3.0");
									if (NetworkManagerGUI.textHasContent(s)) {
										long up = Long.parseLong(s.trim());

										aNode.setUpTime(up);
										
									}
								} else {
									// nonSNMP uptime
									aNode.setUpTime(0);
									
								}

							}

							long diff = System.currentTimeMillis()
									- aNode.getStartTime();
							if (aNode.getSnmp()) {
								// snmp uptime
								String s = SNMPget.get(aNode,
										"1.3.6.1.2.1.1.3.0"); // SysUpTime
								if (NetworkManagerGUI.textHasContent(s)) {
									long up = Long.parseLong(s.trim());
									aNode.setUpTime(up);
									
								}
							} else {
								// nonSNMP uptime
								aNode.setUpTime(diff);
								
							}
							aNode.setBadStatus(false);
							aNode.setDownTime(0);
							aNode.setResponse(FPinger.pingTm);
							downNodes.removeElement(aNode);
							aNode.setMACaddress();
							

						} else {
							// System.out.print(address + " is not ok\n");

							if (!aNode.getBadStatus()) {
								aNode.setUpTime(0);
								aNode.setStartDownTime();

								downNodes.addElement(aNode);
								
								EventPanel.updateStatus(
										aNode.getIP() + "#"
										+ aNode.getDNSname()+"#" + formatter.format(now)+"#Down"); // log
								NetworkManagerGUI.plogger.info(aNode.getIP()+" is down");
							}

							long downdiff = System.currentTimeMillis()
									- aNode.getEndTime();
							aNode.setDownTime(downdiff);
							aNode.setBadStatus(true);
							aNode.setResponse("");
							

						}
						NetworkManagerGUI.manager.repaint();
						StatusPanel.updateStatus(aNode);
						IfacePanel.updateStatus(aNode);
					}

				}
				// netpanel router pinging
				for (int i = 0; i < NetworkManagerGUI.netPanel.nets.size(); i++) {
					Network aNet = (Network) NetworkManagerGUI.netPanel.nets.get(i);
					if (aNet.getType()==1){
					/////////////
						String addr = aNet.getNetwork();
						// System.out.println("Pinging " + address);
						FPinger pinger = new FPinger();
						boolean ping = true;
						if (NetworkManagerGUI.monRetries == 3) {
							ping = pinger.Fping3(addr);
						} else {
							ping = pinger.Fping(addr);
						}
						if (ping) {
							// System.out.print(address + " is ok\n");
							if (aNet.getBadStatus()) {
								EventPanel.updateStatus(
										aNet.getNetwork() + "#"
										+ aNet.getNetwork() + "#"+formatter.format(now)+"#Ok"); // log
								NetworkManagerGUI.plogger.info(aNet.getNetwork()+" now is up");
							}
						    aNet.setBadStatus(false);
							
						} else {
							// System.out.print(address + " is not ok\n");

							if (!aNet.getBadStatus()) {
								EventPanel.updateStatus(
										aNet.getNetwork() + "#"
										+ aNet.getNetwork()+"#" + formatter.format(now)+"#Down"); // log
								NetworkManagerGUI.plogger.info(aNet.getNetwork()+" is down");
							}
							aNet.setBadStatus(true);
						}
					//////////////
					}
				}
				//
				NetworkManagerGUI.status.setText(" ");
				// alerts
				if (NetworkManagerGUI.alerts && !downNodes.isEmpty()) {
					showAlerts();

				}
				if (NetworkManagerGUI.email && !downNodes.isEmpty()) {
					sendAlert();
				}
				if (NetworkManagerGUI.alertcmd && !downNodes.isEmpty()) {
					runAlertCmd();
				}
				// netpanel
				NetworkManagerGUI.netPanel.repaint();
				// tree
				NetworkManagerGUI.treePanel.refresh();
				// graphs refresh
				NetworkManagerGUI.graphsPanel.Refresh();
				
				if (!NetworkManagerGUI.MONITORING) {
					timer.cancel();
				} else {
					timer.schedule(new PingerTask(), delay * 1000); // new Timer
				}

			} 
		}
	}

	public static void showAlerts() {
		JDialog dialog = new JDialog();
		dialog.setTitle("NetWhistler Alert");
		dialog.setSize(350, 100);
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
		JPanel opt = new JPanel();
		opt.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		opt.setLayout(new BorderLayout());
		DefaultListModel model = new DefaultListModel();
		JList statusList = new JList(model);
		statusList.setCellRenderer(new AlertCellRenderer());
		for (int i = 0; i < downNodes.size(); i++) {
			Node aNode = (Node) downNodes.get(i);
			String s = aNode.getIP() + " (" + aNode.getDNSname() + ") is down";
			ListItem list = new ListItem(Color.white, Color.red, s, 1);
			model.addElement(list);
		}
		JScrollPane scroll = new JScrollPane(statusList);
		opt.add(scroll);
		dialog.getContentPane().add(opt, BorderLayout.CENTER);
		dialog.setVisible(true);
	}

	public void sendAlert() { // alertAddress , smtpAddress
		Date now = new Date();
		Format formatter = new SimpleDateFormat("MM.dd.yyyy HH.mm.ss");
		String alertTime = formatter.format(now);
		// System.out.print("Sending...");
		String alertText = "";
		if (NetworkManagerGUI.htmlAlert) {
			alertText = "<HTML><HEAD><TITLE>NetWhistler Alert</TITLE></HEAD><BODY><CENTER><H4><FONT COLOR=#6787BA>NetWhistler Alert</FONT><BR></H4><TABLE CELLPADDING=0 CELLSPACING=0 BORDER=1 BORDERCOLOR=#6787BA WIDTH=66%><TR><TD BGCOLOR=#6787BA VALIGN=center ALIGN=center><FONT COLOR=white><B>"
					+ alertTime + "</B></FONT></TD></TR>";
			for (int i = 0; i < downNodes.size(); i++) {
				Node aNode = (Node) downNodes.get(i);
				String s = "  <TR><TD VALIGN=center ALIGN=center><FONT COLOR=red><B>"
						+ aNode.getIP()
						+ " ("
						+ aNode.getDNSname()
						+ ") is down</B><BR></FONT></TD></TR>";
				alertText = alertText + s;
			}
			alertText = alertText
					+ "</TABLE><BR><FONT COLOR=#6787BA>Generated by NetWhistler</FONT><BR></CENTER></BODY></HTML>";
		} else {
			alertText = "NetWhistler Alert: " + alertTime + "\n";
			for (int i = 0; i < downNodes.size(); i++) {
				Node aNode = (Node) downNodes.get(i);
				String s = aNode.getIP() + " is down\n";
				// + " (" + aNode.getHostname() + ") is down\n";
				alertText = alertText + s;
			}

		}
		try {
			
			sendMail.send(NetworkManagerGUI.smtpAddress, 25,
					NetworkManagerGUI.alertAddress,
					NetworkManagerGUI.alertAddress, "NetWhistler Alert",
					alertText, NetworkManagerGUI.htmlAlert);
		} catch (MessagingException ex) {
			nosend = 1;
			NetworkManagerGUI.logger.info(" Can't send email alert");
		}

		if (nosend == 1) {
			JDialog tmp = new JDialog();
			new MessageDialog(tmp, "Can't send email alert", "Email alert");
		} else {
			NetworkManagerGUI.logger.info(" Sending email alert");
		}

	}

	public static void runAlertCmd() {
		Runtime r = Runtime.getRuntime();
		Process p = null;
		String ip = "";
		if (NetworkManagerGUI.textHasContent(NetworkManagerGUI.alertCommand)) {
			for (int i = 0; i < downNodes.size(); i++) {
				Node aNode = (Node) downNodes.get(i);
				ip = aNode.getIP();
				try {
					p = r.exec(NetworkManagerGUI.alertCommand.replaceAll("%IP",
							ip));
					if (p == null) {
						NetworkManagerGUI.logger.info(" Can't run " + NetworkManagerGUI.alertCommand + " " + ip);
						;
					}
				} catch (Exception ex) {
					NetworkManagerGUI.logger.info(" Error executing " + NetworkManagerGUI.alertCommand + " " + ip);
				}
			}
		}
	}

	public static String millisecondsToString(long time) {

		int seconds = (int) ((time / 1000) % 60);
		int minutes = (int) ((time / 60000) % 60);
		int hours = (int) ((time / 3600000) % 24);
		int days = (int) ((time / 3600000) / 24);
		hours += days * 24;
		String secondsStr = (seconds < 10 ? "0" : "") + seconds;
		String minutesStr = (minutes < 10 ? "0" : "") + minutes;
		String hoursStr = (hours < 10 ? "0" : "") + hours;
		return new String("(" + days + " days) " + hoursStr + ":" + minutesStr
				+ ":" + secondsStr);

	}

}
