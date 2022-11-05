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
package nnm;

import java.awt.Font;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Toolkit;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import nnm.inet.getMac;
import nnm.inet.Service;
import nnm.snmp.OidToCheck;
import nnm.util.ColorIcon;


public class Node  {
	public static int RADIUS = 20;
    public boolean showIcon;
	public static int nodeSize = 38;

	public static int cloudradius = 60;

	private String label;

	private String namelabel;
	private String NIC = "";
	private String IPlabel;
	private String hostname="";

	public String MACaddr = "";

	private String nodeType;
	private Color badColor = Color.red;
	public boolean monitor = true;

	private boolean snmp = false;

	private int dnslabel = 1;

	private boolean badStatus;

	private long upTime;

	private long downTime;

	private Service service;
    private OidToCheck oid;
	private Point location;
	private String network;
	private Vector incidentEdges;

	private boolean selected;
	private boolean enabled;
	private Vector checkPorts;
 private Vector oids;
	private String[] info;

	private ImageIcon icon;

	private long startTime;

	private long endTime;

	private String tipImage = "";

	private String response = "";

	private String howHops = "";

	private String rcommunity = "public";

	private String wcommunity = "private";

	public static boolean showHops = false;

	public Node() {
		this("", "","", new Point(0, 0), "", 1, true, false, "", "", new Vector(), new Vector(),
				0, 0, 0, 0, "", null, "");
	}

	public Node(String aLabel, String aNetwork, String aHostname, String[] aInfo,
			String anodeType, String aTipImage) {
		this(aLabel, aNetwork, aHostname, new Point(0, 0), "", 1, true, false, "", "",
				new Vector(),new Vector(), 0, 0, 0, 0, "", aInfo, "");
	}

	public Node(Point aPoint) {
		this("", "","", aPoint, "", 1, true, false, "", "", new Vector(), new Vector(),0, 0, 0,
				0, "", null, "");
	}

	public Node(String aLabel, String aNetwork, String aHostname, Point aPoint,
			String anodeType, int aDns, boolean aMonitor, boolean aSnmp,
			String aRcommunity, String aWcommunity, Vector aCheckPorts, Vector aOids,
			long astartTime, long aendTime, long aUpTime, long aDownTime,
			String aResponse, String[] aInfo, String aTipImage) {
		label = aLabel;
		network = aNetwork;
		hostname = aHostname;
		info = aInfo;
		startTime = astartTime;
		endTime = aendTime;
		nodeType = anodeType;
		monitor = aMonitor;
		dnslabel = aDns;
		snmp = aSnmp;
		checkPorts = aCheckPorts;
		oids = aOids;
		upTime = aUpTime;
		downTime = aDownTime;
		response = aResponse;
		tipImage = aTipImage;
		location = aPoint;
		incidentEdges = new Vector();
		rcommunity = aRcommunity;
		wcommunity = aWcommunity;
		
	}

	public String getLabel() {
		return label;
	}
	public String getNetwork(){
		return network;
	} 
	public String getDNSname() {
		if (!NetworkManagerGUI.textHasContent(hostname))
			getHostname();
		return hostname;
	}

	public String[] getInfo() {
		return info;
	}

	public Point getLocation() {
		return location;
	}

	public String getnodeType() {
		return nodeType;
	}

	public String getTipImage() {
		return tipImage;
	}

	public boolean getMonitor() {
		return monitor;
	}

	public Vector getCheckPorts() {
		return checkPorts;
	}
	public Vector getSNMPOids() {
		return oids;
	}
	public int getDnslabel() {
		return dnslabel;
	}

	public boolean getSnmp() {
		return snmp;
	}
	public void setBadColorDefault() {
		badColor=Color.red;
	}
	public String getResponse() {
		return response;
	}

	public String getRcommunity() {
		return rcommunity;
	}

	public String getWcommunity() {
		return wcommunity;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean state) {
		if (NetworkManagerGUI.MONITORING){
		if (state == false)
		{	
			enabled = false;
			badColor = Color.RED;
		}else { 
			badColor=new Color(255, 128, 128);
			enabled = true;
		}
		} else {
			badColor = Color.RED;
		}
	}
	

	public void setLabel(String newLabel) {
		label = newLabel;
	}

	public void setResponse(String newResp) {
		response = newResp;
	}

	public void setInfo(String[] newInfo) {
		info = newInfo;
	}
	public String getNIC() {
		return NIC;
	}
	public void setLocation(Point aPoint) {
		location = aPoint;
	}
	public void setNIC(String addr) {
		NIC = addr;
	}
	public void setTipImage(String newTipImage) {
		tipImage = newTipImage;
	}

