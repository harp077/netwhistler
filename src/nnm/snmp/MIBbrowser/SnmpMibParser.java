// This file based on SNMP MIBbrowser Copyright (C) 2002  Dwipal A. Desai
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
package nnm.snmp.MIBbrowser;

import java.io.*;

public class SnmpMibParser {

	String fileName;

	SnmpMibString parsingString;

	String lastToken;

	String tokenVal;

	String tokenVal2;

	PipedReader pr;

	PipedWriter pw;

	BufferedReader r;

	FileReader inFile;

	public static int TT_EOL = 10;

	MibParserIface tokens;

	SnmpMibParser(String s, MibParserIface Mibparserinterface) {
		tokenVal = "xx";
		tokenVal2 = "";
		fileName = s;
		tokens = Mibparserinterface;
	}

	int parseMibFile() {
		int ok = 0;

		StreamTokenizer streamtokenizer;
		try {
			inFile = new FileReader(new File(fileName));
			r = new BufferedReader(inFile);
			streamtokenizer = new StreamTokenizer(r);
		} catch (Exception exception) {

			ok = 1;
			return -1;
		}
		streamtokenizer.resetSyntax();
		streamtokenizer.eolIsSignificant(true);
		streamtokenizer.wordChars(33, 126);
		int i = 0;
		boolean flag = false;
		try {
			while (getNextToken(streamtokenizer).trim().length() > 0
					|| streamtokenizer.ttype == TT_EOL) {
				String s4 = getTokenVal(streamtokenizer);
				switch (i) {
				default:
					break;

				case 0: // '\0'
					parsingString = new SnmpMibString();
					if (s4.indexOf("IMPORT") != -1) {
						i = 100;
						break;
					}
					if (s4.equals("MODULE-IDENTITY")) {
						parsingString.name = lastToken;
						i = 1;
						break;
					}
					if (s4.equals("OBJECT-TYPE")) {
						String s5 = new String(lastToken.trim());
						s5 = s5.substring(0, 1);
						if (s5.toLowerCase().equals(s5)) {
							i = 1;
							parsingString.name = lastToken;
						}
						break;
					}
					if (s4.indexOf("OBJECT-GROUP") != -1) {
						String s6 = new String(lastToken.trim());
						s6 = s6.substring(0, 1);
						if (s6.toLowerCase().equals(s6)) {
							i = 1;
							parsingString.name = lastToken;
						}
						break;
					}
					if (s4.equals("OBJECT")) {
						parsingString.name = lastToken;
						i = 2;
						break;
					}
					if (s4.equals("::=")) {
						parsingString.init();
						parsingString.name = lastToken;
						i = 9;
					}
					break;

				case 1: // '\001'
					if (s4.equals("::=")) {
						i = 3;
						break;
					}
					if (s4.equals("SYNTAX")) {
						i = 5;
						break;
					}
					if (s4.indexOf("ACCESS") != -1) {
						i = 6;
						break;
					}
					if (s4.equals("STATUS")) {
						i = 7;
						break;
					}
					if (s4.equals("DESCRIPTION")) {
						i = 8;
						break;
					}
					if (s4.equals("INDEX")) {
						i = 11;
						break;
					}
					if (s4.equals("OBJECTS")) {
						i = 14;
					}
					break;

				case 2: // '\002'
					if (s4.equals("IDENTIFIER")) {
						i = 1;
					} else {
						i = 0;
					}
					break;

				case 3: // '\003'
					if (!s4.trim().startsWith("{") && s4.trim().length() != 0) {
						parsingString.parent = s4;
						i = 4;
					}
					break;

				case 4: // '\004'
					try {
						if (s4.trim().endsWith(")")) {
							String s7 = "";
							SnmpMibString snmpmibString = new SnmpMibString();
							s7 = s4.substring(s4.indexOf('(') + 1, s4
									.indexOf(')'));
							try {
								snmpmibString.number = Integer.parseInt(s7
										.trim());
							} catch (Exception exception2) {
								ok = 1;// System.out.println("Error in line " +
										// streamtokenizer.lineno());
								break;
							}
							snmpmibString.name = s4.substring(0, s4
									.indexOf("("));
							snmpmibString.parent = parsingString.parent;
							parsingString.parent = snmpmibString.name;
							addToken(snmpmibString);
						} else {
							parsingString.number = Integer.parseInt(s4.trim());
							addToken(parsingString);
							i = 0;
						}
						break;
					} catch (NumberFormatException numberformatexception) {
						ok = 1;// System.out.println("Error in getting
								// number.." + s4 + "\n" +
								// numberformatexception.toString());
					}
				case 5: // '\005'
					if (s4.indexOf('{') != -1) {
						i = 12;
						parsingString.syntax = parsingString.syntax.concat(" "
								+ s4);
						break;
					}
					if (streamtokenizer.ttype == TT_EOL
							|| streamtokenizer.ttype == -1) {
						parsingString.syntax = parsingString.syntax.concat(s4);
						if (!flag) {
							break;
						}
						if (parsingString.syntax.indexOf('{') != -1) {
							i = 12;
							break;
						}
						if (parsingString.syntax.trim().startsWith("SEQUENCE")) {
							parsingString.recordType = 1;
							parsingString.tableEntry = 1;
						}
						i = 1;
						flag = false;
						break;
					}
					parsingString.syntax = parsingString.syntax
							.concat(" " + s4);
					if (parsingString.syntax.trim().length() > 0) {
						flag = true;
					}
					break;

				case 6: // '\006'
					if (streamtokenizer.ttype == TT_EOL) {
						i = 1;
					} else {
						parsingString.access = parsingString.access.concat(" "
								+ s4);
					}
					break;

				case 7: // '\007'
					if (streamtokenizer.ttype == TT_EOL) {
						i = 1;
					} else {
						parsingString.status = parsingString.status.concat(" "
								+ s4);
					}
					break;

				case 8: // '\b'
					if (streamtokenizer.ttype == -1) {
						break;
					}
					parsingString.description = parsingString.description
							.concat(" " + s4);
					if (s4.trim().length() != 0) {
						i = 1;
					}
					break;

				case 9: // '\t'
					parsingString.recordType = SnmpMibString.recVariable;
					if (s4.indexOf('{') != -1) {
						i = 10;
						parsingString.syntax = parsingString.syntax.concat(" "
								+ s4);
						break;
					}
					if (streamtokenizer.ttype == TT_EOL
							|| streamtokenizer.ttype == -1) {
						parsingString.syntax = parsingString.syntax.concat(s4);
						if (!flag) {
							break;
						}
						if (parsingString.syntax.indexOf('{') != -1) {
							i = 10;
							break;
						}
						if (parsingString.syntax.trim().startsWith("SEQUENCE")) {
							parsingString.recordType = 1;
						}
						addToken(parsingString);
						i = 0;
						flag = false;
						break;
					}
					parsingString.syntax = parsingString.syntax
							.concat(" " + s4);
					if (parsingString.syntax.trim().length() > 0) {
						flag = true;
					}
					break;

				case 10: // '\n'
					parsingString.syntax = parsingString.syntax.concat(s4);
					if (s4.indexOf('}') == -1) {
						break;
					}
					i = 0;
					flag = false;
					if (parsingString.syntax.trim().startsWith("SEQUENCE")) {
						parsingString.recordType = 1;
					}
					addToken(parsingString);
					break;

				case 11: // '\013'
					if (s4.trim().startsWith("{")) {
						break;
					}
					if (s4.indexOf('}') != -1) {
						i = 1;
					} else {
						parsingString.index = parsingString.index.concat(s4);
					}
					break;

				case 12: // '\f'
					parsingString.syntax = parsingString.syntax.concat(s4);
					if (s4.indexOf('}') == -1) {
						break;
					}
					i = 1;
					flag = false;
					if (parsingString.syntax.trim().startsWith("SEQUENCE")) {
						parsingString.recordType = 1;
						parsingString.tableEntry = 1;
					}
					break;

				case 14: // '\016'
					parsingString.syntax = parsingString.syntax.concat(s4);
					if (s4.indexOf('}') != -1) {
						i = 1;
					}
					break;

				case 100: // 'd'
					if (s4.indexOf(';') != -1) {
						i = 0;
						// fall through

					}
				case 101: // 'e'
					if (s4.indexOf('}') != -1) {
						i = 0;
					}
					break;
				}
			}
		} catch (Exception exception1) {
			ok = 1;
		}
		if (ok == 1) {
			// System.out.println(" Error parsing MIB!");
		}

		return 0;
	}

