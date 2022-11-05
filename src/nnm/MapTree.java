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
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import nnm.util.MapTreeRenderer;
import nnm.util.report.showStatus;

// Referenced classes of package nnm:
//            NetworkManagerGUI, Node, Network, NetworkManager, 
//            Graph

public class MapTree extends JPanel
{

    public static DefaultMutableTreeNode rootNode;
    public static DefaultMutableTreeNode ipnet;
    public static DefaultMutableTreeNode net;
    public static DefaultTreeModel treeModel;
    public static JTree tree;
    public static JPopupMenu nodePopupMenu;
    public static Node sNode;
    public static int LOOK = 0;
    public static JComboBox looksBox;
    public static DefaultTreeCellRenderer renderer;
    public static TreeNode currentNode = null;

    public MapTree()
    {
        super(new GridLayout(1, 0));
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        rootNode = new DefaultMutableTreeNode("Root");
        ipnet = new DefaultMutableTreeNode("IP Network");
        rootNode.add(ipnet);
        treeModel = new DefaultTreeModel(rootNode);
        treeModel.setRoot(rootNode);
        tree = new JTree(treeModel);
        tree.putClientProperty("JTree.lineStyle", "Angled");
        tree.getSelectionModel().setSelectionMode(1);
        tree.setCellRenderer(new MapTreeRenderer());
        tree.setExpandsSelectedPaths(true);
        tree.setFont(NetworkManagerGUI.smallFont);
        tree.addMouseListener(new MouseAdapter() {

            DefaultMutableTreeNode node;

            public void mousePressed(MouseEvent e)
            {
            	
            	TreePath path = MapTree.tree.getPathForLocation(e.getX(), e.getY());
                MapTree.currentNode = null;
                updateUI();
                MapTree.sNode = null;
                if(path != null)
                {
                    node = (DefaultMutableTreeNode)path.getLastPathComponent();
                    if(node.isLeaf())
                    {
                    	
                    	Object obj = node.getUserObject();
                        for(int i = 0; i < Graph.nodes.size(); i++)
                        {
                            Node aNode = (Node)Graph.nodes.get(i);
                            if(aNode.isSelected())
                                aNode.toggleSelected();
                            if(obj.equals(aNode))
                            {
                                MapTree.sNode = aNode;
                                aNode.toggleSelected();
                                NetworkManager.currentNetwork = MapTree.sNode.getNetwork();
                                NetworkManagerGUI.topcards.show(NetworkManagerGUI.cardsPanel, "Third");
                                JViewport port = NetworkManagerGUI.jsp.getViewport();
                                int w = NetworkManagerGUI.splitPane.getWidth() / 2;
                                int h = NetworkManagerGUI.splitPane.getHeight() / 2;
                                int x = (int)aNode.getLocation().getX();
                                int y = (int)aNode.getLocation().getY();
                                int x1 = 0;
                                int y1 = 0;
                                if(x - w <= 0)
                                    x1 =0 ;
                                else
                                    x1 = x - w;
                                if(y - h <= 0)
                                    y1 = 0;
                                else
                                    y1 = y - h;
                                Point point = new Point(x1, y1);
                                NetworkManagerGUI.jsp.getViewport().setViewPosition(point);
                                NetworkManagerGUI.jsp.repaint();
                                
                                for (int s = 0; s < NetworkManagerGUI.statPanel.model.getRowCount(); s++) {
                    				String ip = (String) NetworkManagerGUI.statPanel.model
                    						.getValueAt(s, 1);
                    				if (ip.equals(sNode.getIP())) {
                    					// System.out.println("IP:"+ip + "i: " + i);
                    					NetworkManagerGUI.statPanel.table.getSelectionModel()
                    							.setSelectionInterval(s, i);
                    					NetworkManagerGUI.statPanel.table.scrollRectToVisible(NetworkManagerGUI.statPanel.table.getCellRect(s, 0, true));
                    				}
                    			}
                                for (int s = 0; s < NetworkManagerGUI.ifacePanel.model.getRowCount(); s++) {
                    				String ip = (String) NetworkManagerGUI.ifacePanel.model
                    						.getValueAt(s, 1);
                    				if (ip.equals(sNode.getIP())) {
                    					// System.out.println("IP:"+ip + "i: " + i);
                    					NetworkManagerGUI.ifacePanel.table.getSelectionModel()
                    							.setSelectionInterval(s, i);
                    					NetworkManagerGUI.ifacePanel.table.scrollRectToVisible(NetworkManagerGUI.ifacePanel.table.getCellRect(s, 0, true));
                    				}
                    			}
                    			for (int q = 0; q < NetworkManagerGUI.eventPanel.model.getRowCount(); q++) {
                    				String ip = (String) NetworkManagerGUI.eventPanel.model
                    						.getValueAt(q, 1);
                    				if (ip.equals(sNode.getIP())) {
                    					// System.out.println("IP:"+ip + "i: " + i);
                    					NetworkManagerGUI.eventPanel.table.getSelectionModel()
                    							.setSelectionInterval(q, i);
                    					NetworkManagerGUI.eventPanel.table.scrollRectToVisible(NetworkManagerGUI.eventPanel.table.getCellRect(q, 0, true));
                    				}
                    			}
                            }
                        }

                    } else
                    if(node.isRoot())
                        NetworkManagerGUI.topcards.show(NetworkManagerGUI.cardsPanel, "First");
                    else
                    if(node.equals(MapTree.ipnet))
                    {
                        NetworkManager.currentNetwork = node.toString();
                        NetworkManagerGUI.topcards.show(NetworkManagerGUI.cardsPanel, "Second");
                    } else
                    {
                        for(int i = 0; i < MapTree.ipnet.getChildCount(); i++)
                        {
                            TreeNode tnode = MapTree.ipnet.getChildAt(i);
                            if(node.equals(tnode))
                            {
                                NetworkManager.currentNetwork = tnode.toString();
                                NetworkManagerGUI.topcards.show(NetworkManagerGUI.cardsPanel, "Second");
                            }
                        }

                        if(MapTree.tree.isCollapsed(path))
                            MapTree.tree.expandPath(path);
                        else
                            MapTree.tree.collapsePath(path);
                    }
                }
                if (e.isPopupTrigger() || e.isMetaDown()) {
                    if (sNode != null)
                            nodePopupMenu.show(tree, e.getX(), e.getY());
                }

            }

        });
        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setPreferredSize(new Dimension(150, scrollPane.getHeight()));
        scrollPane.setMinimumSize(new Dimension(150, scrollPane.getHeight()));
        container.add(scrollPane, "Center");
        add(container);
        nodePopupMenu = new JPopupMenu();
        JMenuItem menuStatus = new JMenuItem("Status");
        JMenuItem menuConfig = new JMenuItem("Properties");
        menuConfig.setFont(NetworkManagerGUI.smallFont);
        nodePopupMenu.add(menuStatus);
        nodePopupMenu.add(menuConfig);
        menuConfig.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                NetworkManager.propertDialog(MapTree.sNode, false);
            }

        });
        menuStatus.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                new showStatus(MapTree.sNode);
            }

        });
    }

    public static void clear()
    {
        ipnet.removeAllChildren();
        refresh();
        currentNode = null;
    }

    public static void removeNode(Node sNode)
    {
        if(!sNode.getnodeType().equals("hub") && !sNode.getnodeType().equals("network-cloud"))
        {
            for(int i = 0; i < ipnet.getChildCount(); i++)
            {
                TreeNode tnet = ipnet.getChildAt(i);
                for(Enumeration e = tnet.children(); e.hasMoreElements();)
                {
                    TreeNode node = (TreeNode)e.nextElement();
                    if(sNode.toString().equals(node.toString()))
                        treeModel.removeNodeFromParent((MutableTreeNode)node);
                }

            }

            refresh();
        }
    }

    public static void removeNetwork(Network sNet)
    {
        for(int i = 0; i < ipnet.getChildCount(); i++)
        {
            TreeNode tnode = ipnet.getChildAt(i);
            if(sNet.getNetwork().equals(tnode.toString()))
                treeModel.removeNodeFromParent((MutableTreeNode)tnode);
        }

        refresh();
    }

    public static void addNode(DefaultMutableTreeNode parent, Object child)
    {
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
        if(parent == null)
            parent = rootNode;
        TreeNode root = ipnet;
        if(root.getChildCount() >= 0)
        {
            for(Enumeration e = root.children(); e.hasMoreElements();)
            {
                TreeNode n = (TreeNode)e.nextElement();
                if(n.toString().equals(NetworkManager.currentNetwork))
                    parent = (DefaultMutableTreeNode)n;
            }

        }
        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());
        refresh();
    }

    public static void selectNode(Node sNode)
    {
        if(!sNode.getnodeType().equals("hub") && !sNode.getnodeType().equals("network-cloud"))
        {
            int startRow = 0;
            String prefix = sNode.toString();
            TreePath path = tree.getNextMatch(prefix, startRow, javax.swing.text.Position.Bias.Forward);
            if(path != null)
            {
                currentNode = (TreeNode)path.getLastPathComponent();
                tree.setSelectionPath(new TreePath(((DefaultTreeModel)tree.getModel()).getPathToRoot(currentNode)));
            }
        }
        refresh();
    }

    public static void expandTree()
    {
        expandEntireTree((DefaultMutableTreeNode)treeModel.getRoot());
    }

    private static void expandEntireTree(TreeNode tNode)
    {
        TreePath tp = new TreePath(((DefaultMutableTreeNode)tNode).getPath());
        tree.expandPath(tp);
        for(int i = 0; i < tNode.getChildCount(); i++)
            expandEntireTree(tNode.getChildAt(i));

    }

    public static void refresh()
    {
        ArrayList expandedPaths = getExpandedPaths();
        treeModel.reload();
        expandPaths(expandedPaths);
    }

    public static ArrayList getExpandedPaths()
    {
        ArrayList expandedPaths = new ArrayList();
        TreePath tp;
        for(Enumeration eenum = tree.getExpandedDescendants(tree.getPathForRow(0)); eenum != null && eenum.hasMoreElements(); expandedPaths.add(tp))
            tp = (TreePath)eenum.nextElement();

        return expandedPaths;
    }

    public static void expandPaths(ArrayList pathlist)
    {
        for(int i = 0; i < pathlist.size(); i++)
            tree.expandPath((TreePath)pathlist.get(i));

    }

}
