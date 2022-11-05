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

import nnm.EventPanel;
import nnm.NetworkManagerGUI;
import nnm.Node;


public class monSSH {
	private static final int DEFAULT_PORT = 22;

	private static final int DEFAULT_RETRY = 0;

	private static final int DEFAULT_TIMEOUT = 3000; // 3 second timeout on

	public static void checkSSH(Node sNode) {

		int retry = DEFAULT_RETRY;
		int timeout = DEFAULT_TIMEOUT;
		String host = sNode.getIP();
		int port = DEFAULT_PORT;
		String strBannerMatch = null;
		int serviceStatus = 0;
		for (int attempts = 0; attempts <= retry && serviceStatus != 1; attempts++) {
			Socket socket = null;
			try {
				socket = new Socket();
				socket.connect(new InetSocketAddress(host, port), timeout);
				socket.setSoTimeout(timeout);
				// System.out.print("connected to host\n");
				serviceStatus = 0;
				if (strBannerMatch == null || strBannerMatch.equals("*")) {
					serviceStatus = 1;
					break;
				}

				BufferedReader rdr = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				String response = rdr.readLine();
				if (response == null) {
					continue;
				}
				if (response.indexOf(strBannerMatch) > -1) {
					String cmd = "SSH-1.99-OpenNMS_1.1\r\n";
					socket.getOutputStream().write(cmd.getBytes());
					response = null;
					try {
						response = rdr.readLine();
					} catch (IOException e) {
					}
				}
				} catch (NoRouteToHostException e) {
				// e.fillInStackTrace();
				// System.out.print("No route to host\n");
				break; // Break out of for(;;)
			} catch (InterruptedIOException e) {
				// System.out.print("Can't connect to host within timeout: " +
				// timeout + " attempt: " + attempts+"\n");
			} catch (ConnectException e) {
				//e.fillInStackTrace();
				// System.out.print("Exception for address\n");
			} catch (IOException e) {
				// Ignore
				//e.fillInStackTrace();
				// System.out.print("IOException\n");
			} finally {
				try {
					if (socket != null) {
						socket.close();
					}
				} catch (IOException e) {
					//e.fillInStackTrace();
					// System.out.print("Error closing socket.\n");
				}
			}
		}

		//
		// return the status of the service
		//
		// System.out.print("Status:"+ serviceStatus);
		if (serviceStatus != 1) {
			boolean stat=sNode.getServiceStatus("SSH");	
			sNode.setServiceStatus("SSH",false);
			if (stat){
				Date now = new Date();
				  Format formatter = new SimpleDateFormat("MM.dd.yyyy HH.mm.ss");
				  EventPanel.updateStatus(sNode.getIP() + "#"	+ sNode.getDNSname()+"#" + formatter.format(now)+ "#SSH: Not Responding"); // log
				  NetworkManagerGUI.plogger.info(sNode.getIP() + " SSH: Not Responding");	
			}
		} else {
			sNode.setServiceStatus("SSH",true);
		}

	}

}
