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
package nnm.snmp.util;

import nnm.NetworkManager;
import nnm.NetworkManagerGUI;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.border.EtchedBorder;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import java.awt.Image;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JComponent;

import nnm.snmp.OidToCheck;
import nnm.snmp.MIBbrowser.SnmpMibTree;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.JDialog;

import nnm.util.MessageDialog;

public class OidView extends JPanel {
	public static String name, oid;

	static final int windowHeight = 300;

	static final int leftWidth = 280;

	static final int rightWidth = 260;

	static final int windowWidth = leftWidth + rightWidth;

	public OidView() {
		makeFrame();
	}

	public static void makeFrame() {
		// Set up a GUI framework
		final JFrame frame = new JFrame("SNMP MIB browser");
		java.net.URL imageURL = NetworkManagerGUI.class
				.getResource("icons/nw.gif");
		ImageIcon frameIcon = new ImageIcon(imageURL);
		Image image = frameIcon.getImage();
		frame.setIconImage(image);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.gc();
				System.runFinalization();
				frame.setVisible(false);
			}
		});

		final GUIMIBview echoPanel = new GUIMIBview();
		frame.getContentPane().add("Center", echoPanel);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		final JButton ok = new JButton("Ok");
		ok.setBackground(NetworkManagerGUI.sysBackColor);
		ok.setFont(NetworkManagerGUI.baseFont);
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!NetworkManagerGUI.textHasContent(name)) {
					JDialog tmp = new JDialog();
					new MessageDialog(tmp, "Choose SNMP Oid, please.",
							"SNMP Oid");
				} else {
					NetworkManager.oids.add(new OidToCheck(name, oid, ""));
					NetworkManager.oidmodel.addElement(name);
				}
				frame.setVisible(false);
			}
		});
		final JButton cancel = new JButton("Cancel");
		cancel.setBackground(NetworkManagerGUI.sysBackColor);
		cancel.setFont(NetworkManagerGUI.baseFont);
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
			}
		});
		buttonPanel.add(ok);
		buttonPanel.add(cancel);
		frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		frame.pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int w = windowWidth + 10;
		int h = windowHeight + 10;
		frame.setSize(w, h);
		Dimension optSize = frame.getSize();
		if (optSize.height > screenSize.height) {
			optSize.height = screenSize.height;
		}
		if (optSize.width > screenSize.width) {
			optSize.width = screenSize.width;
		}
		frame.setLocation((screenSize.width - optSize.width) / 2,
				(screenSize.height - optSize.height) / 2);

		frame.setVisible(true);

	} // makeFrame

	private void jbInit() throws Exception {
	}
}

class GUIMIBview extends JPanel {
	private String oid;

	private String value;

	public JTextArea textArea;

	public static JTextField toid, tvalue;

	SnmpMibTree treeSupport;

	JTree tree;

	final static JPanel mainP = new JPanel();

	final set_one application = new set_one();

