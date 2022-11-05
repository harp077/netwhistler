//
// This file is part of the OpenNMS(R) Application.
//
// OpenNMS(R) is Copyright (C) 2002-2003 The OpenNMS Group, Inc. All rights reserved.
// OpenNMS(R) is a derivative work, containing both original code, included code and modified
// code that was published under the GNU General Public License. Copyrights for modified
// and included code are below.
//
// OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
//
// Modifications:
//
// 2004 May 05: Switch from SocketChannel to Socket with connection timeout.
// 2003 Jul 21: Explicitly closed socket.
// 2003 Jul 18: Enabled retries for monitors.
// 2003 Jun 11: Added a "catch" for RRD update errors. Bug #748.
// 2003 Jan 31: Added the ability to imbed RRA information in poller packages.
// 2003 Jan 31: Cleaned up some unused imports.
// 2003 Jan 29: Added response times to certain monitors.
// 2002 Nov 14: Used non-blocking I/O socket channel classes.
//
// Original code base Copyright (C) 1999-2001 Oculan Corp. All rights reserved.
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//
// For more information contact:
//      OpenNMS Licensing <license@opennms.org>
//      http://www.opennms.org/
//      http://www.opennms.com/
//
// Tab Size = 8
//

package nnm.inet.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import nnm.EventPanel;
import nnm.NetworkManagerGUI;
import nnm.Node;


final public class monCitrix {

	private static final int DEFAULT_PORT = 1494;

	private static final int DEFAULT_RETRY = 0;

	private static final int DEFAULT_TIMEOUT = 3000;

	public static void checkCitrix(Node sNode) {

		int retry = DEFAULT_RETRY;
		int port = DEFAULT_PORT;
		int timeout = DEFAULT_TIMEOUT;

		if (timeout == 0)
			timeout = 10;

		int serviceStatus = 0;
		String host = sNode.getIP();
		for (int attempts = 0; attempts <= retry && serviceStatus != 1; attempts++) {
			Socket socket = null;
			try {
				// create a connected socket
				socket = new Socket();
				socket.connect(new InetSocketAddress(host, port), timeout);
				socket.setSoTimeout(timeout);
				// System.out.println("CitrixMonitor: connected to host: " + ip
				// + " on port: " + port);

				// We're connected, so upgrade status to unresponsive
				serviceStatus = 0;
			BufferedReader reader = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				StringBuffer buffer = new StringBuffer();
				while (serviceStatus != 1) {
					buffer.append((char) reader.read());
					if (buffer.toString().indexOf("ICA") > -1) {
						serviceStatus = 1;
					}
				}
			} catch (ConnectException cE) {
				// cE.fillInStackTrace();
				// System.out.println("CitrixPlugin: connection refused by host
				// " + ip);
				serviceStatus = 0;
			} catch (NoRouteToHostException e) {
				// No route to host!! No need to perform retries.
				// e.fillInStackTrace();
				// System.out.println("CitrixPlugin: Unable to test host " + ip
				// + ", no route available");
				serviceStatus = 0;
				break;
			} catch (InterruptedIOException e) {
				// System.out.println("CitrixMonitor: did not connect to host
				// within timeout: " + timeout + " attempt: " + attempts);
				serviceStatus = 0;
			} catch (IOException e) {
				// System.out.println("CitrixPlugin: Error communicating with
				// host " + ip);
				serviceStatus = 0;
			} catch (Throwable t) {
				// System.out.println("CitrixPlugin: Undeclared throwable
				// exception caught contacting host " + ip);
				serviceStatus = 0;
			} finally {
				try {
					if (socket != null) {
						socket.close();
						socket = null;
					}
				} catch (IOException e) {
				}
			}
		}
		//
		if (serviceStatus != 1) {
			boolean stat=sNode.getServiceStatus("CITRIX");
			sNode.setServiceStatus("CITRIX", false);
			if (stat){
			Date now = new Date();
			  Format formatter = new SimpleDateFormat("MM.dd.yyyy HH.mm.ss");
			  EventPanel.updateStatus( sNode.getIP() + "#"	+ sNode.getDNSname() +"#"+ formatter.format(now)+ "#CITRIX: Not Responding"); // log
			  NetworkManagerGUI.plogger.info(sNode.getIP() + " CITRIX: Not Responding");
			}
			} else {
			sNode.setServiceStatus("CITRIX", true);
		}

	}

}
