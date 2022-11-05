//
// This file is part of the OpenNMS(R) Application.
//
// OpenNMS(R) is Copyright (C) 2002-2003 The OpenNMS Group, Inc.  All rights reserved.
// OpenNMS(R) is a derivative work, containing both original code, included code and modified
// code that was published under the GNU General Public License. Copyrights for modified
// and included code are below.
//
// OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
//
// Modifications:
//
// 2003 Jan 31: Added the ability to imbed RRA information in poller packages.
// 2003 Jan 29: Added response times to certain monitors.
//
// Original code base Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
//
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
//      OpenNMS Licensing       <license@opennms.org>
//      http://www.opennms.org/
//      http://www.opennms.com/
//

package nnm.inet.services;

import java.net.UnknownHostException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import nnm.EventPanel;
import nnm.NetworkManagerGUI;
import nnm.Node;
import jcifs.netbios.NbtAddress;

final public class monSMB {
	/**
	 * Default retries.
	 */
	private static final int DEFAULT_RETRY = 0;

	/**
	 * Default timeout. Specifies how long (in milliseconds) to block waiting
	 * for data from the monitored interface.
	 */
	private static final int DEFAULT_TIMEOUT = 3000;

	int serviceStatus = 0;

	public static void checkSMB(Node sNode) {
		int retry = DEFAULT_RETRY;
		int timeout = DEFAULT_TIMEOUT;
		String host = sNode.getIP();
		int serviceStatus = 0;
		NbtAddress nbtAddr = null;
		try {
			nbtAddr = NbtAddress.getByName(host);
			int nodeType = nbtAddr.getNodeType();
			// System.out.println("is: " + nbtAddr.toString());
			serviceStatus = 1;
		} catch (UnknownHostException uhE) {
			// System.out.println("poll: Unknown host exception generated for "
			// + ip + ", reason: " + uhE.getLocalizedMessage());
		} catch (RuntimeException rE) {
			// System.out.println("poll: Unexpected runtime exception " + rE);
		} catch (Exception e) {
			// System.out.println("poll: Unexpected exception" + e);
		}
		// return the status of the service
		//
		if (serviceStatus != 1) {
			boolean stat=sNode.getServiceStatus("SAMBA");	
			sNode.setServiceStatus("SAMBA", false);
			if (stat){
				Date now = new Date();
				  Format formatter = new SimpleDateFormat("MM.dd.yyyy HH.mm.ss");
				  EventPanel.updateStatus( sNode.getIP() + "#"	+ sNode.getDNSname()+"#" + formatter.format(now)+ "#SAMBA: Not Responding"); // log
				  NetworkManagerGUI.plogger.info(sNode.getIP() + " SAMBA: Not Responding");	
			}
		} else {
			sNode.setServiceStatus("SAMBA", true);
		}

	}
}
