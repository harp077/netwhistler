package nnm.inet.syslog;

import java.util.Date;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.io.IOException;

import nnm.NetworkManagerGUI;

public class Syslog {
	public static boolean SERVER;

	public Syslog() {

		Date now = new Date();
		Format formatter = new SimpleDateFormat("MM.dd.yyyy HH.mm.ss");
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(514);
			// create socket at agreed port
			byte[] buffer = new byte[1000];
			while (SERVER) {
				DatagramPacket request = new DatagramPacket(buffer,
						buffer.length);
				aSocket.receive(request);
				String packet_string = new String(buffer, 0, 0, request
						.getLength());
				SyslogConsole.logArea.append("[" + formatter.format(now) + "]"
						+ "   "
						+ request.getAddress().toString().replaceAll("\\/", "")
						+ " : " + packet_string + "\n");

			}
		} catch (SocketException e) {
			NetworkManagerGUI.logger.info(" Can't run syslog server: Socket busy");
			// System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			// System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null) {
				aSocket.close();
			}
		}

	}
}