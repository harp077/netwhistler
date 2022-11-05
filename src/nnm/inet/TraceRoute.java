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
//
package nnm.inet;

import java.io.*;
import nnm.NetworkManagerGUI;
import nnm.Node;

public class TraceRoute {

	public static String IPaddr = "";

	public static boolean TRACE = false;

	public static Process p;

	public static Node aNode;

	public static StringBuffer result = null;

	public TraceRoute() {
	}

	public static void trace(Node sNode) throws IOException {
		aNode = sNode;
		IPaddr = aNode.getIP();
		TRACE = true;

		result = new StringBuffer();
		try {
			// Process p;
			NetworkManagerGUI.status.setText(" Tracing ...");
			NetworkManagerGUI.statusArea.append(" traceroute to " + IPaddr
					+ ", 30 hops max\n");
			p = Runtime.getRuntime().exec("traceroute -d -w 5" + " " + IPaddr);
			readResult(p.getInputStream());
			p.destroy();
		} catch (Exception e) {
			NetworkManagerGUI.logger.info(" Error executing traceroute");
		}
	}

	public static void readResult(InputStream in) {
		String line = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		try {

			while (TRACE && (line = br.readLine()) != null) {

				if (NetworkManagerGUI.textHasContent(line)) {
					NetworkManagerGUI.statusArea.append(" " + line + "\n");
				}
				NetworkManagerGUI.statusArea
						.setCaretPosition(NetworkManagerGUI.statusArea
								.getDocument().getLength());
				// boolean n =line.trim().endsWith(aNode.getIP());
				// if (n){
				String[] hops = line.trim().split(" ");
				String hop = hops[3].toString().replace('(', ' ').replace(')',
						' ');

				if (hop.trim().equals(aNode.getIP())) {
					if (NetworkManagerGUI.textHasContent(hops[0])) {
						aNode.setHops("Hops: " + hops[0]);
					}
				}

			}
			NetworkManagerGUI.manager.repaint();
			NetworkManagerGUI.statusArea.append(" trace complete\n");
			NetworkManagerGUI.status.setText(" ");

		} catch (IOException e) {
			// e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// e.printStackTrace();
			}
		}

	}

}
