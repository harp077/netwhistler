package nnm.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import nnm.NetworkManagerGUI;

//  A dialogue box to confirm a user action.  A message is displayed,
//  and the user may choose to respond with either "Yes" or "No."
public class InputDialog extends JDialog implements ActionListener {

	// Use a boolean value to record the user's response.
	String input;
	JTextField tf;

	// Constructor function.
	public InputDialog(JDialog parent, String message, String title, int length) {

		// Create a "JDialog" box with the appropriate parent and title.
		super(parent, title, true);
		// Display the message.
		//setBackground(NetworkManagerGUI.sysBackColor);
		JPanel messagePane = new JPanel();
		JLabel mesLab = new JLabel(message);
		mesLab.setFont(NetworkManagerGUI.baseFont);
		messagePane.add(mesLab);
		tf = new FixedLengthTextField(length);
		messagePane.add(tf);
		getContentPane().add(messagePane);
		// Add "Yes" and "No" buttons.
		JPanel buttonPane = new JPanel();
		JButton y = new JButton("OK");
		y.setBackground(NetworkManagerGUI.sysBackColor);
		JButton n = new JButton("Cancel");
		n.setBackground(NetworkManagerGUI.sysBackColor);
		y.setFont(NetworkManagerGUI.baseFont);
		n.setFont(NetworkManagerGUI.baseFont);
		buttonPane.add(y);
		buttonPane.add(n);
		y.addActionListener(this);
		n.addActionListener(this);
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		pack();
		setResizable(false);
		setLocationRelativeTo(JOptionPane.getFrameForComponent(parent));
		setVisible(true);
	}

	// When the user selects "Yes" or "No"
	public void actionPerformed(ActionEvent e) {

		// Record his choice
		if (e.getActionCommand().equals("OK")) {
			input = tf.getText();
			setVisible(false);
		} else {
			// Then close the window.
			setVisible(false);
		}
	}

	// See which action the user has performed.
	public String getAction() {
		return input;
	}
}
