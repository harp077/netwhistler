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
package nnm;

import java.io.*;

import nnm.inet.ValidIP;

public class FPinger {
	public static String pingTm;
	static {
	}

	public boolean Fping(String host) {
		String cmd = "fping -a -r 1 -e -t " + NetworkManagerGUI.replyTime + " " + host;
		if (NetworkManagerGUI.customPing){
			if (NetworkManagerGUI.textHasContent(NetworkManagerGUI.cpingCommand))
				cmd = NetworkManagerGUI.cpingCommand + " " + host;
											
		}
		Runtime r = Runtime.getRuntime();
		Process p = null;
		try {
			p = r.exec(cmd);
			if (p == null) {
				NetworkManagerGUI.logger.info(" Can't run " + cmd);
				return false;
				// System.out.println("Cant't");
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(p
					.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				pingTm = "";
				if (NetworkManagerGUI.textHasContent(line)) {
					String[] tmp = line.split(" ");
						if (!NetworkManagerGUI.customPing)
						{
							pingTm = tmp[1];
							pingTm = pingTm.replaceAll("\\(","").replaceAll("\\)","");
						}
							else pingTm=line.trim();
															
					return true;
				} 

			}
		} catch (IOException ex1) {
			NetworkManagerGUI.logger.info(" Error executing " + cmd);
		}
		return false;
	}

	public boolean Fping3(String host) {
		boolean status = true;
		int ok = 0;
		int bad = 0;
		for (int i = 0; i < 3; i++) {
			if (Fping(host)) {
				ok++;
			} else {
				bad++;
			}
		}
		if (ok > bad) {
			status = true;
		} else {
			status = false;
		}
		return status;
	};
}
