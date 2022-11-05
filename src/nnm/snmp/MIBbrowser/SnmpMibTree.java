// This file based on SNMP MIBbrowser Copyright (C) 2002  Dwipal A. Desai
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

package nnm.snmp.MIBbrowser;

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JTree;
import javax.swing.tree.*;
import javax.swing.ImageIcon;
import nnm.NetworkManagerGUI;

public class SnmpMibTree
    implements MibParserIface, Runnable {

  private DefaultMutableTreeNode rootNode;
  private DefaultMutableTreeNode treeRootNode;
  private DefaultMutableTreeNode rootOrphan;
  JTree tree;
  private Vector mibs;
  private Vector orphanNodes;
  private String errorMsg;
  public SnmpOid getOID;
  SnmpMibTreeHash treeHash;
  SnmpMibTreeHash variableHash;
  SnmpMibTreeHash orphanHash;

  public SnmpMibTree() {

    errorMsg = "";
    getOID = new SnmpOid();
    SnmpMibString snmpmibString = new SnmpMibString();
    snmpmibString.name = "MIB Tree";
    snmpmibString.parent = "MIB Tree";
    snmpmibString.number = 0;
    treeRootNode = new DefaultMutableTreeNode(snmpmibString);
    SnmpMibString snmpmibString1 = new SnmpMibString();
    snmpmibString1.name = "root";
    snmpmibString1.parent = "MIB Tree";
    snmpmibString1.number = 1;
    rootNode = new DefaultMutableTreeNode(snmpmibString1);
    SnmpMibString snmpmibString2 = new SnmpMibString();
    rootOrphan = new DefaultMutableTreeNode(snmpmibString2);
    treeHash = new SnmpMibTreeHash();
    treeHash.put(snmpmibString1.name, rootNode);
    variableHash = new SnmpMibTreeHash();
    orphanHash = new SnmpMibTreeHash();
    orphanNodes = new Vector();
    mibs = new Vector();
    clearError();
  }

  public DefaultMutableTreeNode getRootNode() {
    return rootNode;
  }

  public boolean addFile(String s) {
    if (s == null) {
      return false;
    }
    File file = new File(s);
    if (!file.exists()) {
      return false;
    }
    else {
      mibs.add(s);
      return true;
    }
  }

  public boolean addDirectory(String s) {
    File file = new File(s);
    if (!file.isDirectory()) {
      return false;
    }
    File afile[] = file.listFiles();
    if (afile == null) {
      return false;
    }
    for (int i = 0; i < afile.length; i++) {
      mibs.add(afile[i].getPath());

    }
    return true;
  }

  public String[] getFiles() {
    try {
      Enumeration enumeration = mibs.elements();
      String as[] = new String[mibs.size()];
      int i = 0;
      while (enumeration.hasMoreElements()) {
        as[i++] = (String) enumeration.nextElement();
      }
      clearError();
      return as;
    }
    catch (Exception exception) {
      setError("Error in getting filenames..\n" + exception.toString());
    }
    return null;
  }

  private void clearError() {
    errorMsg = "";
  }

  private void setError(String s) {
    errorMsg = s;
  }

  public JTree buildTree() {
    if (mibs.size() == 0) {
      setError("Error : Please add files first");
      return null;
    }
    else {
      getOID = new SnmpOid();
      Thread thread = new Thread(this);
      thread.setPriority(9);
      thread.start();
      treeRootNode.add(rootNode);
      tree = new JTree(treeRootNode);
      tree.putClientProperty("JTree.lineStyle", "Angled");
      tree.getSelectionModel().setSelectionMode(1);
      ImageIcon openicon = null;
      ImageIcon leaficon = null;
      ImageIcon closedicon = null;
      java.net.URL imgURL = NetworkManagerGUI.class.getResource(
          "icons/folder.gif");
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
      return tree;
    }
  }

  public void run() {
    Enumeration enumeration = mibs.elements();
    String s = "";
    Object obj = null;
    String s1;
    for (; enumeration.hasMoreElements(); loadFile(s1)) {
      s1 = (String) enumeration.nextElement();

    }
    updateOrphans();
  }

  private void loadFile(String s) {
    parseFile(s);
 }

  public boolean loadNewFile(String s) {
    if (s == null) {
      return false;
    }
    File file = new File(s);
    if (!file.exists()) {
      return false;
    }
    if (mibs.indexOf(s) == -1) {
      tree.collapsePath(tree.getSelectionPath());
      mibs.add(s);
      loadFile(s);
      updateOrphans();
      return true;
    }
    else {
      return false;
    }
  }

  private void updateOrphans() {
     Object obj = null;
    for (boolean flag = true; flag; ) {
      flag = false;
      for (Enumeration enumeration = orphanNodes.elements();
           enumeration.hasMoreElements(); ) {
        DefaultMutableTreeNode defaultmutabletreenode = (DefaultMutableTreeNode)
            enumeration.nextElement();
        if (addNode(defaultmutabletreenode)) {
          flag = true;
          orphanNodes.remove(defaultmutabletreenode);
        }
      }
    }

    getOID.buildOidToNameResolutionTable(rootNode);
    for (Enumeration enumeration1 = orphanNodes.elements();
         enumeration1.hasMoreElements(); ) {
      DefaultMutableTreeNode defaultmutabletreenode1 = (DefaultMutableTreeNode)
          enumeration1.nextElement();
      SnmpMibString snmpmibString = (SnmpMibString) defaultmutabletreenode1.
          getUserObject();
      if (snmpmibString.recordType != SnmpMibString.recVariable) {
        if (snmpmibString.recordType == SnmpMibString.recTable) {
          ;
        }
        else
        if (!treeHash.containsKey(snmpmibString.name)) {
          rootOrphan.add(defaultmutabletreenode1);
        }
      }
    }

    SnmpMibString snmpmibString1;
    if (tree != null && tree.getModel() != null) {
      ( (DefaultTreeModel) tree.getModel()).reload();
      tree.revalidate();
      tree.repaint();
    }
  }

  private int parseFile(String s) {
    SnmpMibParser snmpmibparser = new SnmpMibParser(s, this);
    return snmpmibparser.parseMibFile();
  }

  private boolean addRecord(SnmpMibString snmpmibString) {
    boolean flag = false;
    if (snmpmibString == null) {
      return false;
    }
    DefaultMutableTreeNode defaultmutabletreenode = new DefaultMutableTreeNode(
        snmpmibString);
    if (!addNode(defaultmutabletreenode)) {
      orphanNodes.add(defaultmutabletreenode);
      orphanHash.put(snmpmibString.name, defaultmutabletreenode);
      return false;
    }
    else {
      return true;
    }
  }

  private boolean addNode(DefaultMutableTreeNode defaultmutabletreenode) {
    SnmpMibString snmpmibString = (SnmpMibString) defaultmutabletreenode.
        getUserObject();
    DefaultMutableTreeNode defaultmutabletreenode1 = (DefaultMutableTreeNode)
        treeHash.get(snmpmibString.parent);
    if (defaultmutabletreenode1 == null) {
      return false;
    }
    SnmpMibString snmpmibString1 = (SnmpMibString) defaultmutabletreenode1.
        getUserObject();
    if (snmpmibString1.recordType > 0) {
      snmpmibString.recordType = snmpmibString1.recordType + 1;
    }
    DefaultMutableTreeNode defaultmutabletreenode2 = isChildPresent(
        defaultmutabletreenode);
    if (defaultmutabletreenode2 == null) {
      try {
        defaultmutabletreenode1.add(defaultmutabletreenode);
        defaultmutabletreenode.setParent(defaultmutabletreenode1);
        treeHash.put(snmpmibString.name, defaultmutabletreenode);
        return true;
      }
      catch (Exception exception) {
        //System.out.println("Error: " + snmpmibString.name + " " + snmpmibString.parent);
      }
      return false;
    }
    for (Enumeration enumeration = defaultmutabletreenode.children();
         enumeration.hasMoreElements(); ) {
      DefaultMutableTreeNode defaultmutabletreenode3 = (DefaultMutableTreeNode)
          enumeration.nextElement();
      if (isChildPresent(defaultmutabletreenode3) == null) {
        defaultmutabletreenode2.add(defaultmutabletreenode3);
      }
    }

    return true;
  }

  DefaultMutableTreeNode isChildPresent(DefaultMutableTreeNode
                                        defaultmutabletreenode) {
    SnmpMibString snmpmibString = (SnmpMibString) defaultmutabletreenode.
        getUserObject();
    return isChildPresent(snmpmibString);
  }

  DefaultMutableTreeNode isChildPresent(SnmpMibString snmpmibString) {
    DefaultMutableTreeNode defaultmutabletreenode = (DefaultMutableTreeNode)
        treeHash.get(snmpmibString.parent);
    if (defaultmutabletreenode == null) {
      defaultmutabletreenode = (DefaultMutableTreeNode) orphanHash.get(
          snmpmibString.parent);
    }
    if (defaultmutabletreenode == null) {
      return null;
    }
    Enumeration enumeration = defaultmutabletreenode.children();
    if (enumeration != null) {
      while (enumeration.hasMoreElements()) {
        DefaultMutableTreeNode defaultmutabletreenode1 = (
            DefaultMutableTreeNode) enumeration.nextElement();
        SnmpMibString snmpmibString1 = (SnmpMibString) defaultmutabletreenode1.
            getUserObject();
        if (snmpmibString1.name.equals(snmpmibString.name)) {
          return defaultmutabletreenode1;
        }
      }
    }
    return null;
  }

  public void newMibParseToken(SnmpMibString snmpmibString) {
    addRecord(snmpmibString);
  }

  public void parseMibError(String s) {
    //System.out.println(" Error parsing:" + s);
  }
}
