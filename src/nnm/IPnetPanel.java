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

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class IPnetPanel extends JPanel {
	public IPnetPanel() {
		setLayout(new BorderLayout());
		setBackground(NetworkManagerGUI.backgroundColor);

		String gif = "icons/ipnet.gif";
		NetworkManagerGUI.imageURL = NetworkManagerGUI.class.getResource(gif);
		ImageIcon icon = new ImageIcon(NetworkManagerGUI.imageURL);
		JLabel ipnetLab = new JLabel("IP Network", icon, SwingConstants.CENTER);
		ipnetLab.setFont(NetworkManagerGUI.baseFont);
		ipnetLab.setVerticalTextPosition(JLabel.BOTTOM);
		ipnetLab.setHorizontalTextPosition(JLabel.CENTER);
		ipnetLab.setHorizontalAlignment(JLabel.CENTER);
		ipnetLab.setVerticalAlignment(JLabel.CENTER);
		add(ipnetLab);
		ipnetLab.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					NetworkManagerGUI.topcards.show(
							NetworkManagerGUI.cardsPanel, "Second");
			}

			public void mousePressed(MouseEvent e) {

			}

			public void mouseReleased(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}
		});
	}

}
