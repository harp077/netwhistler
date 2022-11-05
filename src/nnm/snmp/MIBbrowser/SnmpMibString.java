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
//
package nnm.snmp.MIBbrowser;

import java.io.Serializable;

public class SnmpMibString implements Serializable {

	/**
	 * 
	 */
	public static int recNormal = 0;

	public static int recVariable = -1;

	public static int recTable = 1;

	public String name;

	public String parent;

	public int number;

	public String description;

	public String access;

	public String status;

	public String syntax;

	public int recordType;

	public int tableEntry;

	public String index;

	SnmpMibString() {
		name = "";
		parent = "";
		number = 0;
		description = "";
		access = "";
		status = "";
		syntax = "";
		recordType = recNormal;
		tableEntry = -1;
		index = "";
		init();
	}

	public void init() {
		name = "";
		parent = "";
		number = 0;
		description = "";
		access = "";
		status = "";
		syntax = "";
		recordType = recNormal;
		index = "";
	}

	public String getCompleteString() {
		String s = new String("");
		s = s.concat("Name   : " + name + "\n");
		s = s.concat("Parent : " + parent + "\n");
		s = s.concat("Number : " + number + "\n");
		s = s.concat("Access : " + access + "\n");
		s = s.concat("Syntax : " + syntax + "");
		s = s.concat("Status : " + status + "\n");
		if (!index.equals("")) {
			s = s.concat("Index : " + index + "\n");
		}
		String s1 = "";
		int i;
		for (i = 50; i < s1.length(); i += 50) {
			s1 = s1 + description.substring(i - 50, i);
			s1 = s1 + "\n";
		}

		s1 = s1 + description.substring(i - 50);
		s = s.concat("Description :" + s1 + "\n");
		s = s.concat("Type :" + recordType + "\n");
		return s;
	}

	public String toString() {
		return name;
	}

}