	String getNextToken(StreamTokenizer streamtokenizer) {
		int ok = 0;
		String s = "";
		lastToken = getTokenVal(streamtokenizer);
		while (s.equals("")) {
			try {
				if (!tokenVal.equals("xx")) {
					return tokenVal;
				}
				if (!tokenVal2.equals("")) {
					setTokenVal(tokenVal2);
					tokenVal2 = "";
					return tokenVal;
				}
				if (streamtokenizer.nextToken() != -1) {
					if (streamtokenizer.ttype == TT_EOL) {
						return getTokenVal(streamtokenizer);
					}
					if (streamtokenizer.ttype == -3) {
						s = streamtokenizer.sval;
						if (s.startsWith("{") && s.trim().length() != 1) {
							setTokenVal("{");
							tokenVal2 = new String(s.substring(1));
							return "{";
						}
						if (s.endsWith("}") && s.trim().length() != 1) {
							setTokenVal(s.replace('}', ' '));
							tokenVal2 = "}";
							return s.replace('}', ' ');
						}
						if (s.startsWith("\"")) {
							String s1 = new String(s);
							streamtokenizer.nextToken();
							for (s = getTokenVal(streamtokenizer); s != null
									&& s.indexOf('"') == -1; s = getTokenVal(streamtokenizer)) {
								String s2 = getTokenVal(streamtokenizer);
								if (s2.trim().length() > 0) {
									s1 = s1.concat(" " + s2);
								}
								if (streamtokenizer.nextToken() == -1) {
									return s;
								}
							}

							s1 = s1.concat(getTokenVal(streamtokenizer));
							if (s1.trim().length() > 0) {
								tokenVal = s1;
							}
						}
						if (s.equals("--")) {
							while (streamtokenizer.ttype != 10) {
								streamtokenizer.nextToken();
							}
							break;
						}
						if (streamtokenizer.ttype == TT_EOL) {
							return " ";
						}
					} else if (streamtokenizer.ttype == -2) {
						s = String.valueOf(streamtokenizer.nval);
						if (s.trim().length() > 0) {
							return s;
						}
					} else {
						s = "";
					}
				} else {
					return "";
				}
			} catch (Exception exception) {
				if (!exception.getMessage().startsWith("Write end dead")) {
					ok = 1;
				}
				return "";
			}
		}
		if (ok == 1) {
			// System.out.println(" Error parsing MIB!");
		}

		return s;
	}

	void setTokenVal(String s) {
		tokenVal = s;
	}

	String getTokenVal(StreamTokenizer streamtokenizer) {
		try {
			if (tokenVal != "xx") {
				String s = tokenVal.toString();
				tokenVal = "xx";
				return s;
			}
			if (streamtokenizer.ttype == TT_EOL) {
				return String.valueOf('\n');
			}
			if (streamtokenizer.ttype == -3) {
				return streamtokenizer.sval;
			}
			if (streamtokenizer.ttype == -2) {
				return String.valueOf((int) streamtokenizer.nval);
			} else {
				return "";
			}
		} catch (Exception exception) {
			// System.out.println("Error get token value " +
			// exception.toString());
		}
		return "";
	}

	void addToken(SnmpMibString snmpmibString) {
		tokens.newMibParseToken(snmpmibString);
	}

}
