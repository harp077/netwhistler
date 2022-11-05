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

package nnm.inet;

import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import nnm.FPinger;
import nnm.NetworkManagerGUI;

// get Mac address
public class getMac {
	private static String mac;

	public getMac() {
	}

	public static String getMACaddr(String hostIP) {
		mac="";
		FPinger pinger = new FPinger();
		String addr = hostIP;
		String cmd="arp -a";
		if (pinger.Fping(addr)) {
			BufferedReader in = null;
			try {
				if (NetworkManagerGUI.customMac){
					if (NetworkManagerGUI.textHasContent(NetworkManagerGUI.cmacCommand))
					cmd = NetworkManagerGUI.cmacCommand;
				}	
				Runtime r = Runtime.getRuntime();
				Process p = r.exec(cmd + " " + hostIP);
				if (p == null) {
					NetworkManagerGUI.logger.info(" Can't run " + cmd);
					return null;
					// System.out.println("Cant't");
				}
				in = new BufferedReader(new InputStreamReader(p
						.getInputStream()));
				String line;
				String macString = null;
				while ((line = in.readLine()) != null) {
					macString = line;
					if (!NetworkManagerGUI.customMac)
					{
						String[] tmp = macString.split(" ");
						macString = tmp[3];
					}
					
						// System.out.println(line);
				}
				in.close();
				// String tmp = macString.replaceAll("\\s+", " ");
				// String[] temp = tmp.split(" ");
				// String macAddressCandidate = temp[2].trim();
				if (IsMacAddress(macString)) {
					// mac = macAddressCandidate.replaceAll("-",
					// ":").toUpperCase();
					mac = macString;
				} else {
					mac = "";
				}

			} catch (IOException io) {
				NetworkManagerGUI.logger.info(" Error executing " + cmd);
			}
		} else {
			return null;
		}

		return mac;
	}

	private final static boolean IsMacAddress(String macAddressCandidate) {
		Pattern macPattern = Pattern
				.compile("[0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0 -9a-fA-F]{2}[-:][0-9a-fA-F]{2}");
		Matcher m = macPattern.matcher(macAddressCandidate);
		return m.matches();
	}
}
