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
import java.util.Hashtable;

public class SnmpMibTreeHash {

	Hashtable treeHash;

	SnmpMibTreeHash() {
		treeHash = new Hashtable();
	}

	public Object get(String s) {
		return treeHash.get(s);
	}

	public void put(String s, Object obj) {
		treeHash.put(s, obj);
	}

	public boolean containsKey(String s) {
		return treeHash.containsKey(s);
	}

	public Enumeration elements() {
		return treeHash.elements();
	}
}
