// NAME
//      $RCSfile: NTSharedResBean.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 1.10 $
// CREATED
//      $Date: 2002/10/10 15:13:56 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//

/*
 * Copyright (C) 1998, 1999, 2000, 2001, 2002 by Westhawk Ltd (www.westhawk.co.uk)
 *
 * Permission to use, copy, modify, and distribute this software
 * for any purpose and without fee is hereby granted, provided
 * that the above copyright notices appear in all copies and that
 * both the copyright notice and this permission notice appear in
 * supporting documentation.
 * This software is provided "as is" without express or implied
 * warranty.
 * author <a href="mailto:snmp@westhawk.co.uk">Tim Panton</a>
 */

package uk.co.westhawk.snmp.beans;

import uk.co.westhawk.snmp.stack.*;
import uk.co.westhawk.snmp.pdu.*;
import java.awt.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import java.io.*;
import java.beans.*;

/**
 * <p>
 * This bean collects the names of the shared resources installed on
 * a NT server. The NT mib is described in the 
 *
 * <a href="http://premium.microsoft.com/msdn/library/winresource/dnwinnt/f1d/d25/s86a2.htm">LAN Manager MIB II for Windows NT Objects</a> .
 *
 * You will have to register to the MSDN before accessing this page.
 * </p>
 *
 * <p>
 * The properties in the parent classes should be set, before calling
 * the action() method. Via a PropertyChangeEvent the application/applet
 * will be notified. 
 * </p>
 *
 * @see SNMPBean#setHost
 * @see SNMPBean#setPort
 * @see SNMPBean#setCommunityName
 * @see SNMPRunBean#setUpdateInterval
 * @see SNMPBean#addPropertyChangeListener
 * @see SNMPBean#action
 * @see GetNextPdu
 * 
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 1.10 $ $Date: 2002/10/10 15:13:56 $
 *
 */
public class NTSharedResBean extends SNMPRunBean implements Observer
{
    private static final String     version_id =
        "@(#)$Id: NTSharedResBean.java,v 1.10 2002/10/10 15:13:56 birgit Exp $ Copyright Westhawk Ltd";

    public final static String svShareName = "1.3.6.1.4.1.77.1.2.27.1.1";

    private int           svShareName_len;
    private OneGetNextPdu pdu;
    private Hashtable     resourceHash;

    private boolean       isGetNextInFlight;
    private Date          lastUpdateDate = null;


/**
 * The default constructor.
 */
public NTSharedResBean() 
{
    resourceHash = new Hashtable();
    svShareName_len = svShareName.length();
}

/**
 * The constructor that will set the host and the port no.
 *
 * @param h the hostname
 * @param p the port no
 * @see SNMPBean#setHost
 * @see SNMPBean#setPort
 */
public NTSharedResBean(String h, int p) 
{
    this();
    setHost(h);
    setPort(p);
}

/**
 * Returns the date of the moment when this bean was last updated.
 * This might be null when the first time the update was not finished.
 *
 * @return the last update date
 */
public Date getLastUpdateDate()
{
    return lastUpdateDate;
}

/**
 * Returns the indices of the NT shared resources.
 * The OID of this shared resource is a concatenation of the 
 * name (svShareName) OID and the shared resource specific index. 
 * The index should be used to get the other properties of this resource.
 *
 * @see #getIndex(String)
 * @see #svShareName
 */
public Enumeration getIndices()
{
    return resourceHash.elements();
}

/**
 * Returns the index of one of the resources. 
 * The OID of this shared resource is a concatenation of the 
 * name (svShareName) OID and the shared resource specific index. 
 * The index should be used to get the other properties of this resource.
 *
 * @param name The name of the resource
 * @return the resource index, might be null if no resource with such name
 * exists
 * @see #getIndices
 * @see #getNames
 */
public String getIndex(String name)
{
    String ret = null;
    if (name != null)
    {
        ret = (String) resourceHash.get(name);
    }
    return ret;
}

/**
 * Returns the names of the NT shared resources (the list
 * of svShareName).
 */
public Enumeration getNames()
{
    return resourceHash.keys();
}

/**
 * Returns the number of NT shared resources.
 */
public synchronized int getCount()
{
    return resourceHash.size();
}

/**
 * This method starts the action of the bean. It will initialises 
 * all variables before starting.
 *
 * @see SNMPBean#action
 */
public void action()
{
    if (isHostPortReachable())
    {
        resourceHash.clear();
        lastUpdateDate = new Date();
        isGetNextInFlight = false;
        setRunning(true);
    }
}

/**
 * Implements the running of the bean.
 *
 * It will send the Pdu, if the previous one is not still in flight.
 *
 * @see SNMPRunBean#isRunning()
 */
public void run()
{
    while (context != null && isRunning())
    {
        if (isGetNextInFlight == false)
        {
            // start the GetNext loop again
            isGetNextInFlight = true;
            pdu = new OneGetNextPdu(context);
            pdu.addObserver(this);
            pdu.addOid(svShareName);
            try
            {
                pdu.send();
            }
            catch (PduException exc)
            {
                System.out.println("PduException " + exc.getMessage());
            }
            catch (IOException exc)
            {
                System.out.println("IOException " + exc.getMessage());
            }
        }

        try
        {
            Thread.sleep(interval);
        } 
        catch (InterruptedException ix)
        {
            ;
        }
    }
}

/**
 * This method is called when the Pdu response is received. When all
 * answers are received it will fire the property change event.
 *
 * The answers are stored in a hashtable, this is done because the speed
 * can only be calculated with the previous answer.
 *
 * @see SNMPBean#addPropertyChangeListener
 */
public void update(Observable obs, Object ov)
{
    varbind var;
    String hashKey;
    String oid, index, name;

    pdu = (OneGetNextPdu) obs;
    if (pdu.getErrorStatus() == AsnObject.SNMP_ERR_NOERROR)
    {
        var = (varbind) ov;
        oid = var.getOid().toString();
        if (oid.startsWith(svShareName))
        {
            // index is the part of the oid AFTER the svShareName
            index = oid.substring(svShareName_len+1);

            name = ((AsnOctets) var.getValue()).getValue();

            // update the hashtable with the new answer
            resourceHash.put(name, index);

            // perform the GetNext on the just received answer
            pdu = new OneGetNextPdu(context);
            pdu.addObserver(this);
            pdu.addOid(oid);
            try
            {
                pdu.send();
            }
            catch (PduException exc)
            {
                System.out.println("PduException " + exc.getMessage());
            }
            catch (IOException exc)
            {
                System.out.println("IOException " + exc.getMessage());
            }
        }
        else
        {
            // the GetNext loop has ended
            lastUpdateDate = new Date();
            isGetNextInFlight = false;
            firePropertyChange("resourceNames", null, null);
        }
    }
    else
    {
        // the GetNext loop has ended
        lastUpdateDate = new Date();
        isGetNextInFlight = false;
        firePropertyChange("resourceNames", null, null);
    }
}


}
