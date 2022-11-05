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

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.*;
import javax.swing.text.*;
import nnm.inet.CheckServices;
import nnm.inet.LocalDiscover;
import nnm.inet.TraceRoute;
import nnm.inet.ValidEmail;
import nnm.inet.setBindName;
import nnm.inet.syslog.SyslogConsole;
import nnm.snmp.TrapConsole;
import nnm.util.Blinker;
import nnm.util.ConfirmDialog;
import nnm.util.ExtCmd;
import nnm.util.FixedLengthPlainDocument;
import nnm.util.FixedLengthTextField;
import nnm.util.HelpDlg;
import nnm.util.HowNodes;
import nnm.util.ImageFilter;
import nnm.util.InputDialog;
import nnm.util.MapFilter;
import nnm.util.MessageDialog;
import nnm.util.MouseXY;
import nnm.util.SplashScreen;
import nnm.util.Ticker;
import nnm.util.nFormatter;
import nnm.util.openMap;
import nnm.xml.MapWriter;
import org.jrobin.mrtg.client.Client;

public class NetworkManagerGUI extends JFrame {

	public static String progName = "NetWhistler";

	public static String version = "2.10";
	public static FileHandler handler,phandler; 
	public static Logger logger,plogger;
	public static NetworkManager manager;

	public static JFrame frame;

	public static JLabel status;

	public static JScrollPane jsp;

	public static JScrollPane netjsp;

	public static Font baseFont = new Font(null, 0, 12);

	public static Font smallFont = new Font(null, 0, 11);

	public static File filePath;

	public static JButton zoomBut;

	public static String network = "";

	public static boolean MONITORING;

	public static boolean ZOOM = false;

	public static Color backgroundColor;

	public static Color sysBackColor = SystemColor.controlHighlight;

	public static Color textColor;

	public static Color lineColor;

	public static Color selColor;

	public static int timeoutMon = 5;

	public static int timeoutMonServices = 5;

	public static int timeoutMonSNMP = 5;

	public static int monRetries = 3;

	public static int replyTime = 25;

	public static String lastResponse;

	public static boolean alerts = false;

	public static boolean email = false;

	public static boolean alertcmd = false;

	public static String alertCommand = "";

	public static boolean customPing = false;

	public static boolean customMac = false;

	public static String cpingCommand = "";

	public static String cmacCommand = "";
	
	public static boolean customSnmp = false;
	public static String snmpScript = "";
	
	public static boolean shadow;

	public static String alertAddress = "";

	public static String smtpAddress = "";

	public static boolean htmlAlert = true;

	public static int boxWidth;

	public static int boxHeight;

	public static Color boxColor;

	public static Color boxBackColor;

	public static Color boxTitleColor;

	public static Color boxShadColor;

	public static int monNodes;

	public static boolean splash = false;

	public static ImageIcon ticon;

	public static URL imageURL;

	public static boolean usbImage = false;

	public static File backFile;

	public static String wizCommunity = "public";

	public static String trapCommunity = "public";

	public static JButton monBut, upBut, findBut, optBut;

	public static JButton cancelStatus;

	public static ImageIcon ticonstart;

	public static ImageIcon ticonstop;

	public static ImageIcon ticonout;

	public static ImageIcon ticonin;

	public static JComboBox findtf;

	public static JMenuItem zoomItem, saveItem;

	public static JMenuItem closeItem;

	public static boolean showStatus = false;

	public static boolean showTrace = false;

	public static JTextArea statusArea;

	public static JLabel nodelb;

	public static JLabel mouselb;

	public static JPanel statusP = new JPanel();

	public static String Driver = "";

	
	public static Vector extCommands = new Vector();
	
	public static Vector <String> nodeTypes = new Vector<String>();
	public static DefaultListModel nodeTypesModel;

	public static JList nodeTypesList;
	
	public static DefaultListModel cmdmodel;

	public static JList cmdList;

	public static JPanel extcmdP;
	public static JPanel nodeTypesP;
	public static ExtCmd cmd;

	public static JMenuItem monItem;

	public static boolean FULLSCREEN = false;

	public static MapTree treePanel;

	public static JSplitPane splitPane;

	public static StatusPanel statPanel;

	public static IfacePanel ifacePanel;

	public static EventPanel eventPanel;

	public static GraphsPanel graphsPanel;

	public static CardLayout panels;

	public static JPanel basePanel;

	public static CardLayout topcards;

	public static JPanel cardsPanel;

	public static IPnetPanel ipnetPanel;

	public static NetPanel netPanel;

	public NetworkManagerGUI() {

	}

	public static void writeMap(File file) throws IOException {
		File f = file;
		String newFile = file.toString();
		if (newFile.toLowerCase().endsWith("xml"))
			filePath = f;
		else
			filePath = new File(f + ".xml");
		MapWriter.write(filePath);
	}

	public static void loadMap(File file) throws IOException {
		File f = file;
		new openMap(f, 1);
	}

