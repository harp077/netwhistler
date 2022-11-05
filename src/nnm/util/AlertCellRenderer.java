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
package nnm.util;

import javax.swing.JLabel;
import javax.swing.ListCellRenderer;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.BorderFactory;
import java.awt.Color;
import nnm.*;
import javax.swing.ImageIcon;

public class AlertCellRenderer extends JLabel implements ListCellRenderer {
	final static ImageIcon okIcon = new ImageIcon(NetworkManagerGUI.class
			.getResource("icons/ok.gif"));

	final static ImageIcon badIcon = new ImageIcon(NetworkManagerGUI.class
			.getResource("icons/bad.gif"));

	public AlertCellRenderer() {
		setOpaque(true);
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean iss, boolean chf) {
		int isOk = ((ListItem) value).getOk();
		if (isOk == 2) {
			setIcon(okIcon);
		} else if (isOk == 1) {
			setIcon(badIcon);
		} else {
			setIcon(null);
		}
		// System.out.print(isOk);
		setText(((ListItem) value).getValue());

		setBackground(((ListItem) value).getBColor());
		setForeground(((ListItem) value).getFColor());
		setFont(NetworkManagerGUI.smallFont);
		setBorder(BorderFactory.createLineBorder(Color.black, 2));
		if (iss) {
			setBorder(BorderFactory.createLineBorder(Color.black, 2));
		} else {
			setBorder(BorderFactory.createLineBorder(list.getBackground(), 2));
		}
		return this;
	}
}