	GUIMIBview() {
		// Make a nice border
		this.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		// Set up the tree
		treeSupport = new SnmpMibTree();
		if (!treeSupport.addDirectory("snmp/mibs/")) {
			JDialog tmp = new JDialog();
			new MessageDialog(tmp, " Can't found base MIBs!", "Load base MIBs");

		}
		tree = treeSupport.buildTree();
		ImageIcon openicon = null;
		ImageIcon leaficon = null;
		ImageIcon closedicon = null;
		java.net.URL imgURL = NetworkManagerGUI.class
				.getResource("icons/folder.gif");
		closedicon = new ImageIcon(imgURL);
		imgURL = NetworkManagerGUI.class.getResource("icons/oid.gif");
		leaficon = new ImageIcon(imgURL);
		imgURL = NetworkManagerGUI.class.getResource("icons/tree.gif");
		openicon = new ImageIcon(imgURL);
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setOpenIcon(openicon);
		renderer.setLeafIcon(leaficon);
		renderer.setClosedIcon(closedicon);
		tree.setCellRenderer(renderer);

		// Build left-side view
		JScrollPane treeView = new JScrollPane(tree);
		treeView.setPreferredSize(new Dimension(OidView.leftWidth,
				OidView.windowHeight));
		JPanel loadPanel = new JPanel();
		loadPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton btnMib = new JButton("Load MIB");
		btnMib.setBackground(NetworkManagerGUI.sysBackColor);
		btnMib.setFont(NetworkManagerGUI.baseFont);
		btnMib.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadNewMib();
			}
		});
		loadPanel.add(btnMib);
		JPanel treePane = new JPanel(new BorderLayout());
		treePane.add("Center", treeView);
		treePane.add("South", loadPanel);

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				TreePath p = e.getNewLeadSelectionPath();
				if (p != null) {
					DefaultMutableTreeNode defaultmutabletreenode = (DefaultMutableTreeNode) tree
							.getLastSelectedPathComponent();
					// / here string!!!!

					String oid = treeSupport.getOID
							.getNodeOid(defaultmutabletreenode);
					String oidName = defaultmutabletreenode.toString();
					application.toid.setText(oidName);
					OidView.name = oidName;
					application.tvalue.setText(oid.trim());
					OidView.oid = oid.substring(1);

				}
			}
		});

		// Build split-pane view
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				treePane, application);
		splitPane.setContinuousLayout(true);
		splitPane.setDividerSize(5);
		splitPane.setDividerLocation(OidView.leftWidth);
		splitPane.setPreferredSize(new Dimension(OidView.windowWidth + 10,
				OidView.windowHeight + 10));

		this.setLayout(new BorderLayout());
		this.add("Center", splitPane);
		application.init();
	}

	public void loadNewMib() {
		int ok = 0;
		try {
			JFileChooser fc = new JFileChooser();
			FileFilter filter = new FileFilter() {
				public boolean accept(File f) {
					return f.isDirectory()? true:
						f.getAbsolutePath().toLowerCase().endsWith(".mib");
				}
				public String getDescription() {
					return "SNMP mibs";
				}
			};
		  fc.setFileFilter(filter);
			NetworkManagerGUI.recursivelySetFonts(fc,
					NetworkManagerGUI.baseFont);
			fc.setDialogTitle("Load MIB");
			// fc.addChoosableFileFilter(new MapFilter("mib", "SNMP mibs"));
			fc.setCurrentDirectory(new File("."));
			fc.setMultiSelectionEnabled(true);
			String s = "";
			int i = fc.showOpenDialog(null);
			if (i == 0) {
				File afile[] = fc.getSelectedFiles();
				if (afile != null && afile.length > 0) {
					for (int j = 0; j < afile.length; j++) {
						loadSingleFile(afile[j]);

					}
				}
			} else {
				return;
			}
		}

		catch (Exception exception) {
			ok = 1;
		}
		if (ok == 1) {
			JDialog tmp = new JDialog();
			new MessageDialog(tmp, " Can't load MIB! ", "Load MIB");
		}

	}

	private void loadSingleFile(File file) {
		String s = file.getAbsolutePath();
		treeSupport.loadNewFile(s);

	}

	public class set_one extends JComponent {

		private String oid;

		private String value;

		public JTextField toid, tvalue;

		public set_one() {
		}

		public void init() {

			makeLayout();

		}

		private void makeLayout() {

			JLabel loid, lvalue;
			this.setLayout(new BorderLayout());
			final JPanel mainP = new JPanel();
			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(1, 1, 1, 1);
			c.anchor = GridBagConstraints.EAST;
			c.fill = GridBagConstraints.HORIZONTAL;
			mainP.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

			loid = new JLabel("SNMP OID Name: ");
			loid.setFont(NetworkManagerGUI.baseFont);
			lvalue = new JLabel("SNMP OID Number: ");
			lvalue.setFont(NetworkManagerGUI.baseFont);

			toid = new JTextField(20);
			toid.setFont(NetworkManagerGUI.baseFont);
			toid.setText(oid);
			tvalue = new JTextField(20);
			lvalue.setFont(NetworkManagerGUI.baseFont);
			tvalue.setText(value);

			mainP.setLayout(new GridBagLayout());
			c.gridx = 0;
			c.gridy = 2;
			mainP.add(loid, c);
			c.gridx = 0;
			c.gridy = 4;
			mainP.add(toid, c);
			c.gridx = 0;
			c.gridy = 6;
			mainP.add(lvalue, c);
			c.gridx = 0;
			c.gridy = 8;
			mainP.add(tvalue, c);

			this.add(mainP, BorderLayout.CENTER);

		}

	}
} // constructor