	public static void groupDialog() {
		final JDialog dialog = new JDialog();
		dialog.setTitle("Map Operations");
		dialog.setSize(340, 190);
		dialog.setResizable(false);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension optSize = dialog.getSize();
		if (optSize.height > screenSize.height)
			optSize.height = screenSize.height;
		if (optSize.width > screenSize.width)
			optSize.width = screenSize.width;
		dialog.setLocation((screenSize.width - optSize.width) / 2,
				(screenSize.height - optSize.height) / 2);
		dialog.setDefaultCloseOperation(1);
		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
		JPanel groupPanel = new JPanel();
		groupPanel.setBorder(new EtchedBorder(1));
		groupPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(1, 1, 1, 1);
		c.anchor = 17;
		c.fill = 2;
		final JRadioButton ipBut = new JRadioButton(
				"Replace All Labels by IP Addresses");
		c.gridx = 0;
		c.gridy = 2;
		ipBut.setFont(baseFont);
		ipBut.setSelected(true);
		groupPanel.add(ipBut, c);
		final JRadioButton dnsBut = new JRadioButton(
				"Replace All Labels by DNS names");
		c.gridx = 0;
		c.gridy = 4;
		dnsBut.setFont(baseFont);
		groupPanel.add(dnsBut, c);
		final JRadioButton ipdnsBut = new JRadioButton(
				"Replace All Labels by IP Addresses + DNS names");
		c.gridx = 0;
		c.gridy = 6;
		ipdnsBut.setFont(baseFont);
		groupPanel.add(ipdnsBut, c);
		final JRadioButton shdnsBut = new JRadioButton(
				"Replace All Labels by Short DNS names");
		c.gridx = 0;
		c.gridy = 8;
		shdnsBut.setFont(baseFont);
		groupPanel.add(shdnsBut, c);
		ipBut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				dnsBut.setSelected(false);
				ipdnsBut.setSelected(false);
				shdnsBut.setSelected(false);
				ipBut.setSelected(true);
			}

		});
		dnsBut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ipBut.setSelected(false);
				ipdnsBut.setSelected(false);
				shdnsBut.setSelected(false);
				dnsBut.setSelected(true);
			}

		});
		ipdnsBut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				dnsBut.setSelected(false);
				ipBut.setSelected(false);
				shdnsBut.setSelected(false);
				ipdnsBut.setSelected(true);
			}

		});
		shdnsBut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				dnsBut.setSelected(false);
				ipBut.setSelected(false);
				ipdnsBut.setSelected(false);
				shdnsBut.setSelected(true);
			}

		});
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(2));
		JButton ok = new JButton("OK");
		ok.setFont(baseFont);
		ok.setBackground(sysBackColor);
		ok.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (ipBut.isSelected()) {
					for (int i = 0; i < Graph.nodes.size(); i++) {
						Node aNode = (Node) Graph.nodes.get(i);
						aNode.setDnslabel(0);
						if (!aNode.getnodeType().equals("hub")
								&& !aNode.getnodeType().equals("network-cloud"))
							aNode.setLabel(aNode.getIP());
					}

				} else if (dnsBut.isSelected()) {
					for (int i = 0; i < Graph.nodes.size(); i++) {
						Node aNode = (Node) Graph.nodes.get(i);
						if (!aNode.getnodeType().equals("hub")
								&& !aNode.getnodeType().equals("network-cloud")) {
							new setBindName(1, aNode, true);
							aNode.setDnslabel(1);
						}
					}

				} else if (ipdnsBut.isSelected()) {
					for (int i = 0; i < Graph.nodes.size(); i++) {
						Node aNode = (Node) Graph.nodes.get(i);
						if (!aNode.getnodeType().equals("hub")
								&& !aNode.getnodeType().equals("network-cloud"))
							aNode.setDnslabel(2);
					}

				} else if (shdnsBut.isSelected()) {
					for (int i = 0; i < Graph.nodes.size(); i++) {
						Node aNode = (Node) Graph.nodes.get(i);
						if (!aNode.getnodeType().equals("hub")
								&& !aNode.getnodeType().equals("network-cloud")) {
							aNode.setDnslabel(3);
							aNode.setLabel(aNode.getDNSname());
						}
					}

				}
				manager.repaint();
				dialog.setVisible(false);
				dialog.dispose();
			}

		});
		buttonPanel.add(ok);
		JButton cancel = new JButton("Cancel");
		cancel.setBackground(sysBackColor);
		buttonPanel.add(cancel);
		cancel.setFont(baseFont);
		cancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
				dialog.dispose();
			}

		});
		container.add(groupPanel, "Center");
		container.add(buttonPanel, "South");
		dialog.getContentPane().add(container);
		dialog.setVisible(true);
	}

	public static void optShapeDialog(Shape sShape, boolean addBox, Point p) {
		final Shape aShape = sShape;
		final boolean add = addBox;
		final Point point = p;
		final JDialog dialog = new JDialog();
		dialog.setTitle("Box Properties");
		dialog.setSize(320, 235);
		dialog.setResizable(false);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension optSize = dialog.getSize();
		if (optSize.height > screenSize.height)
			optSize.height = screenSize.height;
		if (optSize.width > screenSize.width)
			optSize.width = screenSize.width;
		dialog.setLocation((screenSize.width - optSize.width) / 2,
				(screenSize.height - optSize.height) / 2);
		dialog.setDefaultCloseOperation(1);
		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
		JTabbedPane tabs = new JTabbedPane();
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(1, 1, 1, 1);
		c.anchor = 17;
		c.fill = 2;
		JPanel colP = new JPanel();
		colP.setBorder(new EtchedBorder(1));
		colP.setLayout(new GridBagLayout());
		JLabel titLab = new JLabel("Title Color  ");
		c.gridx = 0;
		c.gridy = 2;
		titLab.setFont(baseFont);
		colP.add(titLab, c);
		colP.add(Box.createHorizontalStrut(20));
		final JButton titColorBut = new JButton(" ");
		if (!add)
			titColorBut.setBackground(aShape.getTitleColor());
		else
			titColorBut.setBackground(Color.black);
		c.gridx = 6;
		c.gridy = 2;
		titColorBut.setFont(baseFont);
		colP.add(titColorBut, c);
		titColorBut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JColorChooser colorChooser = new JColorChooser();
				JDialog Coldialog = JColorChooser.createDialog(null,
						"Select a Title Color", true, colorChooser, null, null);
				recursivelySetFonts(Coldialog, baseFont);
				Coldialog.setResizable(false);
				Coldialog.setVisible(true);
				Color c = colorChooser.getColor();
				if (c != null) {
					boxTitleColor = c;
					titColorBut.setBackground(boxTitleColor);
					dialog.repaint();
				}
			}

		});
		JLabel forLab = new JLabel("Box Foreground Color  ");
		c.gridx = 0;
		c.gridy = 4;
		forLab.setFont(baseFont);
		colP.add(forLab, c);
		colP.add(Box.createHorizontalStrut(20));
		final JButton forColorBut = new JButton(" ");
		if (!add)
			forColorBut.setBackground(aShape.getBoxColor());
		else
			forColorBut.setBackground(Color.gray);
		c.gridx = 6;
		c.gridy = 4;
		forColorBut.setFont(baseFont);
		colP.add(forColorBut, c);
		forColorBut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JColorChooser colorChooser = new JColorChooser();
				JDialog Coldialog = JColorChooser.createDialog(null,
						"Select a Foreground Color", true, colorChooser, null,
						null);
				recursivelySetFonts(Coldialog, baseFont);
				Coldialog.setResizable(false);
				Coldialog.setVisible(true);
				Color c = colorChooser.getColor();
				if (c != null) {
					boxColor = c;
					forColorBut.setBackground(boxColor);
					dialog.repaint();
				}
			}

		});
		JLabel backLab = new JLabel("Box Background Color  ");
		c.gridx = 0;
		c.gridy = 6;
		backLab.setFont(baseFont);
		colP.add(backLab, c);
		colP.add(Box.createHorizontalStrut(20));
		final JButton backColorBut = new JButton(" ");
		if (!add)
			backColorBut.setBackground(aShape.getBackColor());
		else
			backColorBut.setBackground(Color.white);
		c.gridx = 6;
		c.gridy = 6;
		backColorBut.setFont(baseFont);
		colP.add(backColorBut, c);
		backColorBut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JColorChooser colorChooser = new JColorChooser();
				JDialog Coldialog = JColorChooser.createDialog(null,
						"Select a Background Color", true, colorChooser, null,
						null);
				recursivelySetFonts(Coldialog, baseFont);
				Coldialog.setResizable(false);
				Coldialog.setVisible(true);
				Color c = colorChooser.getColor();
				if (c != null) {
					boxBackColor = c;
					backColorBut.setBackground(boxBackColor);
					dialog.repaint();
				}
			}

		});
		JLabel shaLab = new JLabel("Box Shadow Color  ");
		c.gridx = 0;
		c.gridy = 8;
		shaLab.setFont(baseFont);
		colP.add(shaLab, c);
		colP.add(Box.createHorizontalStrut(20));
		final JButton shaColorBut = new JButton(" ");
		if (!add)
			shaColorBut.setBackground(aShape.getShadColor());
		else
			shaColorBut.setBackground(Color.lightGray);
		c.gridx = 6;
		c.gridy = 8;
		shaColorBut.setFont(baseFont);
		colP.add(shaColorBut, c);
		shaColorBut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JColorChooser colorChooser = new JColorChooser();
				JDialog Coldialog = JColorChooser
						.createDialog(null, "Select a Shadow Color", true,
								colorChooser, null, null);
				recursivelySetFonts(Coldialog, baseFont);
				Coldialog.setResizable(false);
				Coldialog.setVisible(true);
				Color c = colorChooser.getColor();
				if (c != null) {
					boxShadColor = c;
					shaColorBut.setBackground(boxShadColor);
					dialog.repaint();
				}
			}

		});
		colP.add(Box.createHorizontalStrut(20));
		final JCheckBox usBox = new JCheckBox("Enable Box Shadow");
		usBox.setBackground(sysBackColor);
		usBox.setFont(baseFont);
		if (!add && aShape.getShadow())
			usBox.setSelected(true);
		else
			usBox.setSelected(false);
		c.gridx = 0;
		c.gridy = 10;
		colP.add(usBox, c);
		usBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (usBox.isSelected())
					shadow = true;
				else
					shadow = false;
			}

		});
		JPanel titleP = new JPanel();
		JPanel btnP = new JPanel();
		btnP.setLayout(new FlowLayout(0));
		titleP.setBorder(new EtchedBorder(1));
		titleP.setLayout(new BorderLayout());
		final JTextArea textArea = new JTextArea(10, 5);
		textArea.setDocument(new FixedLengthPlainDocument(300));
		textArea.setCaretPosition(textArea.getDocument().getLength());
		if (!add) {
			String text[] = aShape.getText();
			for (int i = 0; i < text.length; i++)
				if (textHasContent(text[i]))
					textArea.append(text[i] + "\n");

		}
		JScrollPane scroll = new JScrollPane(textArea);
		JLabel boxLab = new JLabel("Box Title");
		boxLab.setFont(baseFont);
		final JTextField boxtf = new FixedLengthTextField(20);
		boxtf.setFont(baseFont);
		if (!add)
			boxtf.setText(aShape.getLabel());
		else
			boxtf.setText("Untitled");
		btnP.add(boxLab, c);
		btnP.add(boxtf, c);
		titleP.add(btnP, "North");
		titleP.add(scroll, "Center");
		tabs.setFont(baseFont);
		tabs.addTab("Colors", null, colP);
		tabs.addTab("Title", null, titleP);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(2));
		JButton ok = new JButton("OK");
		ok.setBackground(sysBackColor);
		ok.setFont(baseFont);
		ok.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String title = boxtf.getText();
				String text[] = new String[300];
				Element root = textArea.getDocument().getDefaultRootElement();
				for (int i = 0; i < root.getElementCount(); i++) {
					Element row = root.getElement(i);
					int start = row.getStartOffset();
					int end = row.getEndOffset();
					try {
						text[i] = textArea.getDocument().getText(start,
								end - start);
						if (textHasContent(text[i])
								&& !text[i].toString().equals("null")
								&& text[i].length() > 0)
							text[i] = text[i]
									.substring(0, text[i].length() - 1);
					} catch (BadLocationException badlocationexception) {
					}
				}

				if (!textHasContent(title)) {
					new MessageDialog(dialog, "Enter Box title, please.",
							"Box Title");
				} else {
					if (add) {
						Shape sShape = new Shape(title,
								NetworkManager.currentNetwork, new Rectangle(
										point.x, point.y, 100, 100), text);
						sShape.setBackColor(backColorBut.getBackground());
						sShape.setBoxColor(forColorBut.getBackground());
						sShape.setTitleColor(titColorBut.getBackground());
						sShape.setShadColor(shaColorBut.getBackground());
						if (shadow)
							sShape.setShadow(true);
						else
							sShape.setShadow(false);
						NetworkManager.aGraph.addShape(sShape);
					} else {
						aShape.setBackColor(backColorBut.getBackground());
						aShape.setBoxColor(forColorBut.getBackground());
						aShape.setTitleColor(titColorBut.getBackground());
						aShape.setShadColor(shaColorBut.getBackground());
						aShape.setLabel(title);
						aShape.setNetwork(NetworkManager.currentNetwork);
						aShape.setText(text);
						if (shadow)
							aShape.setShadow(true);
						else
							aShape.setShadow(false);
					}
					manager.repaint();
					dialog.setVisible(false);
					dialog.dispose();
				}
			}

		});
		buttonPanel.add(ok);
		JButton cancel = new JButton("Cancel");
		cancel.setBackground(sysBackColor);
		buttonPanel.add(cancel);
		cancel.setFont(baseFont);
		cancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
				dialog.dispose();
			}

		});
		container.add(tabs, "Center");
		container.add(buttonPanel, "South");
		dialog.getContentPane().add(container);
		dialog.setVisible(true);
	}

	public static void optionsDialog() {
		final JDialog dialog = new JDialog();
		dialog.setTitle("Map Options");
		dialog.setSize(410, 330);
		dialog.setResizable(false);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension optSize = dialog.getSize();
		if (optSize.height > screenSize.height)
			optSize.height = screenSize.height;
		if (optSize.width > screenSize.width)
			optSize.width = screenSize.width;
		dialog.setLocation((screenSize.width - optSize.width) / 2,
				(screenSize.height - optSize.height) / 2);
		dialog.setDefaultCloseOperation(1);
		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
		JTabbedPane tabs = new JTabbedPane();
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(1, 1, 1, 1);
		c.anchor = 17;
		c.fill = 2;
		// ////////////////////////////////
		JPanel customscriptsP = new JPanel();
		customscriptsP.setPreferredSize(new Dimension(
				customscriptsP.getWidth(), 120));
		customscriptsP.setMinimumSize(new Dimension(customscriptsP.getWidth(),
				120));
		customscriptsP.setBorder(new EtchedBorder(1));
		customscriptsP.setLayout(new GridBagLayout());
		JLabel customLab = new JLabel("Custom Scripts ");
		c.gridx = 0;
		c.gridy = 0;
		customLab.setFont(baseFont);
		customscriptsP.add(customLab, c);
		final JCheckBox cpingBox = new JCheckBox("Ping");
		cpingBox.setFont(baseFont);
		if (customPing)
			cpingBox.setSelected(true);
		else
			cpingBox.setSelected(false);
		c.gridx = 0;
		c.gridy = 2;
		customscriptsP.add(cpingBox, c);
		final JTextField cpingtf = new FixedLengthTextField(25);
		c.gridx = 4;
		c.gridy = 2;
		cpingtf.setFont(baseFont);
		if (customPing)
			cpingtf.setText(cpingCommand);

		customscriptsP.add(cpingtf, c);
		final JCheckBox cmacBox = new JCheckBox("MAC address");
		cmacBox.setFont(baseFont);
		if (customMac)
			cmacBox.setSelected(true);
		else
			cmacBox.setSelected(false);
		c.gridx = 0;
		c.gridy = 4;
		customscriptsP.add(cmacBox, c);
		final JTextField cmactf = new FixedLengthTextField(25);
		c.gridx = 4;
		c.gridy = 4;
		cmactf.setFont(baseFont);
		if (customMac)
			cmactf.setText(cmacCommand);

		customscriptsP.add(cmactf, c);
		// ////////////////////////////////
		JPanel cmonP = new JPanel();
		cmonP.setLayout(new GridBagLayout());
		JLabel monLab = new JLabel("Monitoring Period (seconds) ");
		c.gridx = 0;
		c.gridy = 4;
		monLab.setFont(baseFont);
		cmonP.add(monLab, c);
		int min = 5;
		int max = 2000;
		int step = 1;
		long initValue = timeoutMon;
		final SpinnerModel model = new SpinnerNumberModel(initValue, min, max,
				step);
		JSpinner monSpin = new JSpinner(model);
		JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor) monSpin
				.getEditor()).getTextField();
		int top = 0;
		int left = 2;
		int bottom = 0;
		int right = 2;
		Insets insets = new Insets(top, left, bottom, right);
		tf.setMargin(insets);
		c.gridx = 4;
		c.gridy = 4;
		monSpin.setFont(baseFont);
		monSpin.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				timeoutMon = ((Number) model.getValue()).intValue();
			}

		});
		cmonP.add(monSpin, c);
		JLabel pingLab = new JLabel("Number of Retries ");
		c.gridx = 0;
		c.gridy = 6;
		pingLab.setFont(baseFont);
		cmonP.add(pingLab, c);
		min = 1;
		max = 3;
		step = 2;
		initValue = monRetries;
		final SpinnerModel pingmodel = new SpinnerNumberModel(initValue, min,
				max, step);
		JSpinner pingSpin = new JSpinner(pingmodel);
		JFormattedTextField pingtf = ((javax.swing.JSpinner.DefaultEditor) pingSpin
				.getEditor()).getTextField();
		pingtf.setMargin(insets);
		c.gridx = 4;
		c.gridy = 6;
		pingSpin.setFont(baseFont);
		pingSpin.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				monRetries = ((Number) pingmodel.getValue()).intValue();
			}

		});
		cmonP.add(pingSpin, c);
		JLabel targetLab = new JLabel("Reply Timeout (milliseconds) ");
		c.gridx = 0;
		c.gridy = 8;
		targetLab.setFont(baseFont);
		cmonP.add(targetLab, c);
		min = 1;
		max = 2000;
		step = 1;
		initValue = replyTime;
		final SpinnerModel tmreplymodel = new SpinnerNumberModel(initValue,
				min, max, step);
		JSpinner tmreplySpin = new JSpinner(tmreplymodel);
		JFormattedTextField replytf = ((javax.swing.JSpinner.DefaultEditor) tmreplySpin
				.getEditor()).getTextField();
		replytf.setMargin(insets);
		c.gridx = 4;
		c.gridy = 8;
		tmreplySpin.setFont(baseFont);
		tmreplySpin.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				replyTime = ((Number) tmreplymodel.getValue()).intValue();
			}

		});
		cmonP.add(tmreplySpin, c);
		JLabel servLab = new JLabel("Services Monitoring Period (seconds) ");
		c.gridx = 0;
		c.gridy = 10;
		servLab.setFont(baseFont);
		cmonP.add(servLab, c);
		min = 5;
		max = 2000;
		step = 1;
		long initValueServ = timeoutMonServices;
		final SpinnerModel servmodel = new SpinnerNumberModel(initValueServ,
				min, max, step);
		JSpinner servSpin = new JSpinner(servmodel);
		JFormattedTextField servtf = ((javax.swing.JSpinner.DefaultEditor) servSpin
				.getEditor()).getTextField();
		servtf.setMargin(insets);
		c.gridx = 4;
		c.gridy = 10;
		servSpin.setFont(baseFont);
		servSpin.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				timeoutMonServices = ((Number) servmodel.getValue()).intValue();
			}

		});
		cmonP.add(servSpin, c);
		JPanel mainmonP = new JPanel();
		mainmonP.setLayout(new BorderLayout());
		mainmonP.add(customscriptsP, "North");
		mainmonP.add(cmonP, "Center");
		// /////////////////////////
		JPanel alertP = new JPanel();
		alertP.setBorder(new EtchedBorder(1));
		alertP.setLayout(new GridBagLayout());
		JLabel alertLab = new JLabel("Map Alerts ");
		c.gridx = 0;
		c.gridy = 2;
		alertLab.setFont(baseFont);
		alertP.add(alertLab, c);
		final JCheckBox popupBox = new JCheckBox("Show Message");
		popupBox.setFont(baseFont);
		if (alerts)
			popupBox.setSelected(true);
		else
			popupBox.setSelected(false);
		c.gridx = 4;
		c.gridy = 2;
		alertP.add(popupBox, c);
		final JCheckBox emailBox = new JCheckBox("Send Email");
		emailBox.setFont(baseFont);
		if (email)
			emailBox.setSelected(true);
		else
			emailBox.setSelected(false);
		c.gridx = 4;
		c.gridy = 4;
		alertP.add(emailBox, c);
		final JCheckBox cmdBox = new JCheckBox("Run Command");
		cmdBox.setFont(baseFont);
		if (alertcmd)
			cmdBox.setSelected(true);
		else
			cmdBox.setSelected(false);
		c.gridx = 4;
		c.gridy = 6;
		alertP.add(cmdBox, c);
		final JLabel usecomLab = new JLabel("Use %IP in command args");
		c.gridx = 0;
		c.gridy = 7;
		usecomLab.setFont(smallFont);
		alertP.add(usecomLab, c);
		final JLabel comLab = new JLabel("External Command ");
		c.gridx = 0;
		c.gridy = 8;
		comLab.setFont(baseFont);
		alertP.add(comLab, c);
		final JTextField cmdtf = new FixedLengthTextField(40);
		c.gridx = 4;
		c.gridy = 8;
		cmdtf.setFont(baseFont);
		if (alertcmd)
			cmdtf.setText(alertCommand);
		alertP.add(cmdtf, c);
		final JLabel mailLab = new JLabel("Email Address ");
		c.gridx = 0;
		c.gridy = 10;
		mailLab.setFont(baseFont);
		alertP.add(mailLab, c);
		final JTextField mailtf = new FixedLengthTextField(40);
		c.gridx = 4;
		c.gridy = 10;
		mailtf.setFont(baseFont);
		if (email)
			mailtf.setText(alertAddress);
		alertP.add(mailtf, c);
		final JLabel smtpLab = new JLabel("SMTP ");
		c.gridx = 0;
		c.gridy = 12;
		smtpLab.setFont(baseFont);
		alertP.add(smtpLab, c);
		final JTextField smtptf = new FixedLengthTextField(40);
		c.gridx = 4;
		c.gridy = 12;
		smtptf.setFont(baseFont);
		if (email)
			smtptf.setText(smtpAddress);
		alertP.add(smtptf, c);
		final JLabel sendLab = new JLabel("Send Alert as ");
		c.gridx = 0;
		c.gridy = 14;
		sendLab.setFont(baseFont);
		alertP.add(sendLab, c);
		final JRadioButton htmlBtn = new JRadioButton("HTML Page");
		c.gridx = 4;
		c.gridy = 14;
		htmlBtn.setFont(baseFont);
		alertP.add(htmlBtn, c);
		final JRadioButton textBtn = new JRadioButton("Plain Text");
		c.gridx = 4;
		c.gridy = 16;
		textBtn.setFont(baseFont);
		alertP.add(textBtn, c);
		if (htmlAlert) {
			htmlBtn.setSelected(true);
			textBtn.setSelected(false);
		} else {
			htmlBtn.setSelected(false);
			textBtn.setSelected(true);
		}
		htmlBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				textBtn.setSelected(false);
				htmlAlert = true;
			}

		});
		textBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				htmlBtn.setSelected(false);
				htmlAlert = false;
			}

		});
		if (!email) {
			mailtf.setEnabled(false);
			smtptf.setEnabled(false);
			mailLab.setEnabled(false);
			smtpLab.setEnabled(false);
			mailtf.setBackground(Color.lightGray);
			smtptf.setBackground(Color.lightGray);
			sendLab.setEnabled(false);
			htmlBtn.setEnabled(false);
			textBtn.setEnabled(false);
		} else {
			mailLab.setEnabled(true);
			smtpLab.setEnabled(true);
			mailtf.setEnabled(true);
			smtptf.setEnabled(true);
			mailtf.setBackground(Color.white);
			smtptf.setBackground(Color.white);
			sendLab.setEnabled(true);
			htmlBtn.setEnabled(true);
			textBtn.setEnabled(true);
		}
		emailBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (emailBox.isSelected()) {
					mailtf.setEnabled(true);
					smtptf.setEnabled(true);
					mailLab.setEnabled(true);
					smtpLab.setEnabled(true);
					mailtf.setBackground(Color.white);
					smtptf.setBackground(Color.white);
					sendLab.setEnabled(true);
					htmlBtn.setEnabled(true);
					textBtn.setEnabled(true);
				} else {
					email = false;
					mailtf.setEnabled(false);
					smtptf.setEnabled(false);
					mailLab.setEnabled(false);
					smtpLab.setEnabled(false);
					mailtf.setBackground(Color.lightGray);
					smtptf.setBackground(Color.lightGray);
					sendLab.setEnabled(false);
					htmlBtn.setEnabled(false);
					textBtn.setEnabled(false);
				}
			}

		});
		if (!alertcmd) {
			comLab.setEnabled(false);
			cmdtf.setEnabled(false);
			cmdtf.setBackground(Color.lightGray);
			usecomLab.setEnabled(false);
		} else {
			comLab.setEnabled(true);
			usecomLab.setEnabled(true);
			cmdtf.setEnabled(true);
			cmdtf.setBackground(Color.white);
		}
		cmdBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (cmdBox.isSelected()) {
					cmdtf.setEnabled(true);
					usecomLab.setEnabled(true);
					comLab.setEnabled(true);
					cmdtf.setBackground(Color.white);
				} else {
					alertcmd = false;
					cmdtf.setEnabled(false);
					usecomLab.setEnabled(false);
					comLab.setEnabled(false);
					cmdtf.setBackground(Color.lightGray);
				}
			}

		});
		if (!customPing) {
			cpingBox.setSelected(false);
			cpingtf.setEnabled(false);
			cpingtf.setBackground(Color.lightGray);
			cpingtf.setText("");

		} else {
			cpingBox.setSelected(true);
			cpingtf.setEnabled(true);
			cpingtf.setBackground(Color.white);
			cpingtf.setText(cpingCommand);

		}
		if (!customMac) {
			cmacBox.setSelected(false);
			cmactf.setEnabled(false);
			cmactf.setBackground(Color.lightGray);
			cmactf.setText("");

		} else {
			cmacBox.setSelected(true);
			cmactf.setEnabled(true);
			cmactf.setBackground(Color.white);
			cmactf.setText(cmacCommand);
		}
	
		cpingBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (cpingBox.isSelected()) {
					customPing = true;
					cpingtf.setEnabled(true);
					cpingtf.setBackground(Color.white);
					cpingtf.setText("scripts/fping.py");
					cpingCommand = cpingtf.getText().trim();
				} else {
					customPing = false;
					cpingtf.setEnabled(false);
					cpingtf.setBackground(Color.lightGray);
					cpingtf.setText("");

				}
			}

		});
		cmacBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (cmacBox.isSelected()) {
					customMac = true;
					cmactf.setEnabled(true);
					cmactf.setBackground(Color.white);
					cmactf.setText("scripts/getmac.py");
				} else {
					customMac = false;
					cmactf.setEnabled(false);
					cmactf.setBackground(Color.lightGray);
					cmactf.setText("");
				}
			}

		});

		JPanel cmdContP = new JPanel();
		cmdContP.setLayout(new BorderLayout());
		extcmdP = new JPanel();
		extcmdP.setLayout(new GridBagLayout());
		JLabel cmdLab = new JLabel("External Commands ");
		c.gridx = 0;
		c.gridy = 2;
		cmdLab.setFont(baseFont);
		extcmdP.add(cmdLab, c);
		cmdmodel = new DefaultListModel();
		cmdList = new JList(cmdmodel);
		cmdList.setFont(baseFont);
		for (int i = 0; i < extCommands.size(); i++) {
			ExtCmd cmd = (ExtCmd) extCommands.get(i);
			cmdmodel.addElement(cmd.getCmdName());
		}

		JScrollPane cmdscroll = new JScrollPane(cmdList);
		c.gridx = 0;
		c.gridy = 4;
		cmdscroll.setPreferredSize(new Dimension(250, 100));
		cmdscroll.setMinimumSize(new Dimension(250, 100));
		extcmdP.add(cmdscroll, c);
		JPanel cmdbutP = new JPanel();
		cmdbutP.setLayout(new FlowLayout(1));
		JButton addcmdBut = new JButton("Add");
		addcmdBut.setBackground(sysBackColor);
		imageURL = nnm.NetworkManagerGUI.class.getResource("icons/add.gif");
		ImageIcon cmdicon = new ImageIcon(imageURL);
		addcmdBut.setIcon(cmdicon);
		addcmdBut.setVerticalTextPosition(0);
		addcmdBut.setHorizontalTextPosition(4);
		final JButton editcmdBut = new JButton("Edit");
		imageURL = nnm.NetworkManagerGUI.class.getResource("icons/edit.gif");
		cmdicon = new ImageIcon(imageURL);
		editcmdBut.setIcon(cmdicon);
		editcmdBut.setBackground(sysBackColor);
		editcmdBut.setVerticalTextPosition(0);
		editcmdBut.setHorizontalTextPosition(4);
		final JButton delcmdBut = new JButton("Delete");
		imageURL = nnm.NetworkManagerGUI.class.getResource("icons/del.gif");
		cmdicon = new ImageIcon(imageURL);
		delcmdBut.setIcon(cmdicon);
		delcmdBut.setBackground(sysBackColor);
		delcmdBut.setVerticalTextPosition(0);
		delcmdBut.setHorizontalTextPosition(4);
		addcmdBut.setFont(baseFont);
		cmdbutP.add(addcmdBut);
		addcmdBut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ExtCmdDialog(0, null);
			}

		});
		editcmdBut.setFont(baseFont);
		cmdbutP.add(editcmdBut);
		if (extCommands.size() == 0)
			editcmdBut.setEnabled(false);
		editcmdBut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int index = cmdList.getSelectedIndex();
				if (index != -1) {
					String cmdname = String.valueOf(cmdList.getSelectedValue());
					ExtCmdDialog(1, cmdname);
				}
			}

		});
		delcmdBut.setFont(baseFont);
		cmdbutP.add(delcmdBut);
		if (extCommands.size() == 0)
			delcmdBut.setEnabled(false);
		delcmdBut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int index = cmdList.getSelectedIndex();
				String cmdname = String.valueOf(cmdList.getSelectedValue());
				ExtCmd command = null;
				if (index != -1) {
					for (int i = 0; i < extCommands.size(); i++) {
						ExtCmd cmdt = (ExtCmd) extCommands.get(i);
						if (cmdt.getCmdName().equals(cmdname))
							command = cmdt;
					}

					cmdmodel.remove(index);
					for (int i = 0; i < NetworkManager.menuExtCmd
							.getItemCount(); i++) {
						JMenuItem item = NetworkManager.menuExtCmd.getItem(i);
						if (item.getText().equals(command.getCmdName()))
							NetworkManager.menuExtCmd.remove(item);
					}

					extCommands.remove(command);
				}
			}

		});
		cmdmodel.addListDataListener(new ListDataListener() {

			public void contentsChanged(ListDataEvent lde) {
				if (cmdmodel.size() != 0) {
					editcmdBut.setEnabled(true);
					delcmdBut.setEnabled(true);
				} else {
					editcmdBut.setEnabled(false);
					delcmdBut.setEnabled(false);
				}
				extcmdP.repaint();
			}

			public void intervalAdded(ListDataEvent arg0) {
				if (cmdmodel.size() != 0) {
					editcmdBut.setEnabled(true);
					delcmdBut.setEnabled(true);
					extcmdP.repaint();
				}
			}

			public void intervalRemoved(ListDataEvent arg0) {
				if (cmdmodel.size() == 0) {
					editcmdBut.setEnabled(false);
					delcmdBut.setEnabled(false);
					extcmdP.repaint();
				}
			}

		});
		cmdContP.add(extcmdP, "Center");
		cmdContP.add(cmdbutP, "South");
		JPanel snmpConP = new JPanel();
		snmpConP.setLayout(new BorderLayout());
		JPanel snmpP = new JPanel();
		snmpP.setPreferredSize(new Dimension(snmpP.getWidth(), 120));
		snmpP.setMinimumSize(new Dimension(snmpP.getWidth(), 120));
		snmpP.setBorder(new EtchedBorder(1));
		snmpP.setLayout(new GridBagLayout());
		JLabel commLab = new JLabel("Wizard Com");
		c.gridx = 0;
		c.gridy = 0;
		commLab.setFont(baseFont);
		snmpP.add(commLab, c);
		final JTextField commtf = new FixedLengthTextField(25);
		c.gridx = 4;
		c.gridy = 0;
		commtf.setText(wizCommunity);
		commtf.setFont(baseFont);
		snmpP.add(commtf, c);
		JLabel tcommLab = new JLabel("Trap Com");
		c.gridx = 0;
		c.gridy = 2;
		tcommLab.setFont(baseFont);
		snmpP.add(tcommLab, c);
		final JTextField tcommtf = new FixedLengthTextField(25);
		c.gridx = 4;
		c.gridy = 2;
		tcommtf.setText(trapCommunity);
		tcommtf.setFont(baseFont);
		snmpP.add(tcommtf, c);
