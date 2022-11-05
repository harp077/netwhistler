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

import javax.mail.*;
import javax.mail.internet.*;

public class sendMail {
	public static void send(String smtpHost, int smtpPort, String from,
			String to, String subject, String content, boolean html)
			throws AddressException, MessagingException {
		// Create a mail session

		java.util.Properties props = new java.util.Properties();

		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", "" + smtpPort);
		Session session = Session.getInstance(props, null);

		// Construct the message
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(from));
		msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
		msg.setSubject(subject);
		if (html)
			msg.setContent(content, "text/html; charset=\"ISO-8859-1\"");
		else
			msg.setText(content);
		Transport.send(msg);

		// Send the message
		// Transport.send(msg);
	}
	// smtp check
	/*
	 * public static boolean testSMTP(String smtpHost) { Properties properties =
	 * System.getProperties(); properties.put("mail.smtp.host", smtpHost);
	 * Session session = Session.getInstance(properties, null);
	 * session.setDebug(true); try { Transport transport =
	 * session.getTransport("smtp"); transport.connect(); return true; } catch
	 * (Exception e) { return false; } }
	 */
}
