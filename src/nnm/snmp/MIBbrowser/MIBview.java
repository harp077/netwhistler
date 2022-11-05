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
package nnm.snmp.MIBbrowser;

import nnm.Graph;
import nnm.NetworkManager;
import nnm.NetworkManagerGUI;
import nnm.Node;

import java.io.File;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.util.*;
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
import java.awt.Color;
import uk.co.westhawk.snmp.stack.*;
import uk.co.westhawk.snmp.pdu.*;
import javax.swing.SwingUtilities;
import javax.swing.JComponent;
import nnm.snmp.MIBbrowser.SnmpMibTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;
import nnm.snmp.MIBbrowser.SnmpMibString;
import javax.swing.JTextArea;
import javax.swing.JDialog;
import nnm.util.FixedLengthTextField;
import nnm.util.MessageDialog;

public class MIBview
    extends JPanel {

public MIBview() {
    try {
      jbInit();
    }
    catch (Exception ex) {
      //ex.printStackTrace();
    }
  }

  static final int windowHeight = 500;
  static final int leftWidth = 380;
  static final int rightWidth = 360;
  static final int windowWidth = leftWidth + rightWidth;
  static String host;
  static String rcomm;
  static String wcomm;
  Node aNode;
  public MIBview(Node sNode) {
    aNode = sNode;
	host = aNode.getIP();
    rcomm = aNode.getRcommunity();
    wcomm = aNode.getWcommunity();
    makeFrame();
  }

  public static void makeFrame() {
    // Set up a GUI framework
    final JFrame frame = new JFrame("NetWhistler SNMP MIB browser for " + host);
    java.net.URL imageURL = NetworkManagerGUI.class.getResource("icons/nw.gif");
    ImageIcon frameIcon = new ImageIcon(imageURL);
    Image image = frameIcon.getImage();
    frame.setIconImage(image);
    frame.addWindowListener(
        new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.gc();
        System.runFinalization();
        frame.setVisible(false);
      }
    }
    );

    final GUIMIBview echoPanel = new GUIMIBview();
    frame.getContentPane().add("Center", echoPanel);
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    final JButton cancel = new JButton("Close");
    cancel.setBackground(NetworkManagerGUI.sysBackColor);
    cancel.setFont(NetworkManagerGUI.baseFont);
    cancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        frame.setVisible(false);

      }
    });

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
    frame.setLocation( (screenSize.width - optSize.width) / 2,
                      (screenSize.height - optSize.height) / 2);

    frame.setVisible(true);

  } // makeFrame

  private void jbInit() throws Exception {
  }
}

