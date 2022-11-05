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

import java.io.*;
import java.awt.Graphics;

public class Edge {
	private String label;

	private Node startNode, endNode;

	private boolean selected;

	public Edge(Node start, Node end) {
		label = NetworkManager.currentNetwork;
		startNode = start;
		endNode = end;
	}

	public Edge(String aLabel, Node start, Node end) {
		label = aLabel;
		startNode = start;
		endNode = end;
	}

	public String getLabel() {
		return label;
	}

	public Node getStartNode() {
		return startNode;
	}

	public Node getEndNode() {
		return endNode;
	}

	public void setLabel(String newLabel) {
		label = newLabel;
	}

	public void setStartNode(Node aNode) {
		startNode = aNode;
	}

	public void setEndNode(Node aNode) {
		endNode = aNode;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean state) {
		selected = state;
	}

	public void toggleSelected() {
		selected = !selected;
	}

	public Node otherEndFrom(Node aNode) {
		if (startNode == aNode) {
			return endNode;
		} else {
			return startNode;
		}
	}

	public String toString() {
		return (startNode.toString() + " : " + endNode.toString());
	}

	public void draw(Graphics aPen) {
		if (selected) {
			aPen.setColor(NetworkManagerGUI.selColor);
		} else {
			aPen.setColor(NetworkManagerGUI.lineColor);
		}
		aPen.drawLine(startNode.getLocation().x, startNode.getLocation().y,
				endNode.getLocation().x, endNode.getLocation().y);
	}

}
