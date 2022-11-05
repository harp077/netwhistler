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
// This file contains parts of a code of ipcalc.java @author Christopher Horn (wiesn@wiesn.at), 0057052/880

package nnm.inet;

import java.net.UnknownHostException;
import java.awt.Point;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JProgressBar;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import nnm.*;
import nnm.snmp.SNMPdiscover;
import nnm.util.ElapsedTime;


import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.Vector;

public class InetAddressUtil {

	private int[] ipTokens;

	private String[] ipTokensBinary;

	private String ipString;

	private String ipBinaryString;

	private String subnetBinaryString;

	private String[] subnetTokensBinary;

	private int subnetNumber;

	private int[] subnetTokensInt;

	private String networkBinaryString;

	private String[] networkTokensBinary;

	private int[] networkTokensInt;

	private String broadcastBinaryString;

	private String[] broadcastTokensBinary;

	private int[] broadcastTokensInt;

	private String hostMaxBinaryString;

	private String[] hostMaxTokensBinary;

	private int[] hostMaxTokensInt;

	private String hostMinBinaryString;

	private String[] hostMinTokensBinary;

	private int[] hostMinTokensInt;

	private int numberOfHosts;

	public InetAddressUtil(String inputString) {
		NetPanel.Discover=true;
		StringTokenizer inputStringTokens = new StringTokenizer(inputString);

		this.ipString = inputStringTokens.nextToken("/");
		this.subnetNumber = Integer.parseInt(inputStringTokens.nextToken("/"));

		this.ipTokens = new int[4];
		this.ipTokensBinary = new String[4];
		this.ipString = ipString;
		this.ipBinaryString = "";

		this.subnetBinaryString = new String();
		this.subnetTokensBinary = new String[4];
		this.subnetTokensInt = new int[4];

		this.networkBinaryString = "";
		this.networkTokensBinary = new String[4];
		this.networkTokensInt = new int[4];

		this.broadcastBinaryString = "";
		this.broadcastTokensBinary = new String[4];
		this.broadcastTokensInt = new int[4];

		// hostMin
		this.hostMinBinaryString = "";
		this.hostMinTokensBinary = new String[4];
		this.hostMinTokensInt = new int[4];

		// hostMax
		this.hostMaxBinaryString = "";
		this.hostMaxTokensBinary = new String[4];
		this.hostMaxTokensInt = new int[4];

		this.numberOfHosts = 0;

		this.evaluateString();
	}

	private void setIPTokens() {
		int counter = 0;
		StringTokenizer ipTok = new StringTokenizer(this.ipString);

		while (ipTok.hasMoreElements()) {
			ipTokens[counter] = Integer.parseInt(ipTok.nextToken("."));
			counter++;
		}
	}

	private void setIPBinaryTokens() {
		for (int i = 0; i < 4; i++) {
			ipTokensBinary[i] = Integer.toBinaryString(ipTokens[i]);
		}
	}

	private void setIPBinaryString() {
		for (int i = 0; i < 4; i++) {
			ipBinaryString += ipTokensBinary[i];
		}
	}

	private void setSubnetBinaryString() {
		for (int i = 0; i < 32; i++) {
			if (i < subnetNumber) {
				subnetBinaryString += "1";
			}

			else {
				subnetBinaryString += "0";
			}

		}
	}

	private void setSubnetBinaryTokens() {
		subnetTokensBinary[0] = this.subnetBinaryString.substring(0, 8);
		subnetTokensBinary[1] = this.subnetBinaryString.substring(8, 16);
		subnetTokensBinary[2] = this.subnetBinaryString.substring(16, 24);
		subnetTokensBinary[3] = this.subnetBinaryString.substring(24, 32);
	}

	private void setSubnetTokens() {
		for (int i = 0; i < 4; i++) {
			subnetTokensInt[i] = Integer.parseInt(subnetTokensBinary[i], 2);
		}
	}

	private void setNetworkBinaryString() {
		for (int i = 0; i < 32; i++) {
			if (i < subnetNumber) {
				networkBinaryString += ipBinaryString.charAt(i);
			}

			else {
				networkBinaryString += "0";
			}
		}
	}

	private void setNetworkTokensBinary() {
		networkTokensBinary[0] = this.networkBinaryString.substring(0, 8);
		networkTokensBinary[1] = this.networkBinaryString.substring(8, 16);
		networkTokensBinary[2] = this.networkBinaryString.substring(16, 24);
		networkTokensBinary[3] = this.networkBinaryString.substring(24, 32);
	}

	private void setNetworkTokens() {
		for (int i = 0; i < 4; i++) {
			networkTokensInt[i] = Integer.parseInt(networkTokensBinary[i], 2);
		}
	}

	private void setBroadcastBinaryString() {
		for (int i = 0; i < 32; i++) {
			if (i < subnetNumber) {
				broadcastBinaryString += ipBinaryString.charAt(i);
			}

			else {
				broadcastBinaryString += "1";
			}
		}
	}