class GUIMIBview
    extends JPanel {
  boolean compress = true;

  SnmpMibTree treeSupport;

  static String host;
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
    java.net.URL imgURL = NetworkManagerGUI.class.getResource(
        "icons/folder.gif");
    closedicon = new ImageIcon(imgURL);
    imgURL = NetworkManagerGUI.class.getResource("icons/box.gif");
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
    treeView.setPreferredSize(
        new Dimension(MIBview.leftWidth, MIBview.windowHeight));
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

    tree.addTreeSelectionListener(
        new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
        TreePath p = e.getNewLeadSelectionPath();
        if (p != null) {
          DefaultMutableTreeNode defaultmutabletreenode = (
              DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
/// here string!!!!
          String oid = treeSupport.getOID.getNodeOid(defaultmutabletreenode);
          application.toid.setText(oid.trim());
//////////
          application.getButton.doClick();
        }
      }
    }
    );

    // Build split-pane view
    JSplitPane splitPane =
        new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                       treePane,
                       application);
    splitPane.setContinuousLayout(true);
    splitPane.setDividerSize(5);
    splitPane.setDividerLocation(MIBview.leftWidth);
    splitPane.setPreferredSize(
        new Dimension(MIBview.windowWidth + 10, MIBview.windowHeight + 10));

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
      NetworkManagerGUI.recursivelySetFonts(fc, NetworkManagerGUI.baseFont);
      fc.setDialogTitle("Load MIB");
      //fc.addChoosableFileFilter(new MapFilter("mib", "SNMP mibs"));
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
      }
      else {
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

  public class set_one
      extends JComponent
      implements Observer, ActionListener {
    public final static String sysDescr = "1.3.6.1.2.1.1.1.0";
    public String newOID = sysDescr;
    private String host;
    private int port;
    private String rcommunity;
    private String wcommunity;
    private String oid;
    private String value;
    private String socketType;
    public JTextArea textArea;
    private SnmpContextFace context;
    public JTextField tver,toid, trcom, twcom, tport, tvalue;
    public JButton getButton, setButton, getNextButton;
    private int version = SnmpConstants.SNMP_VERSION_1;
    private JLabel lmessage;
    private JComboBox snmpVersion;
    private Pdu pdu;
    private boolean pduInFlight;

    public set_one() {}

    public void init() {

      host = MIBview.host;
      port = SnmpContextBasisFace.DEFAULT_PORT;
      socketType = SnmpContextFace.STANDARD_SOCKET;
      oid = sysDescr;
      rcommunity = MIBview.rcomm;
      wcommunity = MIBview.wcomm;
      pduInFlight = false;
      makeLayout(host, oid, port, rcommunity);
      sendGetRequest(host, port, rcommunity, oid);
    }

    public void actionPerformed(ActionEvent evt) {
      Object src = evt.getSource();
      host = host;
      port = SnmpContextBasisFace.DEFAULT_PORT;
      rcommunity = trcom.getText();
      		
      if (!NetworkManagerGUI.textHasContent(rcommunity))
    	   rcommunity="public";
       wcommunity = twcom.getText();
       if (!NetworkManagerGUI.textHasContent(wcommunity))
    	   wcommunity="private"; 
      oid = toid.getText();
    //System.out.print("Comm:" + rcommunity);
      try {
			if (version == SnmpConstants.SNMP_VERSION_2c) {
				context = new SnmpContextv2cPool(host, port, socketType);
			} else {
				context = new SnmpContextPool(host, port, socketType);
			}
        if (src == getButton) {
          pdu = new OneGetPdu(context);
          pdu.addOid(oid);
          //
          DefaultMutableTreeNode defaultmutabletreenode = (
              DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
          if (defaultmutabletreenode == null) {
            return;
          }
          SnmpMibString snmpmibString = (SnmpMibString) defaultmutabletreenode.
              getUserObject();
          textArea.setText(snmpmibString.getCompleteString());

        } else if (src== getNextButton){
           pdu = new OneGetNextPdu(context);
           pdu.addOid(oid);
        } else if (src == setButton) {
          context.setCommunity(wcommunity);
          OneSetPdu setPdu = new OneSetPdu(context);
          String value = tvalue.getText();
          AsnObject obj;
          if (isNumber(value)) {
            obj = new AsnInteger(getNumber(value));
          }
          else {
            obj = new AsnOctets(value);
          }
          setPdu.addOid(oid, obj);
          pdu = setPdu;
        }
        sendRequest(pdu);
      }
      catch (Exception exc) {
        setErrorMessage("Exception error");
      }
    }

    public boolean isNumber(String str) {
      boolean res = false;
      try {
        int t = Integer.valueOf(str).intValue();
        res = true;
      }
      catch (NumberFormatException e) {}

      return res;
    }

    public int getNumber(String str) {
      int t = 0;
      try {
        t = Integer.valueOf(str).intValue();
      }
      catch (NumberFormatException e) {}

      return t;
    }

    private void sendGetRequest(String host, int port, String community,
                                String oid) {
    	try {
			if (version == SnmpConstants.SNMP_VERSION_2c) {
				context = new SnmpContextv2cPool(host, port, socketType);
			} else {
				context = new SnmpContextPool(host, port, socketType);
			}

        pdu = new OneGetNextPdu(context);
        pdu.addOid(oid);
        sendRequest(pdu);
      }
      catch (java.io.IOException exc) {
        // exc.printStackTrace();
        // setErrorMessage("IOException: " + exc.getMessage());
      }
    }

    private void sendRequest(Pdu pdu) {
      boolean hadError = false;

      setButton.setEnabled(false);
      getButton.setEnabled(false);
      getNextButton.setEnabled(false);
      try {
        if (!pduInFlight) {
          pduInFlight = true;
          setMessage("Sending request ...");

          tvalue.setText("");
          pdu.addObserver(this);
          pdu.send();
        }
        else {
          setErrorMessage("Pdu still in flight");
        }
      }
      catch (PduException exc) {
        //exc.printStackTrace();
        //      setErrorMessage("PduException: " + exc.getMessage());
        setErrorMessage("PduException error");
        hadError = true;
      }
      catch (java.io.IOException exc) {
        //exc.printStackTrace();
        //    setErrorMessage("IOException: " + exc.getMessage());
        setErrorMessage("IOException error");
        hadError = true;
      }

      if (hadError == true) {
        pduInFlight = false;
        setButton.setEnabled(true);
        getButton.setEnabled(true);
        getNextButton.setEnabled(true);

      }
    }

    public void update(Observable obs, Object ov) {
      pduInFlight = false;

      ScreenOnUpdate upd = new ScreenOnUpdate(obs, ov);
      SwingUtilities.invokeLater(upd);
    }

    public void setErrorMessage(String message) {
      setMessage(message, true);
    }

    public void setMessage(String message) {
      setMessage(message, false);
    }

    public void setMessage(String message, boolean isError) {
      lmessage.setText(message);
      Color c = Color.white;
      if (isError) {
        c = Color.red;
      }
      lmessage.setBackground(c);
    }

    private void makeLayout(String host, String oid, int port, String com) {

      JLabel lver, loid, lrcom, lwcom, lvalue;
      this.setLayout(new BorderLayout());
      final JPanel mainP = new JPanel();
      GridBagConstraints c = new GridBagConstraints();
      c.insets = new Insets(1, 1, 1, 1);
      c.anchor = GridBagConstraints.EAST;
      c.fill = GridBagConstraints.HORIZONTAL;
      mainP.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
      textArea = new JTextArea();
      textArea.setEditable(false);
      textArea.setCaretPosition(textArea.getDocument().getLength());
      JScrollPane scroll = new JScrollPane(textArea);
      JPanel info = new JPanel();
      info.setLayout(new BorderLayout());
      info.add(scroll);
      DefaultComboBoxModel verModel;
		String[] version_entries = { "v1", "v2c" };
		verModel = new DefaultComboBoxModel();
		for (int i = 0; i < version_entries.length; i++) {
			verModel.addElement(version_entries[i]);
		}
		snmpVersion = new JComboBox(verModel);
		snmpVersion.setPreferredSize(new Dimension(100, 20));
		snmpVersion.setMinimumSize(new Dimension(100, 20));
		snmpVersion.setFont(NetworkManagerGUI.baseFont);
		snmpVersion.setBackground(NetworkManagerGUI.sysBackColor);
		snmpVersion.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				String v = (String) e.getItem();
				if (v.equals("v2c")) {
					version = SnmpConstants.SNMP_VERSION_2c;
				} else {
					version = SnmpConstants.SNMP_VERSION_1;
				}

			}
		});
      lrcom = new JLabel("Read Community: ");
      lrcom.setFont(NetworkManagerGUI.baseFont);
      lwcom = new JLabel("Write Community: ");
      lwcom.setFont(NetworkManagerGUI.baseFont);
      lver = new JLabel("SNMP Version: ");
	  lver.setFont(NetworkManagerGUI.baseFont);
      loid = new JLabel("OID: ");
      loid.setFont(NetworkManagerGUI.baseFont);
      lvalue = new JLabel("Value: ");
      lvalue.setFont(NetworkManagerGUI.baseFont);
      lmessage = new JLabel("");
      lmessage.setFont(NetworkManagerGUI.baseFont);
      lmessage.setOpaque(false);
      trcom = new FixedLengthTextField(25);
      trcom.setText(rcommunity);
      trcom.setFont(NetworkManagerGUI.baseFont);
      trcom.setEditable(false);
      twcom = new FixedLengthTextField(25);
      twcom.setText(wcommunity);
      twcom.setFont(NetworkManagerGUI.baseFont);
      twcom.setEditable(false);
      toid = new JTextField(20);
      toid.setFont(NetworkManagerGUI.baseFont);
      toid.setText(oid);
      tvalue = new JTextField(20);
      lvalue.setFont(NetworkManagerGUI.baseFont);
      tvalue.setText(value);

      getButton = new JButton("Get");
      getButton.setBackground(NetworkManagerGUI.sysBackColor);
      getButton.setFont(NetworkManagerGUI.baseFont);
      getNextButton = new JButton("Get Next");
      getNextButton.setBackground(NetworkManagerGUI.sysBackColor);
      getNextButton.setFont(NetworkManagerGUI.baseFont);
      setButton = new JButton("Set");
      setButton.setBackground(NetworkManagerGUI.sysBackColor);
      setButton.setFont(NetworkManagerGUI.baseFont);

      mainP.setLayout(new GridBagLayout());
      c.gridx = 0;
      c.gridy = 2;
      mainP.add(lrcom, c);
      c.gridx = 0;
      c.gridy = 4;
      mainP.add(trcom, c);
      c.gridx = 0;
      c.gridy = 6;
      mainP.add(lwcom, c);
      c.gridx = 0;
      c.gridy = 8;
      mainP.add(twcom, c);
      c.gridx = 0;
	  c.gridy = 10;
	  mainP.add(lver, c);
	  c.gridx = 0;
	  c.gridy = 12;
	  mainP.add(snmpVersion, c);
      c.gridx = 0;
      c.gridy = 14;
      mainP.add(loid, c);
      c.gridx = 0;
      c.gridy = 16;
      mainP.add(toid, c);
      c.gridx = 0;
      c.gridy = 18;
      mainP.add(lvalue, c);
      c.gridx = 0;
      c.gridy = 20;
      mainP.add(tvalue, c);
      c.gridx = 0;
      c.gridy = 22;
      mainP.add(lmessage, c);
      final JPanel btnP = new JPanel();
      btnP.setLayout(new FlowLayout());
      btnP.add(getButton);
      btnP.add(getNextButton);
      btnP.add(setButton);
      setButton.addActionListener(this);
      getButton.addActionListener(this);
      getNextButton.addActionListener(this);
      JSplitPane splitPane =
          new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                         info,
                         mainP);
      splitPane.setContinuousLayout(true);
      splitPane.setDividerSize(5);
      splitPane.setDividerLocation(150);

      this.add(splitPane, BorderLayout.CENTER);
      this.add(btnP, BorderLayout.SOUTH);

    }

    class ScreenOnUpdate
        implements Runnable {
      Observable obs;
      Object ov;

      public ScreenOnUpdate(Observable ob, Object obj) {
        obs = ob;
        ov = obj;
      }

      public void run() {
        setMessage("Received answer");
        if (pdu.getErrorStatus() != AsnObject.SNMP_ERR_NOERROR) {
          setErrorMessage(pdu.getErrorStatusString());
        }
        else {
          varbind var = (varbind) ov;
          if (var != null) {
            AsnObjectId oid = var.getOid();
            toid.setText(oid.toString());

            AsnObject obj = var.getValue();
            // SysUpTime  to 00:00:00
            if (toid.getText().equals("1.3.6.1.2.1.1.3.0")) {
              long time = Long.parseLong(obj.toString().trim());
              tvalue.setText(NetworkManager.secondsToString(time));
            }
            else {
              tvalue.setText(obj.toString());
            }
          }
        }
        setButton.setEnabled(true);
        getButton.setEnabled(true);
        getNextButton.setEnabled(true);
      }
    }
  }
} // constructor
