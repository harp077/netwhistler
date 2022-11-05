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
//
package nnm.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JLabel;

public class ElapsedTime extends Thread {
	JLabel ta;

	SimpleDateFormat formatter;

	/* The constructor of the class */
	public ElapsedTime(JLabel tb) {
		ta = tb;
		formatter = new SimpleDateFormat("mm:ss");
	}

	/* Main method of the thread */
	public void run() {
		Date start_ = new Date();
		/* loop forever */
		while (true) {
			try {
				sleep(100); /* Thanks Andrew! */
			} catch (InterruptedException e) {
			}
			/* Refresh display of time on screen */
			Date now = new Date();
			long nMillis = now.getTime() - start_.getTime();

			long nHours = nMillis / 1000 / 60 / 60;
			nMillis -= nHours * 1000 * 60 * 60;

			long nMinutes = nMillis / 1000 / 60;
			nMillis -= nMinutes * 1000 * 60;

			long nSeconds = nMillis / 1000;
			nMillis -= nSeconds * 1000;

			StringBuffer time = new StringBuffer();
			if (nHours > 0)
				time.append(nHours + ":");
			if (nHours > 0 && nMinutes < 10)
				time.append("0");
			time.append(nMinutes + ":");
			if (nSeconds < 10)
				time.append("0");
			time.append(nSeconds);

			ta.setText(" Elapsed Time " + time + " ");
		}
	}

}