//////////////
		JPanel snmpmonP = new JPanel();
		snmpmonP.setLayout(new GridBagLayout());
		
		JLabel snmpmonLab = new JLabel("Monitoring Period (seconds) ");
		c.gridx = 0;
		c.gridy = 0;
		snmpmonLab.setFont(baseFont);
		snmpmonP.add(snmpmonLab, c);
		min = 5;
		max = 2000;
		step = 1;
		long initValueSNMP = timeoutMonSNMP;
		final SpinnerModel snmpmodel = new SpinnerNumberModel(initValueSNMP,
				min, max, step);
		JSpinner snmpSpin = new JSpinner(snmpmodel);
		JFormattedTextField snmptf = ((javax.swing.JSpinner.DefaultEditor) snmpSpin
				.getEditor()).getTextField();
		snmptf.setMargin(insets);
		c.gridx = 4;
		c.gridy = 0;
		snmpSpin.setFont(baseFont);
		snmpSpin.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				timeoutMonSNMP = ((Number) snmpmodel.getValue()).intValue();
			}

		});
		snmpmonP.add(snmpSpin, c);
///////// snmp custom cmd
		
		final JCheckBox snmpcmdBox = new JCheckBox("Resulting Script");
		snmpcmdBox.setFont(baseFont);
		if (customSnmp)
			snmpcmdBox.setSelected(true);
		else
			snmpcmdBox.setSelected(false);
		c.gridx = 0;
		c.gridy = 2;	
		snmpmonP.add(snmpcmdBox, c);
		final JTextField snmpcmdtf = new FixedLengthTextField(25);
		c.gridx = 0;
		c.gridy = 4;
		snmpcmdtf.setText(snmpScript);
		snmpcmdtf.setFont(baseFont);
		snmpmonP.add(snmpcmdtf, c);
		/////////////
		snmpConP.add(snmpP, "North");
		snmpConP.add(snmpmonP, "Center");
		
		if (!customSnmp) {
			snmpcmdBox.setSelected(false);
			snmpcmdtf.setEnabled(false);
			snmpcmdtf.setBackground(Color.lightGray);
			snmpcmdtf.setText("");

		} else {
			snmpcmdBox.setSelected(true);
			snmpcmdtf.setEnabled(true);
			snmpcmdtf.setBackground(Color.white);
			snmpcmdtf.setText(snmpScript);
		}
		snmpcmdBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (snmpcmdBox.isSelected()) {
					customSnmp = true;
					snmpcmdtf.setEnabled(true);
					snmpcmdtf.setBackground(Color.white);
					snmpcmdtf.setText("scripts/getsnmp.py");
					snmpScript = cpingtf.getText().trim();
				} else {
					customSnmp = false;
					snmpcmdtf.setEnabled(false);
					snmpcmdtf.setBackground(Color.lightGray);
					snmpcmdtf.setText("");

				}
			}

		});
		///////////////////////////////////
		JPanel colorP = new JPanel();
		colorP.setBorder(new EtchedBorder(1));
		colorP.setLayout(new GridBagLayout());
		JLabel backLab = new JLabel("Map Background Color  ");
		c.gridx = 0;
		c.gridy = 2;
		backLab.setFont(baseFont);
		colorP.add(backLab, c);
		colorP.add(Box.createHorizontalStrut(20));
		final JButton backColorBut = new JButton(" ");
		backColorBut.setBackground(backgroundColor);
		c.gridx = 6;
		c.gridy = 2;
		backColorBut.setFont(baseFont);
		colorP.add(backColorBut, c);
		backColorBut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JColorChooser colorChooser = new JColorChooser(backgroundColor);
				JDialog Coldialog = JColorChooser.createDialog(null,
						"Select a Background Color", true, colorChooser, null,
						null);
				recursivelySetFonts(Coldialog, baseFont);
				Coldialog.setResizable(false);
				Coldialog.setVisible(true);
				Color c = colorChooser.getColor();
				if (c != null) {
					backgroundColor = c;
					manager.setBackground(backgroundColor);
					backColorBut.setBackground(backgroundColor);
					dialog.repaint();
				}
			}

		});
		JLabel lineLab = new JLabel("Map Connectors Color  ");
		c.gridx = 0;
		c.gridy = 4;
		lineLab.setFont(baseFont);
		colorP.add(lineLab, c);
		colorP.add(Box.createHorizontalStrut(20));
		final JButton lineColorBut = new JButton(" ");
		lineColorBut.setBackground(lineColor);
		c.gridx = 6;
		c.gridy = 4;
		lineColorBut.setFont(baseFont);
		colorP.add(lineColorBut, c);
		lineColorBut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JColorChooser colorChooser = new JColorChooser(lineColor);
				JDialog Coldialog = JColorChooser.createDialog(null,
						"Select a Connectors Color", true, colorChooser, null,
						null);
				recursivelySetFonts(Coldialog, baseFont);
				Coldialog.setResizable(false);
				Coldialog.setVisible(true);
				Color c = colorChooser.getColor();
				if (c != null) {
					lineColor = c;
					manager.repaint();
					lineColorBut.setBackground(lineColor);
					dialog.repaint();
				}
			}

		});
		JLabel textLab = new JLabel("Text Color  ");
		c.gridx = 0;
		c.gridy = 8;
		textLab.setFont(baseFont);
		colorP.add(textLab, c);
		colorP.add(Box.createHorizontalStrut(20));
		final JButton textColorBut = new JButton(" ");
		textColorBut.setBackground(textColor);
		c.gridx = 6;
		c.gridy = 8;
		textColorBut.setFont(baseFont);
		colorP.add(textColorBut, c);
		textColorBut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JColorChooser colorChooser = new JColorChooser(textColor);
				JDialog Coldialog = JColorChooser.createDialog(null,
						"Select a Text Color", true, colorChooser, null, null);
				recursivelySetFonts(Coldialog, baseFont);
				Coldialog.setResizable(false);
				Coldialog.setVisible(true);
				Color c = colorChooser.getColor();
				if (c != null) {
					textColor = c;
					manager.repaint();
					textColorBut.setBackground(textColor);
					dialog.repaint();
				}
			}

		});
		JLabel selLab = new JLabel("Selected Color  ");
		c.gridx = 0;
		c.gridy = 12;
		selLab.setFont(baseFont);
		colorP.add(selLab, c);
		colorP.add(Box.createHorizontalStrut(20));
		final JButton selColorBut = new JButton(" ");
		selColorBut.setBackground(selColor);
		c.gridx = 6;
		c.gridy = 12;
		selColorBut.setFont(baseFont);
		colorP.add(selColorBut, c);
		selColorBut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JColorChooser colorChooser = new JColorChooser(selColor);
				JDialog Coldialog = JColorChooser.createDialog(null,
						"Select a Selected Color", true, colorChooser, null,
						null);
				recursivelySetFonts(Coldialog, baseFont);
				Coldialog.setResizable(false);
				Coldialog.setVisible(true);
				
				Color c = colorChooser.getColor();
				if (c != null) {
					selColor = c;
					manager.repaint();
					selColorBut.setBackground(selColor);
					dialog.repaint();
				}
			}

		});
		JLabel defLab = new JLabel("Set Defaults ");
		c.gridx = 0;
		c.gridy = 14;
		defLab.setFont(baseFont);
		colorP.add(defLab, c);
		colorP.add(Box.createHorizontalStrut(20));
		JButton defColorBut = new JButton("Set Color");
		defColorBut.setBackground(sysBackColor);
		c.gridx = 6;
		c.gridy = 14;
		defColorBut.setFont(baseFont);
		colorP.add(defColorBut, c);
		defColorBut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				backgroundColor = new Color(235,235,235);
				textColor = Color.black;
				lineColor = new Color(0,102,153);
				selColor = Color.orange;
				backColorBut.setBackground(backgroundColor);
				textColorBut.setBackground(textColor);
				lineColorBut.setBackground(lineColor);
				selColorBut.setBackground(selColor);
				dialog.repaint();
				manager.setBackground(backgroundColor);
				manager.repaint();
			}

		});
		JPanel imgP = new JPanel();
		imgP.setBorder(new EtchedBorder(1));
		imgP.setLayout(new GridBagLayout());
		final JTextField imgtf = new JTextField();
		imgtf.setPreferredSize(new Dimension(150, 20));
		imgtf.setMaximumSize(new Dimension(150, 20));
		c.gridx = 0;
		c.gridy = 4;
		imgtf.setFont(baseFont);
		if (Graph.backImage != null)
			imgtf.setText(Graph.backImage);
		imgP.add(imgtf, c);
		final JButton selBut = new JButton("Select");
		selBut.setBackground(sysBackColor);
		imageURL = nnm.NetworkManagerGUI.class
				.getResource("icons/pictures.gif");
		ImageIcon bimgicon = new ImageIcon(imageURL);
		selBut.setIcon(bimgicon);
		selBut.setVerticalTextPosition(0);
		selBut.setHorizontalTextPosition(4);
		selBut.setFont(baseFont);
		c.gridx = 0;
		c.gridy = 6;
		imgP.add(selBut, c);
		final JCheckBox imgBox = new JCheckBox("Use Background Image");
		imgBox.setFont(baseFont);
		if (textHasContent(Graph.backImage) && !Graph.backImage.equals("null")) {
			imgBox.setSelected(true);
			imgtf.setEditable(true);
			imgtf.setBackground(Color.white);
			selBut.setEnabled(true);
		} else {
			imgBox.setSelected(false);
			imgtf.setEditable(false);
			imgtf.setBackground(Color.lightGray);
			selBut.setEnabled(false);
		}
		c.gridx = 0;
		c.gridy = 2;
		imgP.add(imgBox, c);
		imgBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (imgBox.isSelected()) {
					usbImage = true;
					imgtf.setEditable(true);
					imgtf.setBackground(Color.white);
					selBut.setEnabled(true);
				} else {
					usbImage = false;
					imgtf.setEditable(false);
					imgtf.setBackground(Color.lightGray);
					selBut.setEnabled(false);
					Graph.backImage = null;
				}
			}

		});
		selBut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				recursivelySetFonts(fc, baseFont);
				fc.addChoosableFileFilter(new ImageFilter());
				fc.setAcceptAllFileFilterUsed(false);
				fc.setCurrentDirectory(new File("."));
				if (textHasContent(Graph.backImage)
						&& !Graph.backImage.equals("null"))
					fc.setCurrentDirectory(new File(Graph.backImage));
				int retval = fc.showOpenDialog(null);
				if (retval == 0) {
					backFile = fc.getSelectedFile();
					Graph.backImage = backFile.getAbsolutePath();
					imgtf.setText(backFile.getAbsolutePath());
					manager.repaint();
					dialog.toFront();
				}
			}

		});
