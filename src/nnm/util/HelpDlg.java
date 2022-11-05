package nnm.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import nnm.NetworkManagerGUI;

public class HelpDlg extends JDialog {
	static final String TITLE = "NetWhistler Help";

	static final String HTML = "docs/netwhistler_howto.html";

	static final Dimension SIZE = new Dimension(600, 300);

	public HelpDlg(Frame parent) {
		super(parent, TITLE);
		constructUI();
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension optSize = getSize();
		if (optSize.height > screenSize.height) {
			optSize.height = screenSize.height;
		}
		if (optSize.width > screenSize.width) {
			optSize.width = screenSize.width;
		}
		setLocation((screenSize.width - optSize.width) / 2,
				(screenSize.height - optSize.height) / 2);
		setVisible(true);
	}

	private void constructUI() {
		Box box = Box.createVerticalBox();
		JTextPane textPane = new JTextPane();
		textPane.setContentType("text/html");
		textPane.setEditable(false);

		try {
			textPane.read(new FileReader(HTML), null);
		} catch (FileNotFoundException e1) {
			// e1.printStackTrace();
		} catch (IOException e1) {
			// e1.printStackTrace();
		}

		textPane.setCaretPosition(0);

		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.setPreferredSize(SIZE);
		scrollPane.setAlignmentX(0.5F);
		box.add(scrollPane);
		box.add(Box.createVerticalStrut(2));
		JButton okButton = new JButton("Close");
		okButton.setBackground(NetworkManagerGUI.sysBackColor);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ok();
			}
		});
		okButton.setAlignmentX(0.5F);
		okButton.setFont(NetworkManagerGUI.baseFont);
		box.add(okButton);
		box.add(Box.createVerticalStrut(2));
		getContentPane().add(box);
		getRootPane().setDefaultButton(okButton);
	}

	private void ok() {
		close();
	}

	private void close() {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
}
