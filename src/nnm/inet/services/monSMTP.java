// OpenNMS(R) is Copyright (C) 2002-2003 The OpenNMS Group, Inc.  All rights reserved.
// OpenNMS(R) is a derivative work, containing both original code, included code and modified
// code that was published under the GNU General Public License. Copyrights for modified 
// and included code are below.
//
// OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
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
package nnm.inet.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

import nnm.EventPanel;
import nnm.NetworkManagerGUI;
import nnm.Node;

public class monSMTP {
	private static final int DEFAULT_PORT = 25;

	private static final int DEFAULT_RETRY = 0;

	private static final int DEFAULT_TIMEOUT = 3000;

	private static String LOCALHOST_NAME;

	private static RE MULTILINE = null;

	private static RE ENDMULTILINE = null;
	static {
		try {
			LOCALHOST_NAME = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException uhE) {
			// System.out.print("Failed to get localhost name");
			LOCALHOST_NAME = "localhost";
		}
		try {
			MULTILINE = new RE("^[0-9]{3}-");
		} catch (RESyntaxException ex) {
			throw new java.lang.reflect.UndeclaredThrowableException(ex);
		}
	}

	public static void checkSMTP(Node sNode) {

		int retry = DEFAULT_RETRY;
		int timeout = DEFAULT_TIMEOUT;

		int port = DEFAULT_PORT;
		String host = sNode.getIP();
		int serviceStatus = 0;
		for (int attempts = 0; attempts <= retry && serviceStatus != 1; attempts++) {
			Socket socket = null;
			try {
				// create a connected socket
				socket = new Socket();
				socket.connect(new InetSocketAddress(host, port), timeout);
				socket.setSoTimeout(timeout);

				// System.out.print("connected to host\n");
				serviceStatus = 1;
				BufferedReader rdr = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				String banner = rdr.readLine();
				if (banner == null) {
					continue;
				}
				if (MULTILINE.match(banner)) {
					String multiLineRC = new String(banner.getBytes(), 0, 3)
							+ " ";
					try {
						ENDMULTILINE = new RE(multiLineRC);
					} catch (RESyntaxException ex) {
						throw new java.lang.reflect.UndeclaredThrowableException(
								ex);
					}
					do {
						banner = rdr.readLine();
					} while (banner != null && !ENDMULTILINE.match(banner));
					if (banner == null) {
						continue;
					}
				}

				StringTokenizer t = new StringTokenizer(banner);
				int rc = Integer.parseInt(t.nextToken());
				if (rc == 220) {
					String cmd = "HELO " + LOCALHOST_NAME + "\r\n";
					socket.getOutputStream().write(cmd.getBytes());
					String response = rdr.readLine();
					if (response == null) {
						continue;
					}
					if (MULTILINE.match(response)) {
						String multiLineRC = new String(response.getBytes(), 0,
								3)
								+ " ";
						try {
							ENDMULTILINE = new RE(multiLineRC);
						} catch (RESyntaxException ex) {
							throw new java.lang.reflect.UndeclaredThrowableException(
									ex);
						}
						do {
							response = rdr.readLine();
						} while (response != null
								&& !ENDMULTILINE.match(response));
						if (response == null) {
							continue;
						}
					}

					t = new StringTokenizer(response);
					rc = Integer.parseInt(t.nextToken());
					if (rc == 250) {
						cmd = "QUIT\r\n";
						socket.getOutputStream().write(cmd.getBytes());
						response = rdr.readLine();
						if (response == null) {
							continue;
						}
						if (MULTILINE.match(response)) {
							String multiLineRC = new String(
									response.getBytes(), 0, 3)
									+ " ";

							try {
								ENDMULTILINE = new RE(multiLineRC);
							} catch (RESyntaxException ex) {
								throw new java.lang.reflect.UndeclaredThrowableException(
										ex);
							}

							do {
								response = rdr.readLine();
							} while (response != null
									&& !ENDMULTILINE.match(response));
							if (response == null) {
								continue;
							}
						}

						t = new StringTokenizer(response);
						rc = Integer.parseInt(t.nextToken());

						if (rc == 221) {
							serviceStatus = 1;
						}
					}
				}

				if (serviceStatus != 1) {
					serviceStatus = 0;
				}
			} catch (NumberFormatException e) {
				// Ignore
				// e.fillInStackTrace();
				// System.out.print("Exception\n");
			} catch (NoRouteToHostException e) {
				// e.fillInStackTrace();
				// System.out.print("No route to host\n");
				break; // Break out of for(;;)
			} catch (InterruptedIOException e) {
				// System.out.print("Can't connect to host within timeout: " +
				// timeout + " attempt: " + attempts + "\n");
			} catch (ConnectException e) {
				// Connection refused. Continue to retry.
				//
				// e.fillInStackTrace();
				// System.out.print("Connection exception for address\n");
			} catch (IOException e) {
				// Ignore
				// e.fillInStackTrace();
				// System.out.print("IOException for address\n");
			} finally {
				try {
					// Close the socket
					if (socket != null) {
						socket.close();
					}
				} catch (IOException e) {
					// e.fillInStackTrace();
					// System.out.print("Error closing socket.\n");
				}
			}
		}

		//
		// return the status of the service
		//
		// System.out.print("Status:" + serviceStatus);
		// System.out.print("Status:" + serviceStatus);
		if (serviceStatus != 1) {
			boolean stat=sNode.getServiceStatus("SMTP");	
			sNode.setServiceStatus("SMTP",false);
			if (stat){
				Date now = new Date();
				  Format formatter = new SimpleDateFormat("MM.dd.yyyy HH.mm.ss");
				  EventPanel.updateStatus(sNode.getIP() + "#"	+ sNode.getDNSname()+"#" +formatter.format(now)+  "#SMTP: Not Responding"); // log
				  NetworkManagerGUI.plogger.info(sNode.getIP() + " SMTP: Not Responding");	
			}
		} else {
			sNode.setServiceStatus("SMTP",true);
		}

	}
}