/////////////////// types panel
		JPanel tPanel = new JPanel();
		tPanel.setLayout(new BorderLayout());
		nodeTypesP = new JPanel();
		nodeTypesP.setLayout(new GridBagLayout());
		JLabel tLab = new JLabel("Node Types ");
		c.gridx = 0;
		c.gridy = 2;
		tLab.setFont(baseFont);
		nodeTypesP.add(tLab, c);
		nodeTypesModel = new DefaultListModel();
		nodeTypesList = new JList(nodeTypesModel);
		nodeTypesList.setFont(baseFont);
		for (int i = 0; i < nodeTypes.size(); i++) {
			String type = (String) nodeTypes.get(i);
			nodeTypesModel.addElement(type);
		}

		JScrollPane typescroll = new JScrollPane(nodeTypesList);
		c.gridx = 0;
		c.gridy = 4;
		typescroll.setPreferredSize(new Dimension(250, 100));
		typescroll.setMinimumSize(new Dimension(250, 100));
		nodeTypesP.add(typescroll, c);
		JPanel tbutP = new JPanel();
		tbutP.setLayout(new FlowLayout(1));
		JButton addTypeBut = new JButton("Add");
		addTypeBut.setBackground(sysBackColor);
		imageURL = nnm.NetworkManagerGUI.class.getResource("icons/add.gif");
		ImageIcon ticon = new ImageIcon(imageURL);
		addTypeBut.setIcon(ticon);
		addTypeBut.setVerticalTextPosition(0);
		addTypeBut.setHorizontalTextPosition(4);
		
		final JButton delTypeBut = new JButton("Delete");
		imageURL = nnm.NetworkManagerGUI.class.getResource("icons/del.gif");
		ticon = new ImageIcon(imageURL);
		delTypeBut.setIcon(ticon);
		delTypeBut.setBackground(sysBackColor);
		delTypeBut.setVerticalTextPosition(0);
		delTypeBut.setHorizontalTextPosition(4);
		addTypeBut.setFont(baseFont);
		tbutP.add(addTypeBut);
		addTypeBut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				InputDialog dlg  = new InputDialog(dialog, "Enter new type", "Node Type", 25);
				String newtype = dlg.getAction();
				if (textHasContent(newtype)) {
					boolean yes = false;
					for (int i = 0; i < nodeTypes.size(); i++) {
						String type = (String) nodeTypes.get(i);
						if (newtype.equals(type)) {
							new MessageDialog(dialog,
									"Duplicate node type",
									"Node Type");
							yes=true;
							break;
						}
						
					}
				if (!yes)
				{
					nodeTypes.add(newtype);
					nodeTypesModel.addElement(newtype);
				}
				}
			}

		});
		
		delTypeBut.setFont(baseFont);
		tbutP.add(delTypeBut);
		if (nodeTypes.size() == 0)
			delTypeBut.setEnabled(false);
		delTypeBut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int index = nodeTypesList.getSelectedIndex();
				String type = String.valueOf(nodeTypesList.getSelectedValue());
			/*	typeBox.addItem("3com");
				typeBox.addItem("access-point");
				typeBox.addItem("ats");
				typeBox.addItem("bridge");
				typeBox.addItem("catalyst");
				typeBox.addItem("cisco");
				typeBox.addItem("d-link");
				typeBox.addItem("firewall");
				typeBox.addItem("freebsd-server");
				typeBox.addItem("hp-server");
				typeBox.addItem("hub");
				typeBox.addItem("ibm");
				typeBox.addItem("juniper");
				typeBox.addItem("linux-server");
				typeBox.addItem("linux-workstation");
				typeBox.addItem("lucent");
				typeBox.addItem("macosx");
				typeBox.addItem("mail-server");
				typeBox.addItem("mainframe");
				typeBox.addItem("managable-hub");
				typeBox.addItem("modem");
				typeBox.addItem("netbsd-server");
				typeBox.addItem("network-cloud");
				typeBox.addItem("network-printer");
				typeBox.addItem("network-printserver");
				typeBox.addItem("notebook");
				typeBox.addItem("novell-server");
				typeBox.addItem("olencom");
				typeBox.addItem("openbsd-server");
				typeBox.addItem("openvms-server");
				typeBox.addItem("pix");
				typeBox.addItem("redhat-server");
				typeBox.addItem("router");
				typeBox.addItem("server");
				typeBox.addItem("sql-server");
				typeBox.addItem("sun-server");
				typeBox.addItem("sun-workstation");
				typeBox.addItem("sun");
				typeBox.addItem("suse-server");
				typeBox.addItem("switch");
				typeBox.addItem("terminal");
				typeBox.addItem("webcam");
				typeBox.addItem("web-server");
				typeBox.addItem("wi-fi");
				typeBox.addItem("windows-server");
				typeBox.addItem("windows-workstation");
				typeBox.addItem("workstation");
				typeBox.addItem("unix");
				typeBox.addItem("ups");*/
				if (type.equals("workstation") || type.equals("hub") || type.equals("network-cloud")
						|| type.equals("server") || type.equals("router") || type.equals("switch"))
				{
					new MessageDialog(dialog,
						"It's a basic type, don't remove it!",
				"Node Type");
						
				} else {
				nodeTypesModel.remove(index);
				nodeTypes.remove(type);
			}
				}
		});
		nodeTypesModel.addListDataListener(new ListDataListener() {
			public void contentsChanged(ListDataEvent lde) {
				if (nodeTypesModel.size() != 0) {
					delTypeBut.setEnabled(true);
				} else {
					delTypeBut.setEnabled(false);
				}
				nodeTypesP.repaint();
			}

			public void intervalAdded(ListDataEvent arg0) {
				if (nodeTypesModel.size() != 0) {
					delTypeBut.setEnabled(true);
					nodeTypesP.repaint();
				}
			}

			public void intervalRemoved(ListDataEvent arg0) {
				if (nodeTypesModel.size() == 0) {
					delTypeBut.setEnabled(false);
					nodeTypesP.repaint();
				}
			}

		});
		tPanel.add(nodeTypesP, "Center");
		tPanel.add(tbutP, "South");
		////////////////////////////////////////////////////////////////////////////////
		tabs.setFont(baseFont);
		tabs.setBackground(Color.lightGray);
		tabs.addTab("Monitoring", null, mainmonP);
		tabs.addTab("Alerts", null, alertP);
		tabs.addTab("SNMP", null, snmpConP);
		tabs.addTab("Types", null, tPanel);
		tabs.addTab("Colors", null, colorP);
		tabs.addTab("Background", null, imgP);
		tabs.addTab("Commands", null, cmdContP);
		// tabs.addTab("DB", null, dbPanel);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(2));
		JButton ok = new JButton("OK");
		ok.setBackground(sysBackColor);
		ok.setFont(baseFont);
		ok.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				cpingCommand = cpingtf.getText().trim();
				cmacCommand = cmactf.getText().trim();
				snmpScript = snmpcmdtf.getText().trim();
				if (!textHasContent(cpingCommand)) {
					customPing = false;
					cpingBox.setSelected(false);
				}
				if (!textHasContent(cmacCommand)) {
					customMac = false;
					cmacBox.setSelected(false);
				}
				if (!textHasContent(snmpScript)) {
					customSnmp = false;
					snmpcmdBox.setSelected(false);
				}
				wizCommunity = commtf.getText();
				if (!textHasContent(wizCommunity))
					wizCommunity = "public";
				trapCommunity = tcommtf.getText();
				if (!textHasContent(trapCommunity))
					trapCommunity = "public";
				
				alertCommand = cmdtf.getText().trim();

				if (emailBox.isSelected()) {
					email = emailBox.isSelected();
					alertAddress = mailtf.getText().trim();
					smtpAddress = smtptf.getText().trim();
					if (!ValidEmail.isValidEmail(alertAddress))
						new MessageDialog(dialog,
								"Enter valid email address, please",
								"Alerts Email");
					else if (!textHasContent(smtpAddress)) {
						new MessageDialog(dialog,
								"Enter SMTP server address, please",
								"Alerts SMTP Server");
					} else {
						timeoutMon = ((Number) model.getValue()).intValue();
						monRetries = ((Number) pingmodel.getValue()).intValue();
						replyTime = ((Number) tmreplymodel.getValue())
								.intValue();
						alerts = popupBox.isSelected();
						dialog.setVisible(false);
						dialog.dispose();
					}
				} else {
					timeoutMon = ((Number) model.getValue()).intValue();
					monRetries = ((Number) pingmodel.getValue()).intValue();
					replyTime = ((Number) tmreplymodel.getValue()).intValue();
					alerts = popupBox.isSelected();
					alertcmd = cmdBox.isSelected();
					dialog.setVisible(false);
					dialog.dispose();
				}
				for (int i = 0; i < Graph.nodes.size(); i++) {
					Node aNode = (Node) Graph.nodes.get(i);
					if (aNode.getDnslabel() == 1 || aNode.getDnslabel() == 2
							|| aNode.getDnslabel() == 3)
						new setBindName(1, aNode, true);
				}

				manager.repaint();

			}

		});
		buttonPanel.add(ok);
		JButton cancel = new JButton("Cancel");
		cancel.setBackground(sysBackColor);
		buttonPanel.add(cancel);
		cancel.setFont(baseFont);
		cancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
				dialog.dispose();
			}

		});
		container.add(tabs, "Center");
		container.add(buttonPanel, "South");
		dialog.getContentPane().add(container);
		dialog.setVisible(true);
	}

	/*
	 * public static void DBconnect() { final JDialog dialog = new JDialog();
	 * dialog.setTitle("Export to/Import from DB"); dialog.setSize(260, 260);
	 * dialog.setResizable(false); Dimension screenSize =
	 * Toolkit.getDefaultToolkit().getScreenSize(); Dimension optSize =
	 * dialog.getSize(); if (optSize.height > screenSize.height) optSize.height =
	 * screenSize.height; if (optSize.width > screenSize.width) optSize.width =
	 * screenSize.width; dialog.setLocation((screenSize.width - optSize.width) /
	 * 2, (screenSize.height - optSize.height) / 2);
	 * dialog.setDefaultCloseOperation(1); JPanel container = new JPanel();
	 * container.setLayout(new BorderLayout()); JPanel dbPanel = new JPanel();
	 * dbPanel.setBorder(new EtchedBorder(1)); dbPanel.setLayout(new
	 * GridBagLayout()); GridBagConstraints c = new GridBagConstraints();
	 * c.insets = new Insets(1, 1, 1, 1); c.anchor = 17; c.fill = 2; JLabel
	 * drvLab = new JLabel("Jdbc driver "); drvLab.setFont(baseFont); c.gridx =
	 * 0; c.gridy = 0; dbPanel.add(drvLab, c); String driver_entries[] = {
	 * "MySQL", "PostgreSQL", "MaxDB" }; DefaultComboBoxModel drvModel = new
	 * DefaultComboBoxModel(); for (int i = 0; i < driver_entries.length; i++)
	 * drvModel.addElement(driver_entries[i]);
	 * 
	 * final JComboBox drvBox = new JComboBox(drvModel);
	 * drvBox.setFont(baseFont); drvBox.setBackground(sysBackColor);
	 * drvBox.addItemListener(new ItemListener() {
	 * 
	 * public void itemStateChanged(ItemEvent e) { Driver = (String)
	 * e.getItem(); }
	 * 
	 * }); c.gridx = 4; c.gridy = 0; dbPanel.add(drvBox, c); JLabel svLab = new
	 * JLabel("Server Address "); svLab.setFont(baseFont); c.gridx = 0; c.gridy =
	 * 2; dbPanel.add(svLab, c); final JTextField svtf = new
	 * FixedLengthTextField(20); c.gridx = 4; c.gridy = 2; if
	 * (!textHasContent(DBaddress)) svtf.setText("localhost"); else
	 * svtf.setText(DBaddress); svtf.setFont(baseFont); dbPanel.add(svtf, c);
	 * JLabel dbLab = new JLabel("Database Name "); dbLab.setFont(baseFont);
	 * c.gridx = 0; c.gridy = 6; dbPanel.add(dbLab, c); final JTextField dbtf =
	 * new FixedLengthTextField(20); dbtf.setFont(baseFont); c.gridx = 4;
	 * c.gridy = 6; if (textHasContent(DBname)) dbtf.setText(DBname);
	 * dbPanel.add(dbtf, c); JLabel userLab = new JLabel("User ");
	 * userLab.setFont(baseFont); c.gridx = 0; c.gridy = 8; dbPanel.add(userLab,
	 * c); final JTextField usertf = new JTextField(20);
	 * usertf.setFont(baseFont); c.gridx = 4; c.gridy = 8; if
	 * (textHasContent(DBuser)) usertf.setText(DBuser); dbPanel.add(usertf, c);
	 * JLabel passLab = new JLabel("Password "); passLab.setFont(baseFont);
	 * c.gridx = 0; c.gridy = 10; dbPanel.add(passLab, c); final JTextField
	 * passtf = new JPasswordField(20); c.gridx = 4; c.gridy = 10; if
	 * (textHasContent(DBpass)) passtf.setText(DBpass); dbPanel.add(passtf, c);
	 * JLabel mapLab = new JLabel("Map Name "); mapLab.setFont(baseFont);
	 * c.gridx = 0; c.gridy = 12; dbPanel.add(mapLab, c); final JTextField maptf =
	 * new JTextField(20); c.gridx = 4; c.gridy = 12; dbPanel.add(maptf, c);
	 * final JRadioButton expBtn = new JRadioButton(" Export Map ");
	 * expBtn.setFont(baseFont); expBtn.setSelected(true); c.gridx = 0; c.gridy =
	 * 14; dbPanel.add(expBtn, c); final JRadioButton impBtn = new
	 * JRadioButton(" Import Map "); impBtn.setFont(baseFont); c.gridx = 4;
	 * c.gridy = 14; dbPanel.add(impBtn, c); expBtn.addActionListener(new
	 * ActionListener() {
	 * 
	 * public void actionPerformed(ActionEvent e) { impBtn.setSelected(false);
	 * expBtn.setSelected(true); exportDB = true; }
	 * 
	 * }); impBtn.addActionListener(new ActionListener() {
	 * 
	 * public void actionPerformed(ActionEvent e) { impBtn.setSelected(true);
	 * expBtn.setSelected(false); exportDB = false; }
	 * 
	 * }); JPanel buttonPanel = new JPanel(); buttonPanel.setLayout(new
	 * FlowLayout(2)); JButton ok = new JButton("Connect");
	 * ok.setBackground(sysBackColor); ok.setFont(baseFont); JButton cancel =
	 * new JButton("Cancel"); cancel.setBackground(sysBackColor);
	 * buttonPanel.add(ok); buttonPanel.add(cancel); cancel.setFont(baseFont);
	 * ok.addActionListener(new ActionListener() {
	 * 
	 * public void actionPerformed(ActionEvent e) { Driver =
	 * drvBox.getSelectedItem().toString(); if (!textHasContent(Driver)) Driver =
	 * "MySQL"; String server = svtf.getText().trim().replaceAll(" ", "");
	 * String db = dbtf.getText().trim().replaceAll(" ", ""); String user =
	 * usertf.getText().trim().replaceAll(" ", ""); String pass =
	 * passtf.getText().trim().replaceAll(" ", ""); String map =
	 * maptf.getText().trim().replaceAll(" ", ""); if (!textHasContent(server))
	 * new MessageDialog(dialog, "Enter server IP address or DNS name, please",
	 * "Server address"); else if (!textHasContent(server)) new
	 * MessageDialog(dialog, "Enter server IP address or DNS name, please",
	 * "Server address"); else if (!textHasContent(db)) new
	 * MessageDialog(dialog, "Enter database name, please", "DB name"); else if
	 * (!textHasContent(user)) new MessageDialog(dialog, "Enter login name,
	 * please", "User login"); else if (!textHasContent(pass)) new
	 * MessageDialog(dialog, "Enter password, please", "User password"); else if
	 * (!textHasContent(map)) { new MessageDialog(dialog, "Enter map name,
	 * please", "Netwhistler map"); } else { if (!exportDB) status .setText("
	 * Load map from DB ..."); else status .setText(" Saving map to DB ...");
	 * dialog.setVisible(false); dialog.dispose(); if (!exportDB) {
	 * Graph.loadDefaults(); DBmap.openMap(Driver, server, db, user, pass, map); }
	 * else { DBmap.saveMap(Driver, server, db, user, pass, map); }
	 * status.setText(" "); manager.repaint(); } }
	 * 
	 * }); cancel.addActionListener(new ActionListener() {
	 * 
	 * public void actionPerformed(ActionEvent e) { dialog.setVisible(false);
	 * dialog.dispose(); }
	 * 
	 * }); container.add(dbPanel, "Center"); container.add(buttonPanel,
	 * "South"); dialog.getContentPane().add(container);
	 * dialog.setVisible(true); }
	 */
	public static void ExtCmdDialog(int t, String com) {
		final int type = t;
		for (int i = 0; i < extCommands.size(); i++) {
			ExtCmd cmdt = (ExtCmd) extCommands.get(i);
			if (cmdt.getCmdName().equals(com))
				cmd = cmdt;
		}

		final JDialog dialog = new JDialog();
		if (type == 0)
			dialog.setTitle("Add Command");
		else if (type == 1)
			dialog.setTitle("Edit Command");
		dialog.setSize(260, 200);
		dialog.setResizable(false);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension optSize = dialog.getSize();
		if (optSize.height > screenSize.height)
			optSize.height = screenSize.height;
		if (optSize.width > screenSize.width)
			optSize.width = screenSize.width;
		dialog.setLocation((screenSize.width - optSize.width) / 2,
				(screenSize.height - optSize.height) / 2);
		dialog.setDefaultCloseOperation(1);
		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
		JPanel cmdPanel = new JPanel();
		cmdPanel.setBorder(new EtchedBorder(1));
		cmdPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(1, 1, 1, 1);
		c.anchor = 17;
		c.fill = 2;
		JLabel nameLab = new JLabel("Display Name ");
		nameLab.setFont(baseFont);
		c.gridx = 0;
		c.gridy = 0;
		cmdPanel.add(nameLab, c);
		final JTextField nmtf = new FixedLengthTextField(20);
		c.gridx = 0;
		c.gridy = 2;
		nmtf.setFont(baseFont);
		if (type != 0)
			nmtf.setText(cmd.getCmdName());
		cmdPanel.add(nmtf, c);
		JLabel execLab = new JLabel("Command ");
		execLab.setFont(baseFont);
		c.gridx = 0;
		c.gridy = 4;
		cmdPanel.add(execLab, c);
		final JTextField extf = new FixedLengthTextField(20);
		extf.setFont(baseFont);
		c.gridx = 0;
		c.gridy = 6;
		if (type != 0)
			extf.setText(cmd.getCmd());
		cmdPanel.add(extf, c);
		JLabel argLab = new JLabel("Arguments ");
		argLab.setFont(baseFont);
		c.gridx = 0;
		c.gridy = 8;
		cmdPanel.add(argLab, c);
		final JTextField argtf = new JTextField(20);
		argtf.setFont(baseFont);
		c.gridx = 0;
		c.gridy = 10;
		if (type != 0)
			argtf.setText(cmd.getCmdArgs());
		cmdPanel.add(argtf, c);
		JLabel ipLab = new JLabel("Use %IP in command args ");
		ipLab.setFont(smallFont);
		c.gridx = 0;
		c.gridy = 12;
		cmdPanel.add(ipLab, c);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(2));
		JButton ok = new JButton("Ok");
		ok.setBackground(sysBackColor);
		ok.setFont(baseFont);
		JButton cancel = new JButton("Cancel");
		cancel.setBackground(sysBackColor);
		buttonPanel.add(ok);
		buttonPanel.add(cancel);
		cancel.setFont(baseFont);
		ok.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String name = nmtf.getText();
				String command = extf.getText();
				String arg = argtf.getText();
				if (!textHasContent(name))
					new MessageDialog(dialog,
							"Enter Command Display name, please",
							"Display name");
				else if (!textHasContent(command)) {
					new MessageDialog(dialog, "Enter Command, please",
							"External  Command");
				} else {
					if (type == 0) {
						extCommands.add(new ExtCmd(name, command, arg));
						cmdmodel.addElement(name);
						JMenuItem menuItem = NetworkManager.AddCmdItem();
						NetworkManager.menuExtCmd.add(menuItem);
						NetworkManager.menuExtCmd.repaint();
					} else if (type == 1) {
						for (int i = 0; i < NetworkManager.menuExtCmd
								.getItemCount(); i++) {
							JMenuItem item = NetworkManager.menuExtCmd
									.getItem(i);
							if (item.getText().equals(cmd.getCmdName()))
								NetworkManager.menuExtCmd.remove(item);
						}

						extCommands.remove(cmd);
						cmd = new ExtCmd(name, command, arg);
						extCommands.add(cmd);
						JMenuItem menuItem = NetworkManager.AddCmdItem();
						NetworkManager.menuExtCmd.add(menuItem);
						cmdmodel = new DefaultListModel();
						for (int i = 0; i < extCommands.size(); i++) {
							ExtCmd cmd = (ExtCmd) extCommands.get(i);
							cmdmodel.addElement(cmd.getCmdName());
						}

						cmdList.setModel(cmdmodel);
						extcmdP.repaint();
					}
					NetworkManager.menuExtCmd.repaint();
					dialog.setVisible(false);
					dialog.dispose();
				}
			}

		});
		cancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
				dialog.dispose();
			}

		});
		container.add(cmdPanel, "Center");
		container.add(buttonPanel, "South");
		dialog.getContentPane().add(container);
		dialog.setVisible(true);
	}

	public static void aboutDialog() {
		final JDialog dialog = new JDialog();
		dialog.setTitle("About NetWhistler");
		dialog.setSize(340, 180);
		dialog.setResizable(false);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension optSize = dialog.getSize();
		if (optSize.height > screenSize.height)
			optSize.height = screenSize.height;
		if (optSize.width > screenSize.width)
			optSize.width = screenSize.width;
		dialog.setLocation((screenSize.width - optSize.width) / 2,
				(screenSize.height - optSize.height) / 2);
		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
		JPanel abPanel = new JPanel();
		abPanel.setBorder(new EtchedBorder(1));
		abPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(1, 1, 1, 1);
		c.anchor = 17;
		c.fill = 2;
		JLabel text1 = new JLabel();
		text1.setFont(baseFont);
		text1.setText("Mila NetWhistler ");
		c.gridx = 0;
		c.gridy = 2;
		abPanel.add(text1, c);
		JLabel logo = new JLabel();
		imageURL = nnm.NetworkManagerGUI.class.getResource("icons/nw.gif");
		ImageIcon frameIcon = new ImageIcon(imageURL);
		logo.setIcon(frameIcon);
		c.gridx = 4;
		c.gridy = 2;
		abPanel.add(logo, c);
		JLabel text2 = new JLabel();
		text2.setFont(baseFont);
		text2.setText("http://www.netwhistler.sourceforge.net");
		c.gridx = 0;
		c.gridy = 4;
		abPanel.add(text2, c);
		String os = System.getProperty("os.name");
		String versa = System.getProperty("os.version");
		String arch = System.getProperty("os.arch");
		JLabel text3 = new JLabel();
		text3.setFont(baseFont);
		text3.setText("Version " + version + " " + os + " " + versa + " ["
				+ arch + "]");
		c.gridx = 0;
		c.gridy = 6;
		abPanel.add(text3, c);
		JLabel text4 = new JLabel();
		text4.setFont(baseFont);
		text4.setText("Copyright \251 2006  Alexander R. Eremin.");
		c.gridx = 0;
		c.gridy = 8;
		abPanel.add(text4, c);
		JPanel buttonPanel = new JPanel();
		JButton ok = new JButton("Ok");
		ok.setBackground(sysBackColor);
		ok.setFont(baseFont);
		buttonPanel.add(ok);
		ok.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
				dialog.dispose();
			}

		});
		container.add(abPanel, "Center");
		container.add(buttonPanel, "South");
		dialog.getContentPane().add(container);
		dialog.setVisible(true);
	}

	public static boolean textHasContent(String aText) {
		String emptyString = "";
		return aText != null && !aText.trim().equals(emptyString);
	}

	public static void recursivelySetFonts(Component comp, Font font) {
		comp.setFont(font);
		if (comp instanceof Container) {
			Container cont = (Container) comp;
			int j = 0;
			for (int ub = cont.getComponentCount(); j < ub; j++) {
				recursivelySetFonts(cont.getComponent(j), font);
				// System.out.println(cont.getComponent(j).getClass());
				if (!cont.getComponent(j).getClass().toString().equals(
						"class javax.swing.JPanel")) {
					cont.getComponent(j).setBackground(sysBackColor);
				}
			}
		}
	}

	public static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager
					.getCrossPlatformLookAndFeelClassName());
		} catch (Exception exception) {
		}
	}

	public static String arrayToString(String a[], String separator) {
		StringBuffer result = new StringBuffer();
		if (a != null && a.length > 0) {
			result.append(a[0]);
			for (int i = 1; i < a.length; i++)
				if (a[i] != null) {
					result.append(separator);
					result.append(a[i]);
				}

		}
		return result.toString();
	}

	public static void ZoomOut() {
		Node.nodeSize = (Node.nodeSize * 75) / 100;
		Node.cloudradius = (Node.cloudradius * 90) / 100;
		Node.RADIUS = (Node.RADIUS * 75) / 100;
		for (int i = 0; i < Graph.nodes.size(); i++) {
			Node aNode = (Node) Graph.nodes.get(i);
			Point newPoint = aNode.getLocation();
			newPoint.x = (newPoint.x * 75) / 100;
			newPoint.y = (newPoint.y * 75) / 100;
			aNode.setLocation(newPoint);
		}

		for (int i = 0; i < Graph.shapes.size(); i++) {
			Shape aShape = (Shape) Graph.shapes.get(i);
			Point newPoint = aShape.getXY();
			double xx = (newPoint.getX() * 75D) / 100D;
			double yy = (newPoint.getY() * 75D) / 100D;
			newPoint.setLocation(xx, yy);
			aShape.setXY(newPoint);
			int x = aShape.getHeight();
			int y = aShape.getWidth();
			aShape.setHeight((x * 75) / 100);
			aShape.setWidth((y * 75) / 100);
		}

	}

	public static void ZoomIn() {
		Node.nodeSize = Node.nodeSize = 42;
		Node.cloudradius = 60;
		Node.RADIUS = 20;
		for (int i = 0; i < Graph.nodes.size(); i++) {
			Node aNode = (Node) Graph.nodes.get(i);
			Point newPoint = aNode.getLocation();
			newPoint.x = (newPoint.x * 100) / 75;
			newPoint.y = (newPoint.y * 100) / 75;
			aNode.setLocation(newPoint);
		}

		for (int i = 0; i < Graph.shapes.size(); i++) {
			Shape aShape = (Shape) Graph.shapes.get(i);
			Point newPoint = aShape.getXY();
			double xx = (newPoint.getX() * 100D) / 75D;
			double yy = (newPoint.getY() * 100D) / 75D;
			newPoint.setLocation(xx, yy);
			aShape.setXY(newPoint);
			int x = aShape.getHeight();
			int y = aShape.getWidth();
			aShape.setHeight((x * 100) / 75);
			aShape.setWidth((y * 100) / 75);
		}

	}

	public static void log(String log) {
		BufferedWriter aFile = null;
		try {
			aFile = new BufferedWriter(new FileWriter("log/netwhistler.log",
					true));
			Date now = new Date();
			Format formatter = new SimpleDateFormat("MM.dd.yyyy HH.mm.ss");
			aFile.write("[" + formatter.format(now) + "] " + progName + ": "
					+ log + "\n");
			aFile.close();
		} catch (IOException ioexception) {
		}
	}

	public static boolean checkProg(String name) {
		Runtime r = Runtime.getRuntime();
		Process p = null;
		try {
			p = r.exec("which " + name);
			if (p == null) {
				return false;
				// System.out.println("Cant't");
			}
			// Read the response from the "ping" program
			BufferedReader in = new BufferedReader(new InputStreamReader(p
					.getInputStream()));
			String line;

			// Now parse the response to see if a reply or timeout was received
			while ((line = in.readLine()) != null) {
				// System.out.print("R: " + line + "\n");
				if (NetworkManagerGUI.textHasContent(line)) {
					// System.out.println("PATH:" + line);
					return true;
				}
			}
		} catch (IOException ioexception) {
		}
		return false;
	}

	public static void main(String args[]) {
		
		boolean append = true;
		try {
	        // Create an appending file handler
	        handler = new FileHandler("logs/monitor.log", append);
	        handler.setFormatter(new nFormatter());
	        logger = Logger.getLogger("monitor");
	        logger.addHandler(handler);
	    } catch (IOException e) {
	    }
	    try {
	        
	        phandler = new FileHandler("logs/events.log", append);
	        phandler.setFormatter(new nFormatter());
	        plogger = Logger.getLogger("pinger");
	        plogger.addHandler(phandler);
	    } catch (IOException e) {
	    }
	    
	   
	    logger.info(" Netwhistler starting");
	   
		
		
		if (!checkProg("fping")) {
			JDialog tmp = new JDialog();
			new MessageDialog(tmp, "Can't find fping in your PATH!", "Fping");
			logger.info(" fping not found in PATH");
		}
		frame = new JFrame(progName);
		manager = new NetworkManager(new Graph());
		basePanel = new JPanel();
		panels = new CardLayout();
		basePanel.setLayout(panels);
		netPanel = new NetPanel();
		new LocalDiscover();
		frame.setSize(800, 600);
		imageURL = nnm.NetworkManagerGUI.class.getResource("icons/nw.gif");
		ImageIcon frameIcon = new ImageIcon(imageURL);
		java.awt.Image image = frameIcon.getImage();
		//frame.setIconImage(image);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension optSize = frame.getSize();
		if (optSize.height > screenSize.height)
			optSize.height = screenSize.height;
		if (optSize.width > screenSize.width)
			optSize.width = screenSize.width;
		frame.setLocation((screenSize.width - optSize.width) / 2,
				(screenSize.height - optSize.height) / 2);
		frame.setDefaultCloseOperation(3);
		JMenuBar mbar = new JMenuBar();
		mbar.setBackground(sysBackColor);
		frame.setJMenuBar(mbar);
		final JToolBar toolbar = new JToolBar();
		JMenu map = new JMenu("Map");
		map.setMnemonic(77);
		map.setFont(baseFont);
		JMenuItem newItem;
		map.add(newItem = new JMenuItem("New"));
		newItem.setFont(baseFont);
		newItem.setAccelerator(KeyStroke.getKeyStroke(78, 2));
		newItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ConfirmDialog dlg = new ConfirmDialog(frame, "Save changes?",
						"Current map");
				boolean yes = dlg.getAction();
				if (yes)
					if (filePath != null) {
						try {
							writeMap(filePath);
						} catch (IOException ioexception) {
							logger.info(" Can't write map file");
						}
					} else {
						JFileChooser fc = new JFileChooser();
						recursivelySetFonts(fc, baseFont);
						fc.addChoosableFileFilter(new MapFilter("xml", progName
								+ " xml maps"));
						fc.setAcceptAllFileFilterUsed(false);
						fc.setCurrentDirectory(new File("maps/"));
						int retval = fc.showSaveDialog(null);
						if (retval == 0) {
							File file = fc.getSelectedFile();
							try {
								writeMap(file);
							} catch (IOException ioexception1) {
								logger.info(" Can't write map file");
							}
						}
					}
				new Graph("Network");
				Graph.loadDefaults();
				frame.repaint();
			}

		});
		JMenuItem openItem;
		map.add(openItem = new JMenuItem("Open"));
		map.add(saveItem = new JMenuItem("Save"));
		JMenuItem saveasItem;
		map.add(saveasItem = new JMenuItem("Save As ..."));
		map.addSeparator();
		// JMenuItem DBItem;
		// map.add(DBItem = new JMenuItem("Export to/Import from DB..."));
		// map.addSeparator();
		JMenuItem saveImItem;
		map.add(saveImItem = new JMenuItem("Save As Image..."));
		map.addSeparator();
		JMenuItem exitItem;
		map.add(exitItem = new JMenuItem("Exit"));
		openItem.setFont(baseFont);
		// DBItem.setFont(baseFont);
		saveItem.setFont(baseFont);
		saveasItem.setFont(baseFont);
		saveImItem.setFont(baseFont);

		exitItem.setFont(baseFont);
		openItem.setAccelerator(KeyStroke.getKeyStroke(79, 2));
		saveItem.setAccelerator(KeyStroke.getKeyStroke(83, 2));

		exitItem.setAccelerator(KeyStroke.getKeyStroke(88, 8));
		openItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				recursivelySetFonts(fc, baseFont);
				fc.addChoosableFileFilter(new MapFilter("xml",
						"NetWhistler xml maps"));
				fc.setCurrentDirectory(new File("maps/"));
				fc.setAcceptAllFileFilterUsed(false);
				int retval = fc.showOpenDialog(null);
				if (retval == 0) {
					File file = fc.getSelectedFile();
					filePath = file;
					try {
						loadMap(file);
						frame.repaint();
					} catch (IOException ioexception) {
						logger.info(" Can't load map file");
					}
				}
			}

		});
		/*
		 * DBItem.addActionListener(new ActionListener() {
		 * 
		 * public void actionPerformed(ActionEvent e) { DBconnect(); }
		 * 
		 * });
		 */
		saveItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (filePath != null) {
					try {
						writeMap(filePath);
					} catch (IOException ioexception) {
						logger.info(" Can't write map file");
					}
				} else {
					JFileChooser fc = new JFileChooser();
					recursivelySetFonts(fc, baseFont);
					fc.addChoosableFileFilter(new MapFilter("xml", progName
							+ " xml maps"));
					fc.setAcceptAllFileFilterUsed(false);
					fc.setCurrentDirectory(new File("maps/"));
					int retval = fc.showSaveDialog(null);
					if (retval == 0) {
						File file = fc.getSelectedFile();
						try {
							writeMap(file);
						} catch (IOException ioexception1) {
							logger.info(" Can't write map file");
						}
					}
				}
			}

		});
		saveasItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				recursivelySetFonts(fc, baseFont);
				fc.addChoosableFileFilter(new MapFilter("xml", progName
						+ " xml maps"));
				fc.setAcceptAllFileFilterUsed(false);
				int retval = fc.showSaveDialog(null);
				fc.setCurrentDirectory(new File("maps/"));
				if (retval == 0) {
					File file = fc.getSelectedFile();
					filePath = new File(file + "xml");
					try {
						writeMap(file);
					} catch (IOException ioexception) {
						logger.info(" Can't write map file");
					}
				}
			}

		});
		saveImItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				recursivelySetFonts(fc, baseFont);
				fc.addChoosableFileFilter(new MapFilter("png",
						"Portable Network Graphics"));
				fc.setAcceptAllFileFilterUsed(false);
				fc.setCurrentDirectory(new File("."));
				int retval = fc.showSaveDialog(null);
				if (retval == 0) {
					File file = fc.getSelectedFile();
					Dimension size = frame.getSize();
					BufferedImage myImage = new BufferedImage(size.width,
							size.height, 1);
					java.awt.Graphics2D g2 = myImage.createGraphics();
					manager.paint(g2);
					try {
						OutputStream out = null;
						String fileStr = file.toString();
						if (fileStr.endsWith("png"))
							out = new FileOutputStream(file);
						else
							out = new FileOutputStream(file + ".png");
						ImageIO.write(myImage, "png", out);
						out.close();
					} catch (Exception exception1) {
						logger.info(" Can't write map image");
					}
				}
			}

		});

		exitItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ConfirmDialog dlg = new ConfirmDialog(frame,
						"Save current Map?", "Current map");
				boolean yes = dlg.getAction();
				if (yes) {
					if (filePath != null) {
						try {
							writeMap(filePath);
							logger.info(" Netwhistler shutdown");
							System.exit(0);
						} catch (IOException ioexception) {
						}
					} else {
						JFileChooser fc = new JFileChooser();
						recursivelySetFonts(fc, baseFont);
						fc.addChoosableFileFilter(new MapFilter("xml", progName
								+ " xml maps"));
						fc.setAcceptAllFileFilterUsed(false);
						fc.setCurrentDirectory(new File("maps/"));
						int retval = fc.showSaveDialog(null);
						if (retval == 0) {
							File file = fc.getSelectedFile();
							try {
								writeMap(file);
								System.exit(0);
							} catch (IOException ioexception1) {
								logger.info(" Can't write map file");
							}
						}
					}
				} else {
					logger.info(" Netwhistler shutdown");
					System.exit(0);
				}
			}

		});
		mbar.add(map);
		JMenu edit = new JMenu("Edit");
		edit.setMnemonic(69);
		edit.setFont(baseFont);
		final JMenuItem netItem;
		edit.add(netItem = new JMenuItem("Add Network"));
		final JMenuItem addItem;
		edit.add(addItem = new JMenuItem("Add Node"));
		final JMenuItem boxItem;
		edit.add(boxItem = new JMenuItem("Add Box"));
		final JMenuItem groupItem;
		edit.add(groupItem = new JMenuItem("Map Operations"));
		edit.addSeparator();
		final JMenuItem optItem;
		edit.add(optItem = new JMenuItem("Options"));
		netItem.setFont(baseFont);
		addItem.setFont(baseFont);
		boxItem.setFont(baseFont);
		groupItem.setFont(baseFont);
		optItem.setFont(baseFont);
		netItem.setAccelerator(KeyStroke.getKeyStroke(115, 0));
		addItem.setAccelerator(KeyStroke.getKeyStroke(116, 0));
		groupItem.setAccelerator(KeyStroke.getKeyStroke(71, 2));
		boxItem.setAccelerator(KeyStroke.getKeyStroke(117, 0));
		optItem.setAccelerator(KeyStroke.getKeyStroke(120, 0));
		netItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				netPanel.addNetworkDialog();
			}

		});
		addItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				NetworkManager.propertDialog(null, true);
			}

		});
		boxItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				optShapeDialog(null, true, new Point(100, 100));
			}

		});
		groupItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				groupDialog();
			}

		});
		optItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				optionsDialog();
			}

		});
		mbar.add(edit);
		JMenu monitor = new JMenu("Monitoring");
		monitor.setMnemonic(79);
		monitor.setFont(baseFont);
		monitor.add(monItem = new JMenuItem("Start Monitoring"));
		monItem.setFont(baseFont);
		monItem.setAccelerator(KeyStroke.getKeyStroke(119, 0));
		monItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (Graph.nodes.size() == 0) {
					JDialog tmp = new JDialog();
					new MessageDialog(tmp, " Map is empty ", "Monitoring");
				} else {
					if (!MONITORING) {
						MONITORING = true;
						monItem.setText("Stop Monitoring");
						monBut.setIcon(ticonstop);
						monBut.setToolTipText("Monitoring Stop");
						for (int i = 0; i < Graph.nodes.size(); i++) {
							Node aNode = (Node) Graph.nodes.get(i);
							aNode.setStartTime();
							aNode.setStartDownTime();
						}
						logger.info(" Monitoring started");
						new pingTimer(timeoutMon);
						new CheckServices(timeoutMonServices);
						new SNMPTimer(timeoutMonSNMP);
						new Blinker(1);
					} else {
						logger.info(" Monitoring stopped");
						MONITORING = false;
						monItem.setText("Start Monitoring");
						monBut.setIcon(ticonstart);
						monBut.setToolTipText("Monitoring Start");
						for (int i = 0; i < Graph.nodes.size(); i++) {
							Node aNode = (Node) Graph.nodes.get(i);
							if (aNode.getBadStatus())
								aNode.setBadColorDefault();
						}
					}
					monBut.repaint();
				}
			}

		});
		// monitor.addSeparator();
		mbar.add(monitor);
		JMenu view = new JMenu("View");
		view.setMnemonic(86);
		view.setFont(baseFont);
		final JCheckBoxMenuItem toolbarItem;
		view.add(toolbarItem = new JCheckBoxMenuItem("Show Toolbar "));
		toolbarItem.setFont(baseFont);
		toolbarItem.setAccelerator(KeyStroke.getKeyStroke(66, 2));
		toolbarItem.setSelected(true);
		toolbarItem.setRolloverEnabled(true);
		toolbarItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (toolbarItem.isSelected())
					toolbar.setVisible(true);
				else
					toolbar.setVisible(false);
			}

		});
		final JCheckBoxMenuItem mapTreeItem;
		view.add(mapTreeItem = new JCheckBoxMenuItem("Show Map Tree "));
		mapTreeItem.setFont(baseFont);
		mapTreeItem.setAccelerator(KeyStroke.getKeyStroke(113, 0));
		mapTreeItem.setSelected(true);
		mapTreeItem.setRolloverEnabled(true);
		mapTreeItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (mapTreeItem.isSelected())
					treePanel.setVisible(true);
				else
					treePanel.setVisible(false);
			}

		});
		JMenuItem fullscreenItem;
		view.add(fullscreenItem = new JMenuItem("Full Screen "));
		fullscreenItem.setFont(baseFont);
		fullscreenItem.setAccelerator(KeyStroke.getKeyStroke(122, 0));
		fullscreenItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				GraphicsEnvironment ge = GraphicsEnvironment
						.getLocalGraphicsEnvironment();
				GraphicsDevice gs = ge.getDefaultScreenDevice();
				if (!FULLSCREEN) {
					frame.dispose();
					frame.setUndecorated(true);
					frame.setResizable(false);
					gs.setFullScreenWindow(frame);
					FULLSCREEN = true;
				} else {
					frame.dispose();
					frame.setUndecorated(false);
					frame.setResizable(true);
					gs.setFullScreenWindow(null);
					FULLSCREEN = false;
					frame.setVisible(true);
					frame.setExtendedState(6);
				}
			}

		});
		view.add(zoomItem = new JMenuItem("Zoom Out (-)"));
		zoomItem.setFont(baseFont);
		zoomItem.setAccelerator(KeyStroke.getKeyStroke(90, 2));
		zoomItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (!ZOOM) {
					ZOOM = true;
					zoomItem.setText("Zoom In (+)");
					zoomBut.setIcon(ticonin);
					zoomBut.setToolTipText("Zoom In (+)");
					ZoomOut();
				} else {
					ZOOM = false;
					zoomItem.setText("Zoom Out (-)");
					zoomBut.setIcon(ticonout);
					zoomBut.setToolTipText("Zoom Out (-)");
					ZoomIn();
				}
				zoomBut.repaint();
				frame.repaint();
			}

		});
		mbar.add(view);
		JMenu snmp = new JMenu("SNMP");
		snmp.setMnemonic(78);
		snmp.setFont(baseFont);
		final JMenuItem mrtgItem = new JMenuItem("MRTG Console");
		snmp.add(mrtgItem);
		mrtgItem.setFont(baseFont);
		mrtgItem.setAccelerator(KeyStroke.getKeyStroke(82, 2));
		mrtgItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Client client = null;
				try {
					client = new Client();
				} catch (IOException ioexception) {
				}
				client.setVisible(true);
			}

		});
		final JMenuItem trapItem;
		snmp.add(trapItem = new JMenuItem("Trap Console"));
		trapItem.setFont(baseFont);
		trapItem.setAccelerator(KeyStroke.getKeyStroke(84, 2));
		trapItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				TrapConsole.showTrap();
			}

		});
		mbar.add(snmp);
		JMenu tools = new JMenu("Tools");
		tools.setMnemonic(84);
		tools.setFont(baseFont);
		final JMenuItem syslogItem;
		tools.add(syslogItem = new JMenuItem("Syslog Console"));
		// final JMenuItem fwItem;
		// tools.add(fwItem = new JMenuItem("Firewall Builder"));
		syslogItem.setFont(baseFont);
		// fwItem.setFont(baseFont);
		syslogItem.setAccelerator(KeyStroke.getKeyStroke(76, 2));
		// fwItem.setAccelerator(KeyStroke.getKeyStroke(70, 2));
		syslogItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				SyslogConsole slog = new SyslogConsole();
				slog.init();
			}

		});
		/*
		 * fwItem.addActionListener(new ActionListener() {
		 * 
		 * public void actionPerformed(ActionEvent e) { new FWBuilder(); }
		 * 
		 * });
		 */
		mbar.add(tools);
		JMenu help = new JMenu("Help");
		help.setMnemonic(72);
		help.setFont(baseFont);
		JMenuItem helpItem;
		help.add(helpItem = new JMenuItem("Help"));
		JMenuItem aboutItem;
		help.add(aboutItem = new JMenuItem("About"));
		helpItem.setFont(baseFont);
		aboutItem.setFont(baseFont);
		helpItem.setAccelerator(KeyStroke.getKeyStroke(112, 0));
		aboutItem.setAccelerator(KeyStroke.getKeyStroke(112, 2));
		helpItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				new HelpDlg(frame);
			}

		});
		aboutItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				aboutDialog();
			}

		});
		mbar.add(help);

		optBut = new JButton();
		// optBut.setBorderPainted(false);
		optBut.setPreferredSize(new Dimension(24, 24));
		optBut.setMaximumSize(new Dimension(24, 24));
		optBut.setToolTipText("Options");
		imageURL = nnm.NetworkManagerGUI.class.getResource("icons/options.gif");
		ticon = new ImageIcon(imageURL);
		optBut.setIcon(ticon);
		optBut.setBackground(sysBackColor);
		optBut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				optItem.doClick();
			}

		});
		monBut = new JButton();
		monBut.setBorderPainted(false);
		monBut.setToolTipText("Monitoring Start");
		imageURL = nnm.NetworkManagerGUI.class
				.getResource("icons/monstart.gif");
		ticonstart = new ImageIcon(imageURL);
		imageURL = nnm.NetworkManagerGUI.class.getResource("icons/monstop.gif");
		ticonstop = new ImageIcon(imageURL);
		monBut.setIcon(ticonstart);
		monBut.setBackground(sysBackColor);
		monBut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				monItem.doClick();
				if (MONITORING) {
					monBut.setIcon(ticonstop);
					monBut.setToolTipText("Monitoring Stop");
				} else {
					monBut.setIcon(ticonstart);
					monBut.setToolTipText("Monitoring Start");
				}
				monBut.repaint();
			}

		});

		upBut = new JButton();
		// upBut.setBorderPainted(false);
		upBut.setPreferredSize(new Dimension(24, 24));
		upBut.setMaximumSize(new Dimension(24, 24));
		upBut.setBackground(sysBackColor);
		upBut.setToolTipText("Parent");
		imageURL = nnm.NetworkManagerGUI.class.getResource("icons/parent.gif");
		ticon = new ImageIcon(imageURL);
		upBut.setIcon(ticon);
		upBut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (ipnetPanel.isShowing()) {
					return;
				} else {
					topcards.previous(cardsPanel);
					return;
				}
			}

		});
		final String looks[] = new String[5];
		looks[0] = "<IP Address>";
		looks[1] = "<DNS Name>";
		looks[2] = "<MAC Address>";
		String look_entries[] = { looks[0], looks[1], looks[2] };
		DefaultComboBoxModel nameModel = new DefaultComboBoxModel();
		for (int i = 0; i < look_entries.length; i++)
			nameModel.addElement(look_entries[i]);

		JComboBox looksBox = new JComboBox(nameModel);
		looksBox.setFont(baseFont);
		looksBox.setPreferredSize(new Dimension(150, 20));
		looksBox.setMaximumSize(new Dimension(150, 20));
		looksBox.setBackground(sysBackColor);
		looksBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				String s = (String) e.getItem();
				if (s.equals(looks[0]))
					MapTree.LOOK = 0;
				else if (s.equals(looks[1]))
					MapTree.LOOK = 1;
				else if (s.equals(looks[2]))
					MapTree.LOOK = 2;
				MapTree _tmp = treePanel;
				MapTree.refresh();
			}

		});
		String selframe[] = new String[5];
		selframe[0] = "Topology View";
		selframe[1] = "Status View";
		selframe[2] = "Interface View";
		selframe[3] = "Events View";
		selframe[4] = "Graphs View";
		String sel_entries[] = { selframe[0], selframe[1], selframe[2],
				selframe[3], selframe[4] };
		DefaultComboBoxModel selModel = new DefaultComboBoxModel();
		for (int i = 0; i < sel_entries.length; i++)
			selModel.addElement(sel_entries[i]);

		JComboBox selBox = new JComboBox(selModel);
		selBox.setBackground(sysBackColor);
		selBox.setFont(baseFont);
		selBox.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				String s = (String) e.getItem();
				CardLayout cl = (CardLayout) basePanel.getLayout();
				cl.show(basePanel, (String) e.getItem());
				if (!s.equals("Topology View")) {
					addItem.setEnabled(false);
					netItem.setEnabled(false);
					boxItem.setEnabled(false);
					groupItem.setEnabled(false);
					upBut.setEnabled(false);
					zoomBut.setEnabled(false);
				} else {
					addItem.setEnabled(true);
					netItem.setEnabled(true);
					boxItem.setEnabled(true);
					groupItem.setEnabled(true);
					upBut.setEnabled(true);
					zoomBut.setEnabled(true);
				}
			}

		});
		selBox.setPreferredSize(new Dimension(150, 20));
		selBox.setMaximumSize(new Dimension(150, 20));

		// finder
		String[] items = { "find" };
		findtf = new JComboBox(items);
		findtf.setEditable(true);
		findtf.setToolTipText("Find Node");
		findtf.setPreferredSize(new Dimension(200, 20));
		findtf.setMaximumSize(new Dimension(200, 20));
		findtf.setBackground(sysBackColor.brighter());
		findtf.setFont(baseFont);
		findtf.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				findNode();

			}
		});
		/*
		 * findtf.addKeyListener (new KeyAdapter() { public void
		 * keyTyped(KeyEvent e) {} public void keyReleased(KeyEvent e) {} public
		 * void keyPressed(KeyEvent e) { int key = e.getKeyCode();
		 * System.out.println("Enter!" + key); if (key == KeyEvent.VK_ENTER) {
		 * 
		 * findNode(); } } } );
		 */
		findBut = new JButton();
		// findBut.setBorderPainted(false);
		findBut.setBackground(sysBackColor);
		findBut.setPreferredSize(new Dimension(24, 24));
		findBut.setMaximumSize(new Dimension(24, 24));
		findBut.setToolTipText("Find");
		imageURL = nnm.NetworkManagerGUI.class.getResource("icons/find.gif");
		ticon = new ImageIcon(imageURL);
		findBut.setIcon(ticon);
		findBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				findNode();
			}

		});

		// zoom
		zoomBut = new JButton();
		zoomBut.setBackground(sysBackColor);
		zoomBut.setPreferredSize(new Dimension(24, 24));
		zoomBut.setMaximumSize(new Dimension(24, 24));
		zoomBut.setToolTipText("Zoom Out (-)");
		imageURL = NetworkManagerGUI.class.getResource("icons/zoomout.gif");
		ticonout = new ImageIcon(imageURL);
		imageURL = NetworkManagerGUI.class.getResource("icons/zoomin.gif");
		ticonin = new ImageIcon(imageURL);
		zoomBut.setIcon(ticonout);
		zoomBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zoomItem.doClick();
				if (ZOOM) {
					zoomBut.setIcon(ticonin);
					zoomBut.setToolTipText("Zoom In (+)");
				} else {
					zoomBut.setIcon(ticonout);
					zoomBut.setToolTipText("Zoom Out (+)");
				}
				zoomBut.repaint();
			}
		});
		toolbar.add(looksBox);
		toolbar.addSeparator();
		toolbar.add(upBut);
		toolbar.add(zoomBut);
		toolbar.add(optBut);
		toolbar.addSeparator();
		toolbar.add(findtf);
		toolbar.addSeparator();
		toolbar.add(findBut);
		toolbar.addSeparator();

		toolbar.add(selBox);
		toolbar.setFloatable(false);
		toolbar.setBackground(sysBackColor);
		frame.getContentPane().add(toolbar, "North");
		javax.swing.border.Border border = BorderFactory.createEtchedBorder(
				Color.white, new Color(178, 178, 178));
		status = new JLabel(" ");
		status.setFont(baseFont);
		JPanel bottomP = new JPanel();
		bottomP.setLayout(new GridLayout(1, 13));
		JLabel tm = new JLabel(" ");
		tm.setToolTipText("Local Time");
		tm.setFont(baseFont);
		Ticker t = new Ticker(tm);
		t.start();
		nodelb = new JLabel(" ");
		nodelb.setFont(baseFont);
		nodelb.setToolTipText("Nodes State");
		HowNodes hn = new HowNodes(nodelb);
		hn.start();
		mouselb = new JLabel(" ");
		mouselb.setFont(baseFont);
		mouselb.setToolTipText("Mouse Location");
		MouseXY mx = new MouseXY(mouselb);
		mx.start();
		Box hbox = Box.createHorizontalBox();
		hbox.add(status);
		hbox.add(Box.createGlue());
		hbox.add(nodelb);
		hbox.add(Box.createGlue());
		hbox.add(mouselb);
		hbox.add(Box.createGlue());
		hbox.add(tm);
		bottomP.add(hbox);
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());
		statusP.setLayout(new BorderLayout());
		statusArea = new JTextArea(" ");
		statusArea.setEditable(false);
		statusArea.setRows(6);
		statusArea.setBackground(new Color(221, 221, 221));
		JScrollPane scroll = new JScrollPane(statusArea);
		scroll.setVerticalScrollBarPolicy(22);
		scroll.setHorizontalScrollBarPolicy(30);
		statusP.setVisible(false);
		statusP.setBackground(sysBackColor);
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new FlowLayout(0));
		JButton closebtn = new JButton();
		closebtn.setBackground(sysBackColor);
		imageURL = nnm.NetworkManagerGUI.class.getResource("icons/bad.gif");
		ImageIcon icon = new ImageIcon(imageURL);
		closebtn.setIcon(icon);
		closebtn.setPreferredSize(new Dimension(15, 15));
		closebtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				statusArea.setText("");
				TraceRoute.TRACE = false;
				statusP.setVisible(false);
				Node.showHops = false;
				manager.repaint();
			}

		});
		btnPanel.add(closebtn);
		JLabel btnlb = new JLabel("Trace panel");
		btnlb.setFont(baseFont);
		btnPanel.add(btnlb);
		statusP.add(btnPanel, "North");
		statusP.add(scroll, "South");
		southPanel.add(statusP, "North");
		southPanel.add(bottomP, "South");
		frame.getContentPane().add(southPanel, "South");
		manager.setPreferredSize(new Dimension(6400, 4800));
		manager.setAutoscrolls(true);
		int v = 20;
		int h = 30;
		jsp = new JScrollPane(manager, v, h);
		treePanel = new MapTree();
		ipnetPanel = new IPnetPanel();
		netPanel.addNetworks();
		netPanel.repaint();
		cardsPanel = new JPanel();
		topcards = new CardLayout();
		cardsPanel.setLayout(topcards);
		netPanel.setPreferredSize(new Dimension(3200, 2400));
		netPanel.setAutoscrolls(true);
		cardsPanel.add(ipnetPanel, "First");
		netjsp = new JScrollPane(netPanel, v, h);
		cardsPanel.add(netjsp, "Second");
		cardsPanel.add(jsp, "Third");
		jsp.addHierarchyListener(new HierarchyListener() {

			public void hierarchyChanged(HierarchyEvent e) {
				if (jsp.isShowing()) {
					addItem.setEnabled(true);
					netItem.setEnabled(false);
					boxItem.setEnabled(true);
					groupItem.setEnabled(true);
					nodelb.setVisible(true);
					mouselb.setVisible(true);
					findtf.setEnabled(true);
					findBut.setEnabled(true);
					zoomBut.setEnabled(true);
					upBut.setEnabled(true);

				}
			}

		});
		netjsp.addHierarchyListener(new HierarchyListener() {

			public void hierarchyChanged(HierarchyEvent e) {
				if (netjsp.isShowing()) {
					addItem.setEnabled(false);
					netItem.setEnabled(true);
					boxItem.setEnabled(false);
					groupItem.setEnabled(false);
					nodelb.setVisible(false);
					mouselb.setVisible(false);
					zoomBut.setEnabled(false);
					upBut.setEnabled(true);
					findtf.setEnabled(false);
					findBut.setEnabled(false);
				}
			}

		});
		ipnetPanel.addHierarchyListener(new HierarchyListener() {

			public void hierarchyChanged(HierarchyEvent e) {
				if (ipnetPanel.isShowing()) {
					addItem.setEnabled(false);
					netItem.setEnabled(false);
					boxItem.setEnabled(false);
					groupItem.setEnabled(false);
					nodelb.setVisible(false);
					mouselb.setVisible(false);
					zoomBut.setEnabled(false);
					upBut.setEnabled(false);
					findtf.setEnabled(false);
					findBut.setEnabled(false);
				}
			}

		});
		basePanel.add(cardsPanel, "Topology View");
		statPanel = new StatusPanel();
		statPanel.setAutoscrolls(true);
		JScrollPane statjsp = new JScrollPane(statPanel, v, h);
		basePanel.add(statjsp, "Status View");
		ifacePanel = new IfacePanel();
		ifacePanel.setAutoscrolls(true);
		JScrollPane ifacejsp = new JScrollPane(ifacePanel, v, h);
		basePanel.add(ifacejsp, "Interface View");
		eventPanel = new EventPanel();
		eventPanel.setAutoscrolls(true);
		JScrollPane eventjsp = new JScrollPane(eventPanel, v, 30);
		basePanel.add(eventjsp, "Events View");
		graphsPanel = new GraphsPanel();
		graphsPanel.setAutoscrolls(true);
		JScrollPane graphsjsp = new JScrollPane(graphsPanel, v, 30);
		basePanel.add(graphsjsp, "Graphs View");
		panels.show(basePanel, "Topology View");
		basePanel.setMinimumSize(new Dimension(200, 50));
		splitPane = new JSplitPane(1, treePanel, basePanel);
		splitPane.setContinuousLayout(true);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(5);
		splitPane.setDividerLocation(100);
		frame.getContentPane().add(splitPane, "Center");
		
		 // SPLASH! 
		  SplashScreen splashScreen = new SplashScreen();
		 splashScreen.open(5000); while(splash) try { Thread.yield(); }
		  catch(Exception exception) { }
		 
		setLookAndFeel();
		manager.setBackground(backgroundColor);
		Graph.loadDefaults();
		
		
		manager.repaint();
		frame.setVisible(true);
		frame.setExtendedState(6);
		
		frame.addWindowListener(new WindowListener() {

			public void windowClosing(WindowEvent e) {
				ConfirmDialog dlg = new ConfirmDialog(frame,
						"Save current map?", "Current map");
				boolean yes = dlg.getAction();
				if (yes) {
					if (filePath != null) {
						try {
							writeMap(filePath);
							logger.info(" Netwhistler shutdown");
							System.exit(0);
						} catch (IOException ioexception) {
							logger.info(" Can't write map file");
						}
					} else {
						JFileChooser fc = new JFileChooser();
						recursivelySetFonts(fc, baseFont);
						fc.addChoosableFileFilter(new MapFilter("xml", progName
								+ " xml maps"));
						fc.setAcceptAllFileFilterUsed(false);
						fc.setCurrentDirectory(new File("maps/"));
						int retval = fc.showSaveDialog(null);
						if (retval == 0) {
							File file = fc.getSelectedFile();
							try {
								writeMap(file);
								logger.info(" Netwhistler shutdown");
								System.exit(0);
							} catch (IOException ioexception1) {
								logger.info(" Can't write map file");
							}
						}
					}
				} else {
					logger.info(" Netwhistler shutdown");
					System.exit(0);
				}
			}

			public void windowOpened(WindowEvent windowevent) {
			}

			public void windowClosed(WindowEvent windowevent) {
			}

			public void windowIconified(WindowEvent windowevent) {
			}

			public void windowDeiconified(WindowEvent windowevent) {
			}

			public void windowActivated(WindowEvent windowevent) {
			}

			public void windowDeactivated(WindowEvent windowevent) {
			}

		});
	}

	public static void findNode() {
		Node aNode = null;
		String text = (String) findtf.getSelectedItem();

		int num = findtf.getItemCount();
		boolean yes = false;
		for (int i = 0; i < num; i++) {
			String item = (String) findtf.getItemAt(i);
			if (item.equals(text))
				yes = true;

		}
		if (!yes)
			findtf.insertItemAt(text, 0);
		if (textHasContent(text)) {
			for (int i = 0; i < Graph.nodes.size(); i++) {
				aNode = (Node) Graph.nodes.get(i);
				if (aNode.isSelected())
					aNode.toggleSelected();
				if (aNode.getLabel().contains(text)
						|| aNode.getIP().contains(text)
						|| aNode.getDNSname().contains(text)) {
					// System.out.println("Find: " + aNode.getLabel());
					treePanel.selectNode(aNode);
					treePanel.expandTree();
					aNode.toggleSelected();
					NetworkManager.currentNetwork = aNode.getNetwork();
					// topcards.show(cardsPanel, "Third");
					JViewport port = jsp.getViewport();
					int w = splitPane.getWidth() / 2;
					int h = splitPane.getHeight() / 2;
					int x = (int) aNode.getLocation().getX();
					int y = (int) aNode.getLocation().getY();
					int x1 = 0;
					int y1 = 0;
					if (x - w <= 0)
						x1 = 0;
					else
						x1 = x - w;
					if (y - h <= 0)
						y1 = 0;
					else
						y1 = y - h;
					Point point = new Point(x1, y1);
					jsp.getViewport().setViewPosition(point);
					jsp.repaint();
					for (int s = 0; s < statPanel.model.getRowCount(); s++) {
						String ip = (String) statPanel.model.getValueAt(s, 1);
						if (ip.equals(aNode.getIP())) {
							// System.out.println("IP:"+ip + "i: " + i);
							statPanel.table.getSelectionModel()
									.setSelectionInterval(s, i);
							statPanel.table.scrollRectToVisible(statPanel.table
									.getCellRect(s, 0, true));
						}
					}
					for (int s = 0; s < ifacePanel.model.getRowCount(); s++) {
						String ip = (String) ifacePanel.model.getValueAt(s, 1);
						if (ip.equals(aNode.getIP())) {
							// System.out.println("IP:"+ip + "i: " + i);
							ifacePanel.table.getSelectionModel()
									.setSelectionInterval(s, i);
							ifacePanel.table
									.scrollRectToVisible(ifacePanel.table
											.getCellRect(s, 0, true));
						}
					}
					for (int q = 0; q < eventPanel.model.getRowCount(); q++) {
						String ip = (String) eventPanel.model.getValueAt(q, 1);
						if (ip.equals(aNode.getIP())) {
							// System.out.println("IP:"+ip + "i: " + i);
							eventPanel.table.getSelectionModel()
									.setSelectionInterval(q, i);
							eventPanel.table
									.scrollRectToVisible(eventPanel.table
											.getCellRect(q, 0, true));
						}
					}

				}

			}
		}
	}

	static {
		backgroundColor = new Color(235, 235, 235);
		textColor = Color.black;
		lineColor = new Color(0, 102, 153);
		selColor = Color.orange;
	}

}
