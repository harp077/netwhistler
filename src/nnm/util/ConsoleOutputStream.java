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
package nnm.util;

import java.io.*;
import javax.swing.text.*;

public class ConsoleOutputStream extends OutputStream {
	private Document document = null;

	private ByteArrayOutputStream outputStream = new ByteArrayOutputStream(256);

	private PrintStream ps = null;

	public ConsoleOutputStream(Document document, PrintStream ps) {
		this.document = document;
		this.ps = ps;
	}

	public void write(int b) {
		outputStream.write(b);
	}

	public void flush() throws IOException {
		super.flush();

		try {
			if (document != null) {
				document.insertString(document.getLength(), new String(
						outputStream.toByteArray()), null);
			}

			if (ps != null) {
				ps.write(outputStream.toByteArray());
			}

			outputStream.reset();
		} catch (Exception e) {
		}
	}
}
