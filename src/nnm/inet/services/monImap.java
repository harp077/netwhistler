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


final public class monImap {

	private static final int DEFAULT_PORT = 143;

	private static final int DEFAULT_RETRY = 0;

	private static final int DEFAULT_TIMEOUT = 3000;

	private static String IMAP_START_RESPONSE_PREFIX = "* OK ";

	private static String IMAP_LOGOUT_REQUEST = "ONMSPOLLER LOGOUT\r\n";

	private static String IMAP_BYE_RESPONSE_PREFIX = "* BYE ";

	private static String IMAP_LOGOUT_RESPONSE_PREFIX = "ONMSPOLLER OK ";

	public static void checkImap(Node sNode) {

		int retry = DEFAULT_RETRY;
		int timeout = DEFAULT_TIMEOUT;
		int port = DEFAULT_PORT;
		String host = sNode.getIP();
		int serviceStatus = 0;
		for (int attempts = 0; attempts <= retry && serviceStatus != 1; attempts++) {
			Socket socket = null;
			try {

				socket = new Socket();
				socket.connect(new InetSocketAddress(host, port), timeout);
				socket.setSoTimeout(timeout);

				// We're connected, so upgrade status to unresponsive
				serviceStatus = 0;

				BufferedReader rdr = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));

				String banner = rdr.readLine();
				if (banner != null
						&& banner.startsWith(IMAP_START_RESPONSE_PREFIX)) {

					socket.getOutputStream().write(
							IMAP_LOGOUT_REQUEST.getBytes());
					String response = rdr.readLine();
					if (response != null
							&& response.startsWith(IMAP_BYE_RESPONSE_PREFIX)) {
						response = rdr.readLine();
						if (response != null
								&& response
										.startsWith(IMAP_LOGOUT_RESPONSE_PREFIX)) {
							serviceStatus = 1;

						}
					}
				}
				if (serviceStatus != 1) {
					serviceStatus = 0;
				}

			} catch (NoRouteToHostException e) {
				// e.fillInStackTrace();
				// System.out.println("ImapMonitor.poll: No route to host
				// exception for address: " + ip);
				break; // Break out of for(;;)
			} catch (ConnectException e) {
				// Connection refused. Continue to retry.
				//
				// e.fillInStackTrace();
				// System.out.println("ImapMonitor.poll: Connection exception
				// for address: " + ip);
			} catch (InterruptedIOException e) {
				// System.out.println("ImapMonitor: did not connect to host
				// within timeout: " + timeout + " attempt: " + attempts);
			} catch (IOException e) {
				// Ignore
				// e.fillInStackTrace();
				// System.out.println("ImapMonitor.poll: IOException while
				// polling address: " + ip);
			} finally {
				try {
					// Close the socket
					if (socket != null)
						socket.close();
				} catch (IOException e) {
					// e.fillInStackTrace();
					// System.out.println("ImapMonitor.poll: Error closing
					// socket.");
				}
			}
		}

		if (serviceStatus != 1) {
			boolean stat=sNode.getServiceStatus("IMAP");
			sNode.setServiceStatus("IMAP", false);
			if (stat){
				Date now = new Date();
				  Format formatter = new SimpleDateFormat("MM.dd.yyyy HH.mm.ss");
				  EventPanel.updateStatus(sNode.getIP() + "#"	+ sNode.getDNSname()+"#" + formatter.format(now)+  "#IMAP: Not Responding"); // log
				  NetworkManagerGUI.plogger.info(sNode.getIP() + " IMAP: Not Responding");	
			}
		} else {
			sNode.setServiceStatus("IMAP", true);
		}

	}

}