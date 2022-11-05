// This file is part of the Mila NetWhistler Network Monitor.
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
//      Alexander Eremin    <netwhistler@gmail.com>
//		http://www.netwhistler.spb.ru
//

package nnm.inet.portscan;

import javax.swing.JFrame;
import nnm.NetworkManagerGUI;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.border.EtchedBorder;
import javax.swing.JTextArea;
import java.awt.Color;
import javax.swing.JScrollPane;
import java.awt.FlowLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner;
import javax.swing.JFormattedTextField;
import java.awt.Insets;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.util.Timer;
import javax.swing.Box;

public class ScanFrame {
	public ScanFrame() {
	};

	public static String addr;

	static int startport;

	static int endport;

	static JTextArea scanArea;

	Timer timer;

	static PortScan scan;

	public static void show(String host) {

		addr = host;
		// frame
		final JFrame frame = new JFrame();

		frame.setTitle("NetWhistler Portscan");
		frame.setSize(500, 350);
		// frame.setResizable(false);
		java.net.URL imageURL = NetworkManagerGUI.class
				.getResource("icons/nw.gif");
		ImageIcon frameIcon = new ImageIcon(imageURL);
		Image image = frameIcon.getImage();
		frame.setIconImage(image);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension optSize = frame.getSize();
		if (optSize.height > screenSize.height) {
			optSize.height = screenSize.height;
		}
		if (optSize.width > screenSize.width) {
			optSize.width = screenSize.width;
		}
		frame.setLocation((screenSize.width - optSize.width) / 2,
				(screenSize.height - optSize.height) / 2);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
		JPanel portP = new JPanel();
		portP.setLayout(new FlowLayout(FlowLayout.LEFT));

		JLabel tLab = new JLabel("Target: ");
		tLab.setFont(NetworkManagerGUI.baseFont);
		portP.add(tLab);
		JLabel hLab = new JLabel(addr);
		hLab.setFont(NetworkManagerGUI.baseFont);
		portP.add(hLab);
		portP.add(Box.createHorizontalStrut(20));
		JLabel stLab = new JLabel("Start port ");

		stLab.setFont(NetworkManagerGUI.baseFont);
		portP.add(stLab);
		startport = 21;
		endport = 1024;
		int min = 1;
		int max = 5999;
		int step = 1;
		long initValue = 21;
		final SpinnerModel model = new SpinnerNumberModel(initValue, min, max,
				step);
		JSpinner monSpin = new JSpinner(model);
		JFormattedTextField sttf = ((JSpinner.DefaultEditor) monSpin
				.getEditor()).getTextField();
		// Set the margin (add two spaces to the left and right side of the
		// value)
		int top = 0;
		int left = 2;
		int bottom = 0;
		int right = 2;
		Insets insets = new Insets(top, left, bottom, right);
		sttf.setMargin(insets);
		monSpin.setFont(NetworkManagerGUI.baseFont);
		monSpin.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				startport = ((Number) model.getValue()).intValue();
			}
		});

		portP.add(monSpin);
		JLabel enLab = new JLabel("Stop port ");

		enLab.setFont(NetworkManagerGUI.baseFont);
		portP.add(enLab);
		min = 2;
		max = 6000;
		step = 1;
		initValue = 1024;
		final SpinnerModel endmodel = new SpinnerNumberModel(initValue, min,
				max, step);
		JSpinner endmonSpin = new JSpinner(endmodel);
		JFormattedTextField entf = ((JSpinner.DefaultEditor) endmonSpin
				.getEditor()).getTextField();
		entf = ((JSpinner.DefaultEditor) endmonSpin.getEditor()).getTextField();
		// Set the margin (add two spaces to the left and right side of the
		// value)
		entf.setMargin(insets);
		endmonSpin.setFont(NetworkManagerGUI.baseFont);
		endmonSpin.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				endport = ((Number) endmodel.getValue()).intValue();
			}
		});

		portP.add(endmonSpin);

		//
		JPanel scanPanel = new JPanel();
		scanPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		scanPanel.setLayout(new BorderLayout());
		scanArea = new JTextArea(20, 40);
		scanArea.setEditable(false);
		scanArea.setBackground(new Color(221, 221, 221));
		scanArea.setCaretPosition(scanArea.getDocument().getLength());
		JScrollPane scroll = new JScrollPane(scanArea);
		scanPanel.add(scroll);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton start = new JButton("Scan");
		start.setBackground(NetworkManagerGUI.sysBackColor);
		buttonPanel.add(start);
		start.setFont(NetworkManagerGUI.baseFont);
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scanArea.setText(" ");
				scan = new PortScan(1, addr, startport, endport);

			}
		});
		JButton stop = new JButton("Stop");
		stop.setBackground(NetworkManagerGUI.sysBackColor);
		buttonPanel.add(stop);
		stop.setFont(NetworkManagerGUI.baseFont);
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Scan.stop();
				if (scan.timer != null) {
					scan.timer.cancel();
				}
			}
		});
		JButton cancel = new JButton("Close");
		cancel.setBackground(NetworkManagerGUI.sysBackColor);
		buttonPanel.add(cancel);
		cancel.setFont(NetworkManagerGUI.baseFont);
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				frame.dispose();
			}
		});
		container.add(portP, BorderLayout.NORTH);
		container.add(scanPanel, BorderLayout.CENTER);
		container.add(buttonPanel, BorderLayout.SOUTH);
		frame.getContentPane().add(container);
		frame.setVisible(true);
	}

}
