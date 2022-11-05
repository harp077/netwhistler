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

import java.util.*;
import nnm.Node;
import uk.co.westhawk.snmp.stack.*;
import uk.co.westhawk.snmp.pdu.*;


public class getPorts
{

    final static String oid = "1.3.6.1.2.1.17.4.3.1.2";

    private SnmpContext context;
    private BlockPdu pdu;
    private static Vector ports;
    private Node sNode; 

public getPorts(Node aNode)
{
 sNode = aNode;	
 ports = new Vector();
}


public void init ()
{

      String host = sNode.getIP();

      int port = SnmpContextBasisFace.DEFAULT_PORT;
         String  socketType = SnmpContextBasisFace.STANDARD_SOCKET;


      String community = sNode.getRcommunity();


    try
    {
        context = new SnmpContext(host, port, socketType);


        pdu = new BlockPdu(context);
        pdu.setPduType(BlockPdu.GETNEXT);
        pdu.addOid(oid);
    }
    catch (java.io.IOException exc)
    {
        //System.out.println("IOException " + exc.getMessage());
        context.destroy();
    	
    }
    catch (Exception exc)
    {
        System.out.println("Exception " + exc.getMessage());
        context.destroy();
    }
}

public void start()
{
    boolean running=true;
    try
    {
        while (running)
        {
            varbind var = pdu.getResponseVariableBinding();
            if (pdu.getErrorStatus() == AsnObject.SNMP_ERR_NOERROR)
            {
                AsnObject obj = var.getValue();
                if (obj != null
                        &&
                    obj.getRespType() != AsnObject.SNMP_VAR_ENDOFMIBVIEW && obj.getRespTypeString().equals("ASN_INTEGER"))
                {
                   
                	AsnObjectId oid = var.getOid();
                  
                    ports.add(var.getValue());

                    pdu = new BlockPdu(context);
                    pdu.setPduType(BlockPdu.GETNEXT);
                    pdu.addOid(oid.toString());
                }
                else
                {
                    running = false;
                }
            }
            else
            {
                running = false;
            }
        }
    }
    catch (PduException exc)
    {
      //  exc.printStackTrace();
       // System.out.println("PduException: " + exc.getMessage());
        running = false;
    }
    catch (java.io.IOException exc)
    {
      //  exc.printStackTrace();
       // System.out.println("IOException: " + exc.getMessage());
        running = false;
    }
}

public static Vector getPortsNumbers(Node aNode)
{

    getPorts application = new getPorts(aNode);
    application.init();
    application.start();
   
    return ports;
}


}
