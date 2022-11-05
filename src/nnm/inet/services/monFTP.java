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
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

import nnm.EventPanel;
import nnm.NetworkManagerGUI;
import nnm.Node;


public class monFTP {
	private static final int DEFAULT_PORT = 21;

	private static final int DEFAULT_RETRY = 0;

	private static final int DEFAULT_TIMEOUT = 3000; // 3 second timeout on

	// read()
	private static final String FTP_ERROR_530_TEXT = "User not logged in. Please login with USER and PASS first";

	private static final String FTP_ERROR_425_TEXT = "425 Session is disconnected.";

	public static RE MULTILINE = null;

	public static RE ENDMULTILINE = null;

	static {
		try {
			MULTILINE = new RE("^[0-9]{3}-");
		} catch (RESyntaxException ex) {
			throw new java.lang.reflect.UndeclaredThrowableException(ex);
		}
	}

	public static void checkFTP(Node sNode) {
		int retry = DEFAULT_RETRY;
		int port = DEFAULT_PORT;
		int timeout = DEFAULT_TIMEOUT;
		String host = sNode.getIP();
		String userid = null;
		String password = null;
		int serviceStatus = 0;
		for (int attempts = 0; attempts <= retry && serviceStatus != 1; attempts++) {
			Socket socket = null;
			try {
				socket = new Socket();
				socket.connect(new InetSocketAddress(host, port), timeout);
				socket.setSoTimeout(timeout);
				// System.out.print("connected to host\n");
				// We're connected, so upgrade status to unresponsive
				serviceStatus = 0;
				BufferedReader lineRdr = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				// check banner.
				String banner = lineRdr.readLine();
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
						banner = lineRdr.readLine();
					} while (banner != null && !ENDMULTILINE.match(banner));
					if (banner == null) {
						continue;
					}
				}

				StringTokenizer t = new StringTokenizer(banner);

				int rc = -1;
				try {
					rc = Integer.parseInt(t.nextToken());
				} catch (NumberFormatException nfE) {
					// nfE.fillInStackTrace();
					// System.out.print("Banner returned invalid result
					// code\n");

				}
				// Verify that return code is in proper range.
				if (rc >= 200 && rc <= 299) {
					// Attempt to login if userid and password available
					boolean bLoginOk = false;
					if (userid == null || userid.length() == 0
							|| password == null || password.length() == 0) {
						bLoginOk = true;
					} else {
						// send the use string
						//
						String cmd = "user " + userid + "\r\n";
						socket.getOutputStream().write(cmd.getBytes());

						// get the response code.
						//
						String response = null;
						do {
							response = lineRdr.readLine();
						} while (response != null && MULTILINE.match(response));
						if (response == null) {
							continue;
						}

						t = new StringTokenizer(response);
						rc = Integer.parseInt(t.nextToken());
						if (rc >= 200 && rc <= 399) {
							// send the password
							cmd = "pass " + password + "\r\n";
							response = lineRdr.readLine();
							if (response == null) {
								continue;
							}

							if (MULTILINE.match(response)) {
								String multiLineRC = new String(response
										.getBytes(), 0, 3)
										+ " ";
								try {
									ENDMULTILINE = new RE(multiLineRC);
								} catch (RESyntaxException ex) {
									throw new java.lang.reflect.UndeclaredThrowableException(
											ex);
								}
								do {
									response = lineRdr.readLine();
								} while (response != null
										&& !ENDMULTILINE.match(response));
								if (response == null) {
									continue;
								}
							}
							// System.out.print("return code: " +
							// response+"\n");
							t = new StringTokenizer(response);
							rc = Integer.parseInt(t.nextToken());
							if (rc >= 200 && rc <= 299) {
								// System.out.print("FtpMonitor.poll: Login
								// successful, parsed return code: " + rc+"\n");
								bLoginOk = true;
							} else {
								// System.out.print("Login failed\n");
								bLoginOk = false;
							}
						}
					}
					if (bLoginOk) {
						String cmd = "QUIT\r\n";
						socket.getOutputStream().write(cmd.getBytes());
						String response = lineRdr.readLine();
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
								response = lineRdr.readLine();
							} while (response != null
									&& !ENDMULTILINE.match(response));

							if (response == null) {
								continue;
							}
						}

						t = new StringTokenizer(response);
						rc = Integer.parseInt(t.nextToken());

						if (rc >= 200 && rc <= 299) {
							serviceStatus = 1;
						}

						else if (rc == 530
								&& response.indexOf(FTP_ERROR_530_TEXT) != -1) {
							serviceStatus = 1;

						}

						else if (rc == 425
								&& response.indexOf(FTP_ERROR_425_TEXT) != -1) {
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
				// System.out.print("Exception address\n");
			} catch (NoRouteToHostException e) {
				// e.fillInStackTrace();
				// System.out.print("No route to host\n");
				break;
			} catch (InterruptedIOException e) {
				// Ignore
				// System.out.print("Can't connect to host with timeout: " +
				// timeout + " attempt: " + attempts + "\n");
				break;
			} catch (ConnectException e) {
				// Connection refused. Continue to retry.
				// e.fillInStackTrace();
				// System.out.print("Connection exception for address\n");
			} catch (IOException e) {
				// Ignore
				// e.fillInStackTrace();
				// System.out.print("IOException\n");
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
		// return the status of the service
		// System.out.print("Status:" + serviceStatus);
		if (serviceStatus != 1) {
			boolean stat=sNode.getServiceStatus("FTP");
			sNode.setServiceStatus("FTP",false);
			if (stat){
				Date now = new Date();
				  Format formatter = new SimpleDateFormat("MM.dd.yyyy HH.mm.ss");
				  EventPanel.updateStatus( sNode.getIP() + "#"	+ sNode.getDNSname() +"#"+ formatter.format(now)+ "#FTP: Not Responding"); // log
				  NetworkManagerGUI.plogger.info(sNode.getIP() + " FTP: Not Responding");	
			}
		} else {
			sNode.setServiceStatus("FTP",true);
		}
	}

}