	public void setNetwork(String net) {
		network = net;
	}
	public void setLocation(int x, int y) {
		location = new Point(x, y);
	}

	public void setCheckPorts(Vector ports) {
		checkPorts = ports;
	}

	public void setSNMPOids(Vector newOids) {
		oids = newOids;
	}
	public void setService(Service s) {
		checkPorts.add(s);

	}

	public void setnodeType(String newnodeType) {
		nodeType = newnodeType;
	}

	public void setMonitor(boolean newMonitor) {
		monitor = newMonitor;
	}

	public void setSnmp(boolean newSnmp) {
		snmp = newSnmp;
	}

	public void setDNSname(String newHostname) {
		hostname = newHostname;
	}

	public void setDnslabel(int n) {
		dnslabel = n;
	}

	public void setMACaddress() {
		if (nodeType.equals("hub") || nodeType.equals("network-cloud")) {
			MACaddr = "";
		} else {
			MACaddr = getMac.getMACaddr(getIP());
			

		}
	}

	public void setHops(String hops) {
		showHops = true;
		howHops = hops;
	}

	public void setRcommunity(String rcomm) {

		rcommunity = rcomm;
	}

	public void setWcommunity(String wcomm) {

		wcommunity = wcomm;
	}

	public Vector incidentEdges() {
		return incidentEdges;
	}

	public void addIncidentEdge(Edge e) {
		incidentEdges.add(e);
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean state) {
		selected = state;
	}

	public void setBadStatus(boolean state) {
		badStatus = state;
		if (!NetworkManagerGUI.MONITORING)
		NetworkManagerGUI.treePanel.treeModel.reload();
	}

	public boolean getBadStatus() {
		return badStatus;
	}

	public String getIP() {
		try {
			InetAddress addr = InetAddress.getByName(label);
			byte[] ipAddr = addr.getAddress();
			// Convert to dot representation
			IPlabel = "";
			for (int i = 0; i < ipAddr.length; i++) {
				if (i > 0) {
					IPlabel += ".";
				}
				IPlabel += ipAddr[i] & 0xFF;
			}
		} catch (UnknownHostException e) {
		}
		return IPlabel;
	}

	public String getHostname() {
		if (nodeType.equals("hub") || nodeType.equals("network-cloud")) {
			namelabel = "";
		} else
			try {
				// Get hostname by textual representation of IP address
				InetAddress addr = InetAddress.getByName(getIP());
				// Get the host name
				String namelabeltmp = addr.getHostName();
				String[] shortname = namelabeltmp.trim().split("\\.");
				if (getDnslabel() == 3) {

					if (!namelabeltmp.equals(getIP()))
						namelabel = shortname[0];
				} else {
					namelabel = namelabeltmp;
				}

				hostname = namelabel;
			} catch (UnknownHostException e) {
			}
		if (getDnslabel() == 4) {
			namelabel = label;
		}

		return namelabel;
	}

	public void setHostname(String hname) {
		hostname = hname;
	}

	public String getMACaddress() {
		return MACaddr;
	}

	public void delService(String name) {
		for (int i = 0; i < checkPorts.size(); i++) {
			Service t = (Service) checkPorts.get(i);
			if (t.getServiceName().equals(name))
				checkPorts.remove(i);
		}
	}

	public void setServiceStatus(String name, boolean b) {
		for (int i = 0; i < checkPorts.size(); i++) {
			Service t = (Service) checkPorts.get(i);
			if (t.getServiceName().equals(name))
				t.setStatus(b);
		}
	}

	public boolean getServiceStatus(String name) {
		boolean status = true;
		for (int i = 0; i < checkPorts.size(); i++) {
			Service t = (Service) checkPorts.get(i);
			if (t.getServiceName().equals(name))
				status = t.getStatus();
		}
		return status;
	}

	public long getUpTime() {
		return upTime;
	}

	public long getDownTime() {
		return downTime;
	}

	public void setUpTime(long newTime) {
		upTime = newTime;
	}

