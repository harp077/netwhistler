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


public class monPOP3 {

	private static final int DEFAULT_PORT = 110;

	private static final int DEFAULT_RETRY = 0;

	private static final int DEFAULT_TIMEOUT = 3000;

	public static void checkPOP3(Node sNode) {

		int retry = DEFAULT_RETRY;
		int port = DEFAULT_PORT;
		int timeout = DEFAULT_TIMEOUT;
		String host = sNode.getIP();
		int serviceStatus = 0;
		for (int attempts = 0; attempts <= retry && serviceStatus != 1; attempts++) {
			Socket socket = null;
			try {
				// create a connected socket
				socket = new Socket();
				socket.connect(new InetSocketAddress(host, port), timeout);
				socket.setSoTimeout(timeout);
				// System.out.print("connected\n");
				// We're connected, so upgrade status to unresponsive
				serviceStatus = 0;
				BufferedReader rdr = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				// Check : "+OK"
				String banner = rdr.readLine();
				if (banner == null) {
					continue;
				}
				StringTokenizer t = new StringTokenizer(banner);
				if (t.nextToken().equals("+OK")) {
					String cmd = "QUIT\r\n";
					socket.getOutputStream().write(cmd.getBytes());
					t = new StringTokenizer(rdr.readLine());
					if (t.nextToken().equals("+OK")) {
						serviceStatus = 1;
					}
				}
				if (serviceStatus != 1) {
					serviceStatus = 0;
				}
			} catch (NoRouteToHostException e) {
				// System.out.print("No route to host\n");
				break;
			} catch (InterruptedIOException e) {
				// System.out.print("Can't connect with timeout: " + timeout + "
				// attempt: " + attempts + "\n");
			} catch (ConnectException e) {
				// System.out.print("Can't connect to address " + host + "\n");
			} catch (IOException e) {
				// System.out.print("IOException for address " + host + "\n");
			} finally {
				try {
					// Close the socket
					if (socket != null) {
						socket.close();
					}
				} catch (IOException e) {
					// System.out.print("Error closing socket\n");
				}
			}
		}
		// return serviceStatus;
		// System.out.print("Status:" + serviceStatus);
		 //System.out.print("Status pop3:" + serviceStatus);
		if (serviceStatus != 1) {
			boolean stat=sNode.getServiceStatus("POP3");	
			sNode.setServiceStatus("POP3",false);
			if (stat){
				Date now = new Date();
				  Format formatter = new SimpleDateFormat("MM.dd.yyyy HH.mm.ss");
				  EventPanel.updateStatus( sNode.getIP() + "#"	+ sNode.getDNSname()+"#" + formatter.format(now)+ "#POP3: Not Responding"); // log
				  NetworkManagerGUI.plogger.info(sNode.getIP() + " POP3: Not Responding");	
			}
		} else {
			sNode.setServiceStatus("POP3",true);
		}

	}

}
