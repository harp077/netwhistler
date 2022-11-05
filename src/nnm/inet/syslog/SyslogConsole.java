package nnm.inet.syslog;

import java.awt.*;
import javax.swing.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import nnm.NetworkManagerGUI;
import nnm.util.MapFilter;

import java.awt.event.ActionListener;
import javax.swing.border.EtchedBorder;
import java.awt.event.ActionEvent;

public class SyslogConsole extends JComponent

{

	public static JTextArea logArea;

	public static DatagramSocket socket;

	public SyslogConsole() {

	}

	public void init() {
		final JFrame frame = new JFrame();
		frame.setTitle("NetWhistler Syslog Console");
		frame.setSize(600, 300);
		// frame.setResizable(false);
		java.net.URL imageURL = NetworkManagerGUI.class
				.getResource("icons/nw.gif");
		ImageIcon frameIcon = new ImageIcon(imageURL);
		Image image = frameIcon.getImage();
		frame.setIconImage(image);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension optSize = frame.getSize();
		if (optSize.height > screenSize.height) {
			optSize.height = screenSize.height;
		}
		if (optSize.width > screenSize.width) {
			optSize.width = screenSize.width;
		}
		frame.setLocation((screenSize.width - optSize.width) / 2,
				(screenSize.height - optSize.height) / 2);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
		JPanel scanPanel = new JPanel();
		scanPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		scanPanel.setLayout(new BorderLayout());
		logArea = new JTextArea(20, 50);
		logArea.setEditable(false);
		logArea.setBackground(new Color(221, 221, 221));
		logArea.setCaretPosition(logArea.getDocument().getLength());
		JScrollPane scroll = new JScrollPane(logArea);
		scanPanel.add(scroll);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton save = new JButton("Save");
		save.setBackground(NetworkManagerGUI.sysBackColor);
		buttonPanel.add(save);
		save.setFont(NetworkManagerGUI.baseFont);
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				NetworkManagerGUI.recursivelySetFonts(fc,
						NetworkManagerGUI.baseFont);
				fc.addChoosableFileFilter(new MapFilter("txt",
						NetworkManagerGUI.progName + " logs"));
				fc.setAcceptAllFileFilterUsed(false);
				int retval = fc.showSaveDialog(null);
				if (retval == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					try {
						PrintWriter aFile;
						String newFile = file.toString();
						if (newFile.endsWith("txt")) {
							aFile = new PrintWriter(new FileWriter(file));
						} else {
							aFile = new PrintWriter(new FileWriter(file
									+ ".txt"));
						}

						aFile.println(logArea.getText());

						aFile.close();
					} catch (IOException ex) {
					}
				}
			}
		});
		JButton clear = new JButton("Clear");
		clear.setBackground(NetworkManagerGUI.sysBackColor);
		buttonPanel.add(clear);
		clear.setFont(NetworkManagerGUI.baseFont);
		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logArea.setText(" ");

			}
		});

		JButton cancel = new JButton("Close");
		cancel.setBackground(NetworkManagerGUI.sysBackColor);
		buttonPanel.add(cancel);
		cancel.setFont(NetworkManagerGUI.baseFont);
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Syslog.SERVER = false;
				frame.setVisible(false);
				frame.dispose();
			}
		});
		container.add(scanPanel, BorderLayout.CENTER);
		container.add(buttonPanel, BorderLayout.SOUTH);
		frame.getContentPane().add(container);
		frame.setVisible(true);
		Syslog.SERVER = true;
		new startSyslog(1);

	}

}
