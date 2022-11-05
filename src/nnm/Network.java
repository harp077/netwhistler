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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import nnm.util.ColorIcon;


public class Network extends JLabel {
	
	private Point location;
	private String network;
	private String community;
	private boolean selected;
	private String gif;
	private int type,Icon;
	private ImageIcon icon;
	private boolean badStatus;
	private Vector incidentEdges;
	
	public Network() {
		this("", new Point(0, 0),"",0,0);
	}
	public Network(String aNetwork, Point aPoint, String aCommunity, int aType, int aIcon) {
		network = aNetwork;
		location = aPoint;
		community = aCommunity;
		type=aType;
		Icon =aIcon;
		incidentEdges = new Vector();
	}
	public String getNetwork(){
		return network;
	}
	public int getType(){
		return type;
	}
	public String getCommunity(){
		return community;
	}
	public Vector neighbours() {
		Vector result = new Vector();

		Enumeration edges = incidentEdges.elements();
		while (edges.hasMoreElements()) {
			result.add(((EdgeN) edges.nextElement()).otherEndFrom(this));
		}
		return result;
	}
	public Point getLocation() {
		return location;
	}
	public int getNetworkIcon() {
		return Icon;
	}

	public void setBadStatus(boolean state) {
		badStatus = state;
		if (!NetworkManagerGUI.MONITORING)
		NetworkManagerGUI.treePanel.treeModel.reload();
	}

	public boolean getBadStatus() {
		return badStatus;
	}
	 public Vector incidentEdges() {
			return incidentEdges;
		}

		public void addIncidentEdgeN(EdgeN e) {
			incidentEdges.add(e);
		}
	public void setLocation(Point p) {
		location = p;
	}
	public void setType(int n) {
		type = n;
	}
	public void setCommunity(String com) {
		community = com;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setNetworkIcon(int icon) {
		Icon =icon;
	}
	public void setSelected(boolean state) {
		selected = state;
	}
	public void toggleSelected() {
		selected = !selected;
	}
	public void draw(Graphics aPen) {
		if (selected) {
			aPen.setColor(NetworkManagerGUI.selColor);
			aPen.drawRect(location.x - 20, location.y - 20, 20 * 2,
					20 * 2);
		}
		aPen.setColor(NetworkManagerGUI.textColor);
		if (type==0)
		 gif = "pics/net.gif";
		else if (type==1)
		{
			if (Icon==1)
			 gif = 	"pics/router.gif";
		   else if (Icon==2)
		 gif = "pics/firewall.gif";
		}
		Image img = null;
		try {
			File sfile = new File(gif);
			img = ImageIO.read(sfile);
		} catch (IOException ex){
			
		}
		if (img!=null){
		icon = new ImageIcon(img);
		if (badStatus && type==1) {
			icon = new ImageIcon(ColorIcon.colorize(icon.getImage(), Color.red));
		}
			
		Image image = icon.getImage();
		
		aPen.drawImage(image, location.x - 20, location.y - 20,
				38, 38, null);
		}else{
			aPen.drawString("X", location.x, location.y);
		}
		int length = network.length();
		int labelLocation = location.x - length * 2 - length;
		if (badStatus && type==1) 
			aPen.setColor(Color.red);
		aPen.drawString(network, labelLocation, location.y + 20 + 15);
		
		}

	}


