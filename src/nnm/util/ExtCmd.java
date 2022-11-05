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

import javax.swing.JDialog;

import nnm.NetworkManagerGUI;

public class ExtCmd {
public String CmdName="";
public String Cmd="";
public String CmdArgs="";
public static String IPaddress="";

  public ExtCmd() {
    this("", "", "");
  }

  public ExtCmd(String aCmdName, String aCmd, String aCmdArgs) {
    CmdName=aCmdName;
    Cmd = aCmd;
    CmdArgs = aCmdArgs;

  }
 public void setCmdName(String s) {
   CmdName=s;
 }
 public void setCmd(String s) {
   Cmd=s;
 }
 public void setCmdArgs(String s) {
   CmdArgs=s;
 }

  public String getCmdName() {
    return CmdName;
  }
  public String getCmd() {
     return Cmd;
   }
   public String getCmdArgs() {
      return CmdArgs;
    }
  public void Execute(){
	  Runtime r = Runtime.getRuntime();
		int ok = 0;
	    Process p = null;
		try {
			p = r.exec(Cmd + " " + CmdArgs.replaceAll("%IP",IPaddress));
			if (p == null) {
				ok =1;
			}
  } catch (Exception ex){
	  NetworkManagerGUI.logger.info(" Error executing " + Cmd); 
	 ok =1; 
  }
  if (ok == 1) {
		JDialog tmp = new JDialog();
		new MessageDialog(tmp, " Can't run " + Cmd, "Execute command");
	}
  }
}