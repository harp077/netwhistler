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

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.Timer;
import javax.swing.JWindow;
import javax.swing.ImageIcon;
import nnm.*;

public class SplashScreen extends JWindow {
	private Image image;

	private int x, y, width, height;

	public SplashScreen() {
		super(new Frame());
		try {
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			NetworkManagerGUI.imageURL = NetworkManagerGUI.class
					.getResource("icons/splash.png");
			ImageIcon icon = new ImageIcon(NetworkManagerGUI.imageURL);
			image = icon.getImage();
			width = image.getWidth(this);
			height = image.getHeight(this);

			Dimension screenSize = toolkit.getScreenSize();

			x = (screenSize.width - width) / 2;
			y = (screenSize.height - height) / 2;
		} catch (Exception exception) {
			image = null;
		}
	}

	public void open(int nMilliseconds) {
		if (image == null) {
			return;
		}

		Timer timer = new Timer(Integer.MAX_VALUE, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				((Timer) event.getSource()).stop();
				close();
			};
		});

		timer.setInitialDelay(nMilliseconds);
		NetworkManagerGUI.splash = true;
		timer.start();

		setBounds(x, y, width, height);
		setVisible(true);
		toFront();
	}

	public void close() {
		setVisible(false);
		dispose();
		NetworkManagerGUI.splash = false;
	}

	public void paint(Graphics graphics) {
		if (image == null) {
			return;
		}
		graphics.drawImage(image, 0, 0, width, height, this);
	}
}
