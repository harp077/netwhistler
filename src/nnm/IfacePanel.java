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

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import nnm.Node;
import java.awt.BorderLayout;
import nnm.NetworkManagerGUI;
import nnm.inet.Service;
import javax.swing.table.TableColumn;
import javax.swing.JLabel;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import javax.swing.table.DefaultTableModel;


public class IfacePanel extends JPanel {

	public static JTable table;

	public static DefaultTableModel model;

	public IfacePanel() {
		super(new BorderLayout());
		model = new DefaultTableModel() {
			public boolean isCellEditable(int rowIndex, int mColIndex) {
				return false;
			}
		};
		table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// Create a couple of columns
		model.addColumn("ID");
		model.addColumn("IP Address");
		model.addColumn("DNS Name");
		model.addColumn("MAC Address");
		model.addColumn("NIC");
		model.addColumn("Node Type");
		model.addColumn("Status");
		for (int c = 1; c < table.getColumnCount(); c++) {
			TableColumn col = table.getColumnModel().getColumn(c);
			int width = 140;
			col.setMinWidth(width);
			col.setMaxWidth(width);
			col.setPreferredWidth(width);
		}
		int vColIndex = 0;
		TableColumn col = table.getColumnModel().getColumn(vColIndex);
		int width = 25;
		col.setPreferredWidth(width);
		vColIndex = 4;
		TableColumn colnic = table.getColumnModel().getColumn(vColIndex);
		width = 200;
		colnic.setMinWidth(width);
		colnic.setMaxWidth(width);
		colnic.setPreferredWidth(width);
		table.setFont(NetworkManagerGUI.smallFont);

		ListSelectionModel rowSM = table.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				ListSelectionModel lsm = (ListSelectionModel) e.getSource();
				if (!lsm.isSelectionEmpty()) {
					int selectedRow = lsm.getMinSelectionIndex();
					String ip = (String) model.getValueAt(selectedRow, 1);
					for (int i = 0; i < Graph.nodes.size(); i++) {
						Node aNode = (Node) Graph.nodes.get(i);
						if (ip.equals(aNode.getIP())){
							MapTree.selectNode(aNode);
							MapTree.expandTree();
						}	
					}
				}
			}
		});

		JTableHeader header = table.getTableHeader();

		add(header, BorderLayout.NORTH);
		add(table, BorderLayout.CENTER);
	}

	public static void updateStatus(Node aNode) {
		for (int i = 0; i < model.getRowCount(); i++) {
			String ip = (String) model.getValueAt(i, 1);
			// System.out.println("IP:"+ip);
			if (ip.equals(aNode.getIP())) {
				String NodeStat = aNode.getBadStatus() ? "DOWN" : "OK";
				String Status = "";
				String Mac = aNode.getMACaddress();
				if (!NetworkManagerGUI.textHasContent(Mac))
					Mac = "n/a";
				if (NodeStat.equals("DOWN"))
					Status = "<font color=red>" + NodeStat + "</font>";
				else
					Status = NodeStat;
				
				String Services = "";
				if (!NodeStat.equals("DOWN")) {
					for (int p = 0; p < aNode.getCheckPorts().size(); p++) {
						Service t = (Service) aNode.getCheckPorts().get(p);
						if (!t.getStatus()) {
							Status = "<font color=#FF9933>WARNING</font>";
						
						}	}
				}
				String NIC =aNode.getNIC();
				if (!NetworkManagerGUI.textHasContent(NIC))
					NIC = "n/a";
				model.setValueAt(aNode.getIP(), i, 1);
				model.setValueAt(aNode.getDNSname(), i, 2);
				model.setValueAt(Mac, i, 3);
				model.setValueAt(NIC, i, 4);
				model.setValueAt(aNode.getnodeType(), i, 5);
				model.setValueAt("<html>" + Status, i, 6);
			}
		}
		model.fireTableDataChanged();
		table.repaint();
	}

	public static void delNode(Node aNode) {
		for (int i = 0; i < model.getRowCount(); i++) {
			String ip = (String) model.getValueAt(i, 1);
			// System.out.println("IP:"+ip);
			if (ip.equals(aNode.getIP()))
				model.removeRow(i);
		}
	}

	public static void addNode(Node aNode) {
		String NodeStat = aNode.getBadStatus() ? "DOWN" : "OK";
		String Status = "";
		String Mac = aNode.getMACaddress();
		if (!NetworkManagerGUI.textHasContent(Mac))
			Mac = "n/a";
		if (NodeStat.equals("DOWN"))
			Status = "<font color=red>" + NodeStat + "</font>";
		else
			Status = NodeStat;
		
		
		if (!NodeStat.equals("DOWN")) {
			for (int p = 0; p < aNode.getCheckPorts().size(); p++) {
				Service t = (Service) aNode.getCheckPorts().get(p);
				if (!t.getStatus()) {
					Status = "<font color=#FF9933>WARNING</font>";
				
				}	}
		}
		String NIC =aNode.getNIC();
		if (!NetworkManagerGUI.textHasContent(NIC))
			NIC = "n/a";

		int id = model.getRowCount() + 1;
		String num = new Integer(id).toString();
		model.insertRow(model.getRowCount(), new Object[] { num, aNode.getIP(),
				aNode.getDNSname(), Mac, NIC, aNode.getnodeType(), 
				"<html>" + Status });
	}
	public static void clear() {
		while (table.getRowCount() > 0)
			((DefaultTableModel)table.getModel()).removeRow(0);
		model.fireTableDataChanged();
	}
	class MyCellRenderer extends JLabel implements TableCellRenderer {
		// This method is called each time a cell in a column
		// using this renderer needs to be rendered.
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus,
				int rowIndex, int vColIndex) {
			// 'value' is value contained in the cell located at
			// (rowIndex, vColIndex)

			if (isSelected) {
				// cell (and perhaps other cells) are selected
			}

			if (hasFocus) {
				// this cell is the anchor and the table has the focus
			}

			// Configure the component with the specified value
			setText(value.toString());
			setFont(NetworkManagerGUI.smallFont);

			// Set tool tip if desired
			setToolTipText((String) value);
			/*
			 * for (int i = 0; i < Graph.nodes.size(); i++) { Node aNode =
			 * (Node) Graph.nodes.get(i); if (getText().equals(aNode.getIP())){
			 * NetworkManagerGUI.imageURL =
			 * NetworkManagerGUI.class.getResource("images/" +
			 * aNode.getnodeType() + ".gif");
			 * 
			 * 
			 * 
			 * ImageIcon icon = new ImageIcon(NetworkManagerGUI.imageURL);
			 * Dimension newSize = new Dimension(32,32); Image image =
			 * icon.getImage(); image = image.getScaledInstance(newSize.width,
			 * newSize.height, Image.SCALE_DEFAULT); icon.setImage(image);
			 * 
			 * setIcon(icon); } }
			 * 
			 */
			// Since the renderer is a component, return itself
			return this;
		}

		// The following methods override the defaults for performance reasons
		public void validate() {
		}

		public void revalidate() {
		}

		protected void firePropertyChange(String propertyName, Object oldValue,
				Object newValue) {
		}

		public void firePropertyChange(String propertyName, boolean oldValue,
				boolean newValue) {
		}
	}

}
