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
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import nnm.EventPanel;
import nnm.NetworkManagerGUI;
import nnm.Node;


public class monHTTP {
	private static final int DEFAULT_PORT = 80;

	private static final int DEFAULT_RETRY = 0;

	private static final String DEFAULT_URL = "/";

	private static final int DEFAULT_TIMEOUT = 3000; // 3 second timeout on

	public static void checkHTTP(Node sNode) {
		int retry = DEFAULT_RETRY;
		int timeout = DEFAULT_TIMEOUT;
		int port = DEFAULT_PORT;
		String url = DEFAULT_URL;

		int response = -1;
		String responseText = null;
		boolean bStrictResponse = (response > 99 && response < 600);
		String host = sNode.getIP();
		final String cmd = "GET " + url + " HTTP/1.0\r\nHost: " + host
				+ "\r\n\r\n";

		int serviceStatus = 0;
		int currentPort = -1;
		int portIndex;
		for (int attempts = 0; attempts <= retry && serviceStatus != 1; attempts++) {
			Socket socket = null;
			try {
				socket = new Socket();
				socket.connect(new InetSocketAddress(host, port), timeout);
				socket.setSoTimeout(timeout);

				// System.out.print("connected to host: " + host +"\n");

				// We're connected, so upgrade status to unresponsive
				serviceStatus = 0;
				socket.getOutputStream().write(cmd.getBytes());
				BufferedReader lineRdr = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				String line = lineRdr.readLine();
				if (line == null) {
					continue;
				}

				if (line.startsWith("HTTP/")) {
					StringTokenizer t = new StringTokenizer(line);
					t.nextToken();

					int rVal = -1;
					try {
						rVal = Integer.parseInt(t.nextToken());
					} catch (NumberFormatException nfE) {
						// System.out.print("Error converting response code "
						// + line + "\n");
					}

					if (bStrictResponse && rVal == response) {
						serviceStatus = 1;
					} else if (!bStrictResponse && rVal > 99 && rVal < 500
							&& (url.equals(DEFAULT_URL))) {
						serviceStatus = 1;
					} else if (!bStrictResponse && rVal > 99 && rVal < 400) {
						serviceStatus = 1;
					} else {
						serviceStatus = 0;
					}
				}
				if (serviceStatus == 1 && responseText != null
						&& responseText.length() > 0) {
					do {
						line = lineRdr.readLine();

					} while (line != null && line.length() != 0);
					if (line == null) {
						continue;
					}
					boolean bResponseTextFound = false;
					do {
						line = lineRdr.readLine();

						if (line != null) {
							int responseIndex = line.indexOf(responseText);
							if (responseIndex != -1) {
								bResponseTextFound = true;
							}
						}

					} while (line != null && !bResponseTextFound);

					if (!bResponseTextFound) {
						serviceStatus = 0;
					}
				}
			} catch (NoRouteToHostException e) {
				// e.fillInStackTrace();
				// System.out.print("No route to host\n");
				break; // Break out of inner for(;;)
			} catch (InterruptedIOException e) {
				// Ignore
				// System.out.print("Can't connect to host within timeout: " +
				// timeout + " attempt: " + attempts+"\n");
			} catch (ConnectException e) {
				// Connection Refused. Continue to retry.
				//
				//e.fillInStackTrace();
				// System.out.print("Connection exception\n");

			} catch (IOException e) {
				// Ignore
				//
				//e.fillInStackTrace();
				// System.out.print("IOException\n");
			} finally {
				try {
					// Close the socket
					if (socket != null) {
						socket.close();
					}
				} catch (IOException e) {
					//e.fillInStackTrace();
					// System.out.print("Error closing socket\n");
				}

			} // end for (attempts)
		} // end for (ports)

		//
		// return the status of the service
		//
		// System.out.print("Status:"+serviceStatus);
		// System.out.print("Status:" + serviceStatus);
		if (serviceStatus != 1) {
			boolean stat=sNode.getServiceStatus("HTTP");
			sNode.setServiceStatus("HTTP",false);
			if (stat){
				Date now = new Date();
				  Format formatter = new SimpleDateFormat("MM.dd.yyyy HH.mm.ss");
				  EventPanel.updateStatus(sNode.getIP() + "#"	+ sNode.getDNSname() +"#"+ formatter.format(now)+  "#HTTP: Not Responding"); // log
				  NetworkManagerGUI.plogger.info(sNode.getIP() + " HTTP: Not Responding");	
			}
		} else {
			sNode.setServiceStatus("HTTP",true);
		}

	}

}
