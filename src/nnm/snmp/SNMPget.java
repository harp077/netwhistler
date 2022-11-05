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
package nnm.snmp;

import java.net.InetAddress;

import nnm.NetworkManagerGUI;
import nnm.Node;
import snmp.*;

public class SNMPget
{

    public SNMPget()
    {
    }

    public static String get(Node sNode, String oid)
    {
    	String value = null;
        try
        {
            InetAddress inetaddress = InetAddress.getByName(sNode.getIP());
            String s = sNode.getRcommunity();
            //System.out.println("Community:" + s);
            int i=0;
            SNMPv1CommunicationInterface snmpv1communicationinterface = new SNMPv1CommunicationInterface(i, inetaddress, s);
            String s1 = oid;
            //System.out.println("Retrieving value corresponding to OID " + s1);
            SNMPVarBindList snmpvarbindlist = snmpv1communicationinterface.getMIBEntry(s1);
            SNMPSequence snmpsequence = (SNMPSequence)snmpvarbindlist.getSNMPObjectAt(0);
            SNMPObjectIdentifier snmpobjectidentifier = (SNMPObjectIdentifier)snmpsequence.getSNMPObjectAt(0);
            SNMPObject snmpobject = snmpsequence.getSNMPObjectAt(1);
           // System.out.println("Retrieved value: " + snmpobject.toString());
            value = snmpobject.toString();
        }
        catch(Exception exception)
        {
        	NetworkManagerGUI.logger.info(" Error  during SNMP operation");
        	// System.out.println("Exception during SNMP operation:  " + exception + "\n");
        }
      return value;
    }
}