	private void setBroadcastTokensBinary() {
		broadcastTokensBinary[0] = this.broadcastBinaryString.substring(0, 8);
		broadcastTokensBinary[1] = this.broadcastBinaryString.substring(8, 16);
		broadcastTokensBinary[2] = this.broadcastBinaryString.substring(16, 24);
		broadcastTokensBinary[3] = this.broadcastBinaryString.substring(24, 32);
	}

	private void setBroadcastTokens() {
		for (int i = 0; i < 4; i++) {
			broadcastTokensInt[i] = Integer.parseInt(broadcastTokensBinary[i],
					2);
		}
	}

	private void setHostMinBinaryString() {
		for (int i = 0; i < 31; i++) {
			if (i < subnetNumber) {
				hostMinBinaryString += ipBinaryString.charAt(i);
			}

			else {
				hostMinBinaryString += "0";
			}
		}
		hostMinBinaryString += "1";
	}

	private void setHostMinTokensBinary() {
		hostMinTokensBinary[0] = this.hostMinBinaryString.substring(0, 8);
		hostMinTokensBinary[1] = this.hostMinBinaryString.substring(8, 16);
		hostMinTokensBinary[2] = this.hostMinBinaryString.substring(16, 24);
		hostMinTokensBinary[3] = this.hostMinBinaryString.substring(24, 32);
	}

	private void setHostMinTokens() {
		for (int i = 0; i < 4; i++) {
			hostMinTokensInt[i] = Integer.parseInt(hostMinTokensBinary[i], 2);
		}
	}

	private void setHostMaxBinaryString() {
		for (int i = 0; i < 31; i++) {
			if (i < subnetNumber) {
				hostMaxBinaryString += ipBinaryString.charAt(i);
			}

			else {
				hostMaxBinaryString += "1";
			}
		}

		hostMaxBinaryString += "0";
	}

	private void setHostMaxTokensBinary() {
		hostMaxTokensBinary[0] = this.hostMaxBinaryString.substring(0, 8);
		hostMaxTokensBinary[1] = this.hostMaxBinaryString.substring(8, 16);
		hostMaxTokensBinary[2] = this.hostMaxBinaryString.substring(16, 24);
		hostMaxTokensBinary[3] = this.hostMaxBinaryString.substring(24, 32);
	}

	private void setHostMaxTokens() {
		for (int i = 0; i < 4; i++) {
			hostMaxTokensInt[i] = Integer.parseInt(hostMaxTokensBinary[i], 2);
		}
	}

	private void setHosts() {
		int hosts = 32 - this.subnetNumber;
		this.numberOfHosts = (int) Math.pow(2, hosts) - 2;
	}

	public String getHostMinString() {

		String hostMin = this.hostMinTokensInt[0] + "."
				+ this.hostMinTokensInt[1] + "." + this.hostMinTokensInt[2]
				+ "." + this.hostMinTokensInt[3];

		return hostMin;
	}

	public String getHostMaxString() {

		String hostMax = this.hostMaxTokensInt[0] + "."
				+ this.hostMaxTokensInt[1] + "." + this.hostMaxTokensInt[2]
				+ "." + this.hostMaxTokensInt[3];
		return hostMax;
	}

	public static String[] prependNulls(String[] array) {
		for (int i = 0; i < array.length; i++) {
			while (array[i].length() < 8) {
				array[i] = "0" + array[i];
			}
		}
		return array;
	}

	private void evaluateString() {

		this.setIPTokens();
		this.setIPBinaryTokens();
		this.ipTokensBinary = prependNulls(this.ipTokensBinary);
		this.setIPBinaryString();

		this.setSubnetBinaryString();
		this.setSubnetBinaryTokens();
		this.setSubnetTokens();

		this.setNetworkBinaryString();
		this.setNetworkTokensBinary();
		this.setNetworkTokens();

		this.setBroadcastBinaryString();
		this.setBroadcastTokensBinary();
		this.setBroadcastTokens();

		this.setHostMinBinaryString();
		this.setHostMinTokensBinary();
		this.setHostMinTokens();

		this.setHostMaxBinaryString();
		this.setHostMaxTokensBinary();
		this.setHostMaxTokens();
		this.setHosts();
	}

