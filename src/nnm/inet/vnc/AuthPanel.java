//
//  Copyright (C) 1999 AT&T Laboratories Cambridge.  All Rights Reserved.
//
//  This is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 2 of the License, or
//  (at your option) any later version.
//
//  This software is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this software; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
//  USA.
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
package nnm.inet.vnc;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;



import nnm.NetworkManagerGUI;

//
// The panel which implements the user authentication scheme
//

class AuthPanel extends JPanel implements ActionListener {

  JLabel title, retry, prompt;
  JPasswordField password;
  JButton ok;

  //
  // Constructor.
  //

  public AuthPanel() {
	  setLayout(new GridBagLayout());
	    //setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		GridBagConstraints c = new GridBagConstraints();
	    c.insets = new Insets(1, 1, 1, 1);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
    title = new JLabel("VNC Authentication");
    title.setFont(NetworkManagerGUI.baseFont);
    c.gridx = 0;
	c.gridy = 2;
    add(title,c);
    prompt = new JLabel("Password: ");
    prompt.setFont(NetworkManagerGUI.baseFont);
    c.gridx = 0;
	c.gridy = 4;
    add(prompt,c);
    password = new JPasswordField(10);
    password.setForeground(Color.black);
    password.setBackground(Color.white);
    password.setEchoChar('*');
    password.addActionListener(this);
    c.gridx = 4;
	c.gridy = 4;
    add(password,c);
    ok = new JButton("OK");
    ok.setBackground(NetworkManagerGUI.sysBackColor);
    ok.setFont(NetworkManagerGUI.baseFont);
    c.gridx = 8;
	c.gridy = 4;
    add(ok,c);
    ok.addActionListener(this);
  }

  //
  // Move keyboard focus to the password text field object.
  //

  public void moveFocusToPasswordField() {
    password.requestFocus();
  }

  //
  // This method is called when a button is pressed or return is
  // pressed in the password text field.
  //

  public synchronized void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == password || evt.getSource() == ok) {
      password.setEnabled(false);
      notify();
    }
  }

  //
  // retry().
  //

  public void retry() {
    title.setText(" Sorry.Try again.");
    password.setEnabled(true);
    password.setText("");
    moveFocusToPasswordField();
  }

}
