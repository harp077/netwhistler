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

import java.io.IOException;

public class OpenURL {
	private static final String UNIX_PATH = "mozilla";

	private static final String UNIX_FLAG = "-remote openURL";

	public static void displayURL(String url) {
		String cmd = null;
		try {
			cmd = UNIX_PATH + " " + UNIX_FLAG + "(" + url + ")";
			Process p = Runtime.getRuntime().exec(cmd);
			try {
				int exitCode = p.waitFor();
				if (exitCode != 0) {
					cmd = UNIX_PATH + " " + url;
					p = Runtime.getRuntime().exec(cmd);
				}

			} catch (InterruptedException x) {

			}
		}

		catch (IOException x) {

		}
	}

}
