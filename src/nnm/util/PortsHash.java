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
//
package nnm.util;

public class PortsHash 
	 implements Comparable
	{  
	   public PortsHash( int value,String key)
	   {  
	      this.key = key;
	      this.value = value;
	   }
	 
	   public String getKey() {return key;}
	 
	   public int getValue() {return value;}
	 
	   public int compareTo(Object otherObject)
	   {  
	      PortsHash other = (PortsHash)otherObject;
	      if (value < other.value) return -1;
	      if (value > other.value) return 1;
	      return 0;
	   }
	 
	   private String key;
	   private int value;
	}