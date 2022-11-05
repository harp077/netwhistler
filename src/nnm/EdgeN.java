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

import java.awt.Graphics;

public class EdgeN {
	private String label;

	private Network startNet, endNet;

	private boolean selected;

	public EdgeN(Network start, Network end) {
		label = NetworkManager.currentNetwork;
		startNet = start;
		endNet = end;
	}

	public EdgeN(String aLabel, Network start, Network end) {
		label = aLabel;
		startNet = start;
		endNet = end;
	}

	public String getLabel() {
		return label;
	}

	public Network getStartNet() {
		return startNet;
	}

	public Network getEndNet() {
		return endNet;
	}

	public void setLabel(String newLabel) {
		label = newLabel;
	}

	public void setStartNet(Network aNet) {
		startNet = aNet;
	}

	public void setEndNet(Network aNet) {
		endNet = aNet;
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

	public Network otherEndFrom(Network aNet) {
		if (startNet == aNet) {
			return endNet;
		} else {
			return startNet;
		}
	}

	public String toString() {
		return (startNet.toString() + " : " + endNet.toString());
	}

	public void draw(Graphics aPen) {
		if (selected) {
			aPen.setColor(NetworkManagerGUI.selColor);
		} else {
			aPen.setColor(NetworkManagerGUI.lineColor);
		}
		aPen.drawLine(startNet.getLocation().x, startNet.getLocation().y,
				endNet.getLocation().x, endNet.getLocation().y);
	}

}