	public static void discovery(String net) {
		InetAddressUtil util = new InetAddressUtil(net);
		String hostMin = util.getHostMinString();
		String hostMax = util.getHostMaxString();

		// progress bar Dialog
		// progress bar Dialog
		final JFrame progress = new JFrame();

		java.net.URL imageURL = NetworkManagerGUI.class
				.getResource("icons/nw.gif");
		ImageIcon frameIcon = new ImageIcon(imageURL);
		Image image = frameIcon.getImage();
		progress.setIconImage(image);
		progress.setDefaultLookAndFeelDecorated(true);

		progress.setResizable(false);
		
		// Set to ignore the button
		progress.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		progress.setTitle("Discovery Progress");
		progress.setSize(310, 140);
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
		topPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		JLabel tm = new JLabel();
		tm.setFont(NetworkManagerGUI.baseFont);
		ElapsedTime t = new ElapsedTime(tm);
		t.start();

		// Create a label and progress bar
		JLabel statusLabel = new JLabel("Please wait...");
		statusLabel.setFont(NetworkManagerGUI.baseFont);
		// statusLabel.setPreferredSize(new Dimension(280, 24));
		topPanel.add(statusLabel);
		JPanel barPanel = new JPanel();
		barPanel.setPreferredSize(new Dimension(310, 120));
		JProgressBar bar = new JProgressBar();
		bar.setPreferredSize(new Dimension(250, 20));
		bar.setFont(new Font(null, Font.PLAIN, 14));
		bar.setMinimum(0);
		bar.setMaximum(util.numberOfHosts);
		bar.setValue(0);
		bar.setStringPainted(true);
		bar.setBorderPainted(true);
		bar.setForeground(new Color(58, 110, 165));
		bar.setBounds(20, 35, 200, 20);
		barPanel.add(tm);
		barPanel.add(bar);

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
		container.add(topPanel, BorderLayout.NORTH);
		container.add(barPanel, BorderLayout.CENTER);
		container.add(buttonPanel, BorderLayout.SOUTH);
		progress.getContentPane().add(container);
		progress.setVisible(true);

		// end progress

		Dimension frameSize = NetworkManagerGUI.manager.getSize();
		int d = frameSize.height;
		int z = frameSize.width;
		int x = 80;
		int y = 50;

		// main loop
		int found = 0;
		Vector range = new Vector();
		// System.out.println("NET: " + net + " HM: " + hostMin + " HMx: " +
		// hostMax);

		IPAddressRange r = null;
		try {
			r = new IPAddressRange(hostMin, hostMax);
		} catch (UnknownHostException ex) {
		}
		Enumeration e = r.elements();
		String addr = "";
		while (e.hasMoreElements()) {
			addr = e.nextElement().toString();
			range.add(addr);
		}

		for (int i = 0; i < range.size(); i++) {
			String ip = range.get(i).toString().replaceAll("\\/", "");
			FPinger pinger = new FPinger();
			if (pinger.Fping(ip)) {
				found = found + 1;
				Node sNode = new Node(ip, NetworkManager.currentNetwork,"", new Point(x, y), "workstation", 
						1, true, false, NetworkManagerGUI.wizCommunity,
						"private", new Vector(), new Vector(), 0, 0, 0, 0, pinger.pingTm,
						null, "");

				NetworkManager.aGraph.addNode(sNode);
				x = x + 140;
				if ((x + 48) > d) {
					x = 80;
					y = y + 80;
				}
			}
			bar.setValue(i);
			statusLabel.setText("Found " + found + " from "
					+ util.numberOfHosts);
			bar.paintImmediately(bar.getBounds());
			// else System.out.print("Host: "+netP+i+ " is bad!\n");
		}
		bar.setMaximum(100);
		bar.setValue(100);
		bar.setIgnoreRepaint(true);
		// dns
		statusLabel.setText("DNS queries...");
		bar.paintImmediately(bar.getBounds());
		for (int i = 0; i < Graph.nodes.size(); i++) {
			Node aNode = (Node) Graph.nodes.get(i);
			if (aNode.getNetwork().equals(NetworkManager.currentNetwork)){
			if (aNode.getnodeType().equals("hub")
					|| aNode.getnodeType().equals("network-cloud")) {
				;
			} else {
				
				aNode.getHostname();
			}
			}	
		}
		// SNMPdiscovery here
		statusLabel.setText("Probing SNMP ...");
		for (int i = 0; i < Graph.nodes.size(); i++) {
			Node aNode = (Node) Graph.nodes.get(i);
			if (aNode.getNetwork().equals(NetworkManager.currentNetwork)){
			if (aNode.getnodeType().equals("hub")
					|| aNode.getnodeType().equals("network-cloud")) {
				;
			} else {
				SNMPdiscover.discover(aNode);
				
			}
		  }
		}	
	   	MapTree.tree.updateUI();
	   	// links
	   	for (int i = 0; i < Graph.nodes.size(); i++) {
			Node aNode = (Node) Graph.nodes.get(i);
			if (aNode.getSnmp())
				FindLinks.find(aNode);
					
	   	}
		// dns
		for (int i = 0; i < Graph.nodes.size(); i++) {
			Node aNode = (Node) Graph.nodes.get(i);
			if (aNode.getNetwork().equals(NetworkManager.currentNetwork)){
			if (aNode.getnodeType().equals("hub")
					|| aNode.getnodeType().equals("network-cloud")) {
				;
			} else {
				aNode.setDnslabel(2);
				new setBindName(1, aNode, true);
				// aNode.setLabel(aNode.getHostname());
			}
		}}
		t.stop();
		if (Graph.nodes.size() != 0) {
			statusLabel.setText("Map " + NetworkManager.currentNetwork + " created");
			ok.setVisible(true);
			NetworkManagerGUI.graphsPanel.Refresh();
		} else {
			statusLabel.setText("Map is empty ");
			ok.setVisible(true);
		}
		NetPanel.Discover=false;
	}
}
