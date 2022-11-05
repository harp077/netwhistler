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
package nnm.snmp;

import java.awt.*;
import javax.swing.*;

import java.util.*;
import uk.co.westhawk.snmp.stack.*;
import uk.co.westhawk.snmp.event.*;
import java.io.*;

import nnm.NetworkManagerGUI;
import nnm.util.MapFilter;

import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.border.EtchedBorder;
import java.awt.event.ActionEvent;
import java.text.Format;
import java.text.SimpleDateFormat;

public class TrapConsole extends JComponent implements TrapListener, Observer {
	public static SnmpContext context;

	public static JTextArea trapArea;

	public TrapConsole() {
		// AsnObject.setDebug(3);
	}

	public void init() {
		// AsnObject.setDebug(15);
		String host = "localhost";
		int port = SnmpContextBasisFace.DEFAULT_PORT;
		String socketType = SnmpContextBasisFace.STANDARD_SOCKET;
		String community = NetworkManagerGUI.trapCommunity;
		final JFrame frame = new JFrame();
		frame.setTitle("NetWhistler SNMP Trap Console");
		frame.setSize(600, 300);
		frame.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				if (frame.getWidth() < 600) {
					frame.setSize(600, frame.getHeight());
				}
				if (frame.getHeight() < 300) {
					frame.setSize(frame.getWidth(), 300);
				}
			}

			public void componentMoved(ComponentEvent componentEvent) {
			}

			public void componentShown(ComponentEvent componentEvent) {
			}

			public void componentHidden(ComponentEvent componentEvent) {
			}
		});
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
		JPanel scanPanel = new JPanel();
		scanPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		scanPanel.setLayout(new BorderLayout());
		trapArea = new JTextArea(20, 50);
		trapArea.setEditable(false);
		trapArea.setBackground(new Color(221, 221, 221));
		trapArea.setCaretPosition(trapArea.getDocument().getLength());
		JScrollPane scroll = new JScrollPane(trapArea);
		scanPanel.add(scroll);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton save = new JButton("Save");
		save.setBackground(NetworkManagerGUI.sysBackColor);
		buttonPanel.add(save);
		save.setFont(NetworkManagerGUI.baseFont);
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				NetworkManagerGUI.recursivelySetFonts(fc,
						NetworkManagerGUI.baseFont);
				fc.addChoosableFileFilter(new MapFilter("txt",
						NetworkManagerGUI.progName + " logs"));
				fc.setAcceptAllFileFilterUsed(false);
				int retval = fc.showSaveDialog(null);
				if (retval == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					try {
						PrintWriter aFile;
						String newFile = file.toString();
						if (newFile.endsWith("txt")) {
							aFile = new PrintWriter(new FileWriter(file));
						} else {
							aFile = new PrintWriter(new FileWriter(file
									+ ".txt"));
						}

						aFile.println(trapArea.getText());

						aFile.close();
					} catch (IOException ex) {
					}
				}
			}
		});
		JButton clear = new JButton("Clear");
		clear.setBackground(NetworkManagerGUI.sysBackColor);
		buttonPanel.add(clear);
		clear.setFont(NetworkManagerGUI.baseFont);
		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				trapArea.setText(" ");
			}
		});

		JButton cancel = new JButton("Close");
		cancel.setBackground(NetworkManagerGUI.sysBackColor);
		buttonPanel.add(cancel);
		cancel.setFont(NetworkManagerGUI.baseFont);
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
				frame.setVisible(false);
				frame.dispose();
			}
		});
		container.add(scanPanel, BorderLayout.CENTER);
		container.add(buttonPanel, BorderLayout.SOUTH);
		frame.getContentPane().add(container);
		frame.setVisible(true);

		try {
			DefaultTrapContext defTrap = DefaultTrapContext.getInstance(
					DefaultTrapContext.DEFAULT_TRAP_PORT, socketType);
			defTrap.addUnhandledTrapListener(this);
			context = new SnmpContext(host, port, socketType);
			context.setCommunity(community);
			context.addTrapListener(this);
			NetworkManagerGUI.logger.info(" Can't run trapd: Another SNMPD running?");
			// System.out.println("ReceiveTrap.init(): "
			// + context.toString());
		} catch (java.io.IOException exc) {
			// System.out.println("ReceiveTrap.init(): IOException "
			// + exc.getMessage());
			// exc.printStackTrace();
			try {
				context.removeTrapListener(this);
			} catch (IOException ex) {
			}
			// System.exit(0);
		}
	}

	public void update(Observable obs, Object ov) {
		Pdu pdu = (Pdu) obs;
		// System.out.println("ReceiveTrap.update(): " + pdu.toString());
	}

	public void destroy() {
		if (context != null) {
			context.destroy();
		}
	}

	public void close() {
		try {
			context.removeTrapListener(this);
		} catch (IOException ex) {
		}

	}

	public void trapReceived(TrapEvent evt) {
		Date now = new Date();
		Format formatter = new SimpleDateFormat("MM.dd.yyyy HH.mm.ss");
		// rkManagerGUI.lastResponse = formatter.format(now);

		if (evt.isDecoded()) {
			Pdu trapPdu = evt.getPdu();
			SnmpContextBasisFace context = trapPdu.getContext();
			//int version = context.getVersion();
			String host = context.getHost();
			//int reqId = trapPdu.getReqId();
			/*
			 * System.out.println("ReceiveTrap.trapReceived():" + " received
			 * decoded trap id " + reqId + ", v " + version + " from host " +
			 * host); /*
			 */
			/*
			 * System.out.println("ReceiveTrap.trapReceived():" + " received
			 * decoded trap " + trapPdu.toString());
			 */
			String msg = "[" + formatter.format(now) + "]" + "   " + host
					+ " : " + trapPdu.toString() + "\n";
			// System.out.println("Host: " + host + " Trap: " +
			// trapPdu.toString() + "\n");
			trapArea.append(msg);
		} else {
			try {
				Pdu trapPdu = context.processIncomingTrap(evt.getMessage());
				//int version = evt.getVersion();
				String host = evt.getHostAddress();
				/*
				 * System.out.println("ReceiveTrap.trapReceived():" + " received
				 * undecoded trap v " + version + " from host " + host +
				 * trapPdu.toString());
				 */
				String msg = " [ " + formatter.format(now) + " ]" + "   "
						+ host + " : " + trapPdu.toString() + "\n";
				trapArea.append(msg);
			} catch (IOException ex) {
			} catch (DecodingException ex) {
			}

		}

	}

	public static void showTrap() {
		TrapConsole trap = new TrapConsole();
		trap.init();
	}

}
