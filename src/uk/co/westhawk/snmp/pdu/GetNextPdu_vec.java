// NAME
//      $RCSfile: GetNextPdu_vec.java,v $
// DESCRIPTION
//      [given below in javadoc format]
// DELTA
//      $Revision: 3.10 $
// CREATED
//      $Date: 2002/10/10 15:13:57 $
// COPYRIGHT
//      Westhawk Ltd
// TO DO
//

/*
 * Copyright (C) 1995, 1996 by West Consulting BV
 *
 * Permission to use, copy, modify, and distribute this software
 * for any purpose and without fee is hereby granted, provided
 * that the above copyright notices appear in all copies and that
 * both the copyright notice and this permission notice appear in
 * supporting documentation.
 * This software is provided "as is" without express or implied
 * warranty.
 * author <a href="mailto:snmp@westhawk.co.uk">Tim Panton</a>
 * original version by hargrave@dellgate.us.dell.com (Jordan Hargrave)
 */

/*
 * Copyright (C) 1996, 1997, 1998, 1999, 2000, 2001, 2002 by Westhawk Ltd
 * <a href="www.westhawk.co.uk">www.westhawk.co.uk</a>
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
 

package uk.co.westhawk.snmp.pdu;
import uk.co.westhawk.snmp.stack.*;
import java.lang.*;

/**
 * <p>
 * The GetNextPdu_vec class will ask for a number of objects (OIDs), based 
 * on the GetNext request.
 * </p>
 *
 * <p>
 * Specify with <em>addOid()</em> the OIDs that should be requested with this
 * Pdu request. No more than <em>count</em> (see constructor) should be added.
 * Add an Observer to the Pdu with <em>addObserver()</em>, and send the Pdu
 * with <em>send()</em>.
 * </p>
 *
 * <p>
 * If no exception occurred whilst receiving the response, the Object to the 
 * update() method of the Observer will be an array of
 * varbinds, so they may contains any AsnObject type.
 * If an exception occurred, that exception will be passed as the Object
 * to the update() method.
 * </p>
 *
 * @author <a href="mailto:snmp@westhawk.co.uk">Birgit Arkesteijn</a>
 * @version $Revision: 3.10 $ $Date: 2002/10/10 15:13:57 $
 * @see Pdu#addOid
 * @see Pdu#send
 * @see InterfaceGetNextPdu
 * @see OneGetNextPdu
 * @see varbind
 */
public class GetNextPdu_vec extends GetNextPdu 
{
    private static final String     version_id =
        "@(#)$Id: GetNextPdu_vec.java,v 3.10 2002/10/10 15:13:57 birgit Exp $ Copyright Westhawk Ltd";

    varbind[] value;

    /**
     * Constructor.
     *
     * @param con The context of the request
     * @param count The number of OIDs to be get
     */
    public GetNextPdu_vec(SnmpContextBasisFace con, int count) 
    {
        super(con);
        value = new varbind[count];
    }

    /**
     * The value of the request is set. This will be called by
     * Pdu.fillin().
     *
     * @param n the index of the value
     * @param a_var the value
     * @see Pdu#new_value 
     */
    protected void new_value(int n, varbind var) 
    {
        if (n <value.length) 
        {
            value[n] = var;
        }
    }

    /**
     * The methods notifies all observers. 
     * This will be called by Pdu.fillin().
     * 
     * <p>
     * If no exception occurred whilst receiving the response, the Object to the 
     * update() method of the Observer will be an array of
     * varbinds, so they may contains any AsnObject type.
     * If an exception occurred, that exception will be passed as the Object
     * to the update() method.
     * </p>
     */
    protected void tell_them()  
    {
        notifyObservers(value);
    }
}
