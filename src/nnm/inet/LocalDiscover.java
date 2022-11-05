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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;

import nnm.NetPanel;
import nnm.Network;
import nnm.NetworkManagerGUI;


public class LocalDiscover {

	public LocalDiscover() {
	
		Vector allnets = getNetworks();
		Dimension frameSize = Toolkit.getDefaultToolkit().getScreenSize();
        int d = frameSize.height;
        int z = frameSize.width;
        int x = 80;
        int y = 50;
		if (allnets.size() != 0) {
			for (int i = 0; i < allnets.size(); i++) {
				String nettmp = (String) allnets.get(i);
				  if((x += 140) + 48 > d)
	            {
	                x = 80;
	                y += 80;
	            }
				Network net = new Network(nettmp,new Point(x,y),"public",0,0);
				NetPanel.nets.add(net);
				}

		}
	}
	public Vector getNetworks() {
		   final Hashtable map = new Hashtable();
			map.put("255.0.0.0", "/8");
			map.put("255.128.0.0", "/9");
			map.put("255.192.0.0", "/10");
			map.put("255.224.0.0", "/11");
			map.put("255.240.0.0", "12");
			map.put("255.248.0.0", "/13");
			map.put("255.252.0.0", "/14");
			map.put("255.254.0.0", "/15");
			map.put("255.255.0.0", "/16");
			map.put("255.255.128.0", "/17");
			map.put("255.255.192.0", "/18");
			map.put("255.255.224.0", "/19");
			map.put("255.255.240.0", "/20");
			map.put("255.255.248.0", "/21");
			map.put("255.255.252.0", "/22");
			map.put("255.255.254.0", "/23");
			map.put("255.255.255.0", "/24");
			map.put("255.255.255.128", "/25");
			map.put("255.255.255.192", "/26");
			map.put("255.255.255.224", "/27");
			map.put("255.255.255.240", "/28");
			map.put("255.255.255.248", "/29");
			map.put("255.255.255.252", "/30");
			map.put("255.255.255.254", "/31");
		    BufferedReader in = null;
		   Vector nets = new Vector();
		  try {
					Runtime r = Runtime.getRuntime();
					Process p = r.exec("route -n");
					if (p == null) {
						NetworkManagerGUI.logger.info(" Cant't run route command");
						return null;
					}
					in = new BufferedReader(new InputStreamReader(p
							.getInputStream()));
					String line;
					String tmp = null;
					while ((line = in.readLine()) != null) {
						tmp =line.replaceAll(" {2,}", " ");	
						String[] nline = tmp.split(" ");
						if (!nline[0].equals("0.0.0.0") && ValidIP.isValidIp(nline[0])){
						String mask = (String) map.get(nline[2]);	
						nets.add(new String(nline[0]+mask));
						}
					}
					in.close();
				} catch (IOException io) {}
				return nets;
		}
	
}
