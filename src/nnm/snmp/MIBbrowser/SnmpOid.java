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

import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;

public class SnmpOid {

	SnmpMibTreeHash oidResolveHash;

	SnmpOid() {
		oidResolveHash = new SnmpMibTreeHash();
	}

	public String getNodeOid(DefaultMutableTreeNode defaultmutabletreenode) {
		String s = "";
		SnmpMibString snmpmibString = (SnmpMibString) defaultmutabletreenode
				.getUserObject();
		if (snmpmibString.recordType == SnmpMibString.recVariable) {
			return snmpmibString.name + "-" + snmpmibString.syntax;
		}
		try {
			javax.swing.tree.TreeNode atreenode[] = defaultmutabletreenode
					.getPath();
			if (atreenode.length < 2) {
				return ".";
			}
			for (int i = 2; i < atreenode.length; i++) {
				SnmpMibString snmpmibString2 = (SnmpMibString) ((DefaultMutableTreeNode) atreenode[i])
						.getUserObject();
				s = s.concat("." + String.valueOf(snmpmibString2.number));
			}

		} catch (Exception exception) {
			return "Error in getting path: " + exception.toString();
		}
		if (snmpmibString.recordType == 3) {
			SnmpMibString snmpmibString1 = (SnmpMibString) ((DefaultMutableTreeNode) defaultmutabletreenode
					.getParent()).getUserObject();
		} else if (defaultmutabletreenode.isLeaf()) {
			s = s.concat(".0");
		} else {
			s = s.concat(".*");
		}
		return s;
	}

	public String getNodeOidQuery(DefaultMutableTreeNode defaultmutabletreenode) {
		String s = "";
		SnmpMibString snmpmibString = (SnmpMibString) defaultmutabletreenode
				.getUserObject();
		try {
			javax.swing.tree.TreeNode atreenode[] = defaultmutabletreenode
					.getPath();
			if (atreenode.length < 2) {
				return ".";
			}
			for (int i = 2; i < atreenode.length; i++) {
				SnmpMibString snmpmibString2 = (SnmpMibString) ((DefaultMutableTreeNode) atreenode[i])
						.getUserObject();
				s = s.concat("." + String.valueOf(snmpmibString2.number));
			}

		} catch (Exception exception) {
			// System.out.println("Error getting path: " +
			// exception.toString());
			return "";
		}
		if (snmpmibString.recordType == 3) {
			SnmpMibString snmpmibString1 = (SnmpMibString) ((DefaultMutableTreeNode) defaultmutabletreenode
					.getParent()).getUserObject();
			if (snmpmibString1.tableEntry == -1) {
				s = s.concat(".65535");
			} else {
				s = s.concat("." + String.valueOf(snmpmibString1.tableEntry));
			}
		} else if (snmpmibString.recordType == 2) {
			if (snmpmibString.tableEntry == -1) {
				s = s.concat(".1.1");
			} else {
				s = s.concat(".1." + String.valueOf(snmpmibString.tableEntry));
			}
		} else if (defaultmutabletreenode.isLeaf()) {
			s = s.concat(".0");
		} else {
			s = s.concat(".0");
		}
		return s;
	}

	public String getNodeOidActual(DefaultMutableTreeNode defaultmutabletreenode) {
		String s = "";
		SnmpMibString snmpmibString = (SnmpMibString) defaultmutabletreenode
				.getUserObject();
		try {
			javax.swing.tree.TreeNode atreenode[] = defaultmutabletreenode
					.getPath();
			if (atreenode.length < 2) {
				return ".";
			}
			for (int i = 2; i < atreenode.length; i++) {
				SnmpMibString snmpmibString1 = (SnmpMibString) ((DefaultMutableTreeNode) atreenode[i])
						.getUserObject();
				s = s.concat("." + String.valueOf(snmpmibString1.number));
			}

		} catch (Exception exception) {
			// System.out.println("Error getting path: " +
			// exception.toString());
			return "Can't resolve OID name";
		}
		return s;
	}

	void buildOidToNameResolutionTable(
			DefaultMutableTreeNode defaultmutabletreenode) {
		try {
			SnmpMibString snmpmibString;
			String s;
			for (Enumeration enumeration = defaultmutabletreenode
					.breadthFirstEnumeration(); enumeration.hasMoreElements(); oidResolveHash
					.put(s, snmpmibString.name)) {
				DefaultMutableTreeNode defaultmutabletreenode1 = (DefaultMutableTreeNode) enumeration
						.nextElement();
				snmpmibString = (SnmpMibString) defaultmutabletreenode1
						.getUserObject();
				s = getNodeOidActual(defaultmutabletreenode1);
			}

		} catch (Exception exception) {
			// System.out.println("Error in building OID table: " +
			// exception.toString());
		}
	}

	public String resolveOidName(String s) {
		String s1 = null;
		String s2 = "." + s.toString();
		try {
			for (s2 = s2.substring(0, s2.lastIndexOf('.')); s1 == null
					&& s2.length() > 2; s2 = s2.substring(0, s2
					.lastIndexOf('.'))) {
				s1 = (String) oidResolveHash.get(s2);

			}
			if (s1 == null) {
				return "***";
			}
		} catch (Exception exception) {
			// System.out.println("Error in Resolving OID Name :\n " +
			// exception.toString());
		}
		return s1 + s.substring(s.indexOf(".", s2.length() + 1));
	}

}