	public void setDownTime(long newTime) {
		downTime = newTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setStartTime() {
		startTime = System.currentTimeMillis() - upTime;
	}

	public void setStartDownTime() {
		endTime = System.currentTimeMillis() - downTime;
	}

	public void setIP(String IP) {
		IPlabel = IP;
	}

	public void toggleSelected() {
		selected = !selected;
	}

	public Vector neighbours() {
		Vector result = new Vector();

		Enumeration edges = incidentEdges.elements();
		while (edges.hasMoreElements()) {
			result.add(((Edge) edges.nextElement()).otherEndFrom(this));
		}
		return result;
	}

	public void draw(Graphics aPen) {
		hostname=NetworkManagerGUI.textHasContent(hostname)? hostname : IPlabel;
		Font small = NetworkManagerGUI.smallFont;
		Font base = NetworkManagerGUI.baseFont;
		aPen.setFont(base);
		if (NetworkManagerGUI.ZOOM)
			aPen.setFont(small);
		String badServices = "";
		boolean badServ = false;
		int b=0;
		for (int i = 0; i < getCheckPorts().size(); i++) {
			Service t = (Service) getCheckPorts().get(i);
			if (!t.getStatus()) {
			b++;
				if (b<=3)
				 badServices = badServices + " " + t.getServiceName().toLowerCase();
				else
				 badServices = b + " down";	
					
				badServ = true;
			}
		}
		if (selected) {
			aPen.setColor(NetworkManagerGUI.selColor);
			aPen.drawRect(location.x - RADIUS, location.y - RADIUS, RADIUS * 2,
					RADIUS * 2);
		}
		String gif = "pics/" + nodeType + ".gif";
		if (!NetworkManagerGUI.textHasContent(nodeType) || nodeType==null)
			nodeType="workstation";
		Image img = null;
		try {
			File sfile = new File(gif);
			img = ImageIO.read(sfile);
		} catch (IOException ex){
			
		}
		
		if (img!=null) {
		icon = new ImageIcon(img);
		if (badServ && !badStatus) {
			icon = new ImageIcon(ColorIcon.colorize(icon.getImage(), new Color(255, 128, 64)));
		}
		if (badStatus) {
			icon = new ImageIcon(ColorIcon.colorize(icon.getImage(), badColor));
		}
		Image image = icon.getImage();
		aPen.drawImage(image, location.x - RADIUS, location.y - RADIUS,
				nodeSize, nodeSize, null);
		} else {
			aPen.drawString("X", location.x, location.y);
			
		}
		if (nodeType.equals("network-cloud")) {
			aPen.setColor(Color.lightGray);
			aPen.fillOval(location.x - 75, location.y - 30, cloudradius,
					cloudradius);
			aPen.fillOval(location.x - 50, location.y - 10, cloudradius,
					cloudradius);
			aPen.fillOval(location.x - 50, location.y - 50, cloudradius,
					cloudradius);
			aPen.fillOval(location.x - 10, location.y - 10, cloudradius,
					cloudradius);
			aPen.fillOval(location.x - 10, location.y - 50, cloudradius,
					cloudradius);
			aPen.fillOval(location.x + 15, location.y - 30, cloudradius,
					cloudradius);
		}
		if (badServ && !badStatus) {
			aPen.setColor(new Color(255, 128, 64));
		} else if (badStatus) {
			aPen.setColor(badColor);
		} else {
			aPen.setColor(NetworkManagerGUI.textColor);
		}
		int length = label.length();
		int labelLocation = location.x - length * 2 - length;
		if (NetworkManagerGUI.ZOOM)
			labelLocation = location.x - length * 2;
		if (nodeType.equals("network-cloud")) {
			aPen.drawString(label, labelLocation, location.y);
		} else {
			if (dnslabel == 2) {
				aPen.drawString(hostname, labelLocation, location.y + RADIUS
						+ 15);
				aPen.drawString("(" + IPlabel + ")",
						labelLocation - RADIUS / 2, location.y + RADIUS + 30);
			} else
				aPen.drawString(label, labelLocation, location.y + RADIUS + 15);
		}
		if (snmp) {
			aPen.setColor(new Color(255, 128, 64));
			aPen.drawString("*", labelLocation - 5, location.y + RADIUS + 15);
		}
		int badlength = badServices.length();
		int badlabelLocation = location.x - badlength * 2 - badlength;
		if (NetworkManagerGUI.ZOOM)
			badlabelLocation = location.x - badlength * 2;
		if (badStatus && NetworkManagerGUI.MONITORING) {
			aPen.setColor(badColor);
			aPen.setFont(small);
			aPen.drawString(pingTimer.millisecondsToString(downTime),
					labelLocation - 5, location.y - RADIUS - 10);
			aPen.setFont(base);
		}
		if (badServ && !badStatus) {
			aPen.setColor(new Color(255, 128, 64));
			aPen.setFont(small);
			aPen.drawString(badServices, badlabelLocation, location.y - RADIUS
					- 10);
			aPen.setFont(base);
		} else {
			badServices = "";
		}
		if (showHops && NetworkManagerGUI.textHasContent(howHops)) {
			aPen.setColor(new Color(255, 128, 64));
			aPen.setFont(small);
			aPen.drawString(howHops, labelLocation + RADIUS, location.y
					- RADIUS - 10);
			aPen.setFont(base);
		} else {
			howHops = "";

		}

	}
	
	}
