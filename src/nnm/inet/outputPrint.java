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
import javax.swing.*;

public class outputPrint implements Runnable {
	JTextArea displayPane;

	BufferedReader reader;

	private outputPrint(JTextArea displayPane, PipedOutputStream pos) {
		this.displayPane = displayPane;

		try {
			PipedInputStream pis = new PipedInputStream(pos);
			reader = new BufferedReader(new InputStreamReader(pis));
		} catch (IOException e) {
		}
	}

	public void run() {
		String line = null;

		try {
			while ((line = reader.readLine()) != null) {
				displayPane.replaceSelection(line + "\n");
				displayPane.setCaretPosition(displayPane.getDocument()
						.getLength());
			}

		} catch (IOException ioe) {
			// JOptionPane.showMessageDialog(null,
			// "Error redirecting output : "+ioe.getMessage());
		}
	}

	public static void redirectOutput(JTextArea displayPane) {
		outputPrint.redirectOut(displayPane);
		// outputPrint.redirectErr(displayPane);
	}

	public static void redirectOut(JTextArea displayPane) {
		PipedOutputStream pos = new PipedOutputStream();
		System.setOut(new PrintStream(pos, true));

		outputPrint console = new outputPrint(displayPane, pos);
		new Thread(console).start();
	}

	public static void redirectErr(JTextArea displayPane) {
		PipedOutputStream pos = new PipedOutputStream();
		System.setErr(new PrintStream(pos, true));

		outputPrint console = new outputPrint(displayPane, pos);
		new Thread(console).start();
	}

}
