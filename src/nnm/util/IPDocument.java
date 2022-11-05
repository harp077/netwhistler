package nnm.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;
import java.math.BigInteger;

public class IPDocument {
	public static JTextField ipFil;

	public IPDocument() {
	}

	public static void setDocument(JTextField f) {
		ipFil = f;
		ipFil.setDocument(new PlainDocument() {
			char[] buffer = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
					' ', ' ', ' ', ' ', ' ', };

			public void insertString(int offs, String str, AttributeSet a)
					throws BadLocationException {
				int counterOfDot = 0;
				if (str == null) {
					return;
				}

				// accept only number, blank and string which contains three '.'
				for (int i = 0; i < str.length(); i++) {
					if (str.charAt(i) == '.') {
						counterOfDot++;
					}
					if (!('0' <= str.charAt(i) && str.charAt(i) <= '9'
							|| str.charAt(i) == '.' || str.charAt(i) == ' ')) {
						return;
					}
				}
				if (counterOfDot != 3 && counterOfDot != 0) {
					return;
				}

				// pad blanks to format ununiform Ip address to uniform one
				// whose length is 15
				str = str.trim();
				String formatedString = "";
				int beginIndex = 0, endIndex = 0;
				while (endIndex >= 0 && counterOfDot == 3) {
					endIndex = str.indexOf('.', beginIndex);

					if (endIndex >= 0) {
						String tt = str.substring(beginIndex, endIndex);
						if (tt.length() == 0) {
							tt = "   ";
						}
						if (tt.length() == 1) {
							tt = "  " + tt;
						}
						if (tt.length() == 2) {
							tt = " " + tt;
						}
						formatedString = formatedString + tt + ".";
						beginIndex = endIndex + 1;
					} else {
						String tt = str.substring(beginIndex);
						if (tt.length() == 0) {
							tt = "   ";
						}
						if (tt.length() == 1) {
							tt = "  " + tt;
						}
						if (tt.length() == 2) {
							tt = " " + tt;
						}
						formatedString += tt;
					}
				}
				if (counterOfDot == 3) {
					str = formatedString;

					// after insert, length can't be more than 15
				}
				String nothing = getText(0, getLength()), nothing2 = "";
				for (int i = 0; i < nothing.length(); i++) {
					if (nothing.charAt(i) != ' ') {
						nothing2 += nothing.charAt(i);
					}
				}
				if (nothing2.length() + str.length() > 16) {
					return;
				}

				String temp = null, number = "";
				// try to insert
				String bak = getText(0, getLength());

				super.insertString(0, new String(buffer), a);

				temp = getText(0, 15);

				// if(temp has not two :) return;
				for (int i = 0; i < temp.length(); i++) {
					if (temp.charAt(i) != '.' && temp.charAt(i) != ' ') {
						number += temp.charAt(i);
					}
				}

				BigInteger big;
				if (number.length() != 0) {
					try {
						big = new BigInteger(number);
					} catch (Exception ex) {
						super.remove(0, getLength());
						super.insertString(0, bak, a);
						return;
					}
				}

				for (int i = 0; i < temp.length(); i++) {
					buffer[i] = temp.charAt(i);
				}

				int cursor = offs;

				switch (offs) {
				case 0:
				case 4:
				case 8:
				case 12:
					if (buffer[offs] == ' ') {

						// buffer[offs] = str.charAt(0);
						for (int i = 0; i < str.length(); i++) {
							buffer[offs + i] = str.charAt(i);
						}
					} else if (buffer[offs + 1] == ' ') {

						// buffer[offs + 1] = str.charAt(0);
						for (int i = 0; i < str.length(); i++) {
							buffer[offs + i + 1] = str.charAt(i);
						}
					} else if (buffer[offs + 2] == ' ') {

						// buffer[offs + 1] = str.charAt(0);
						for (int i = 0; i < str.length(); i++) {
							buffer[offs + i + 2] = str.charAt(i);
							if (offs == 0) {
								cursor = 4;
							}
							if (offs == 4) {
								cursor = 8;
							}
							if (offs == 8) {
								cursor = 12;
							}
						}
					}
					break;
				case 1:
				case 5:
				case 9:
				case 13:
					if (buffer[offs - 1] == ' ') {

						// buffer[offs] = str.charAt(0);
						for (int i = 0; i < str.length(); i++) {
							buffer[offs + i - 1] = str.charAt(i);
						}
					} else if (buffer[offs] == ' ') {

						// buffer[offs - 1] = str.charAt(0);
						for (int i = 0; i < str.length(); i++) {
							buffer[offs + i] = str.charAt(i);
						}
					} else if (buffer[offs + 1] == ' ') { // buffer[offs - 1]
															// = str.charAt(0);
						for (int i = 0; i < str.length(); i++) {
							buffer[offs + i + 1] = str.charAt(i);
						}
						if (offs == 1) {
							cursor = 4;
						}
						if (offs == 5) {
							cursor = 8;
						}
						if (offs == 9) {
							cursor = 12;
						}
					}
					break;
				case 2:
				case 6:
				case 10:
				case 14:
					if (buffer[offs - 2] == ' ') {

						// buffer[offs - 1] = str.charAt(0);
						for (int i = 0; i < str.length(); i++) {
							buffer[offs + i - 2] = str.charAt(i);
						}
					} else if (buffer[offs - 1] == ' ') {

						// buffer[offs - 1] = str.charAt(0);
						for (int i = 0; i < str.length(); i++) {
							buffer[offs + i - 1] = str.charAt(i);
						}
					} else if (buffer[offs] == ' ')
					// buffer[offs - 2] = str.charAt(0);
					{
						for (int i = 0; i < str.length(); i++) {
							buffer[offs + i] = str.charAt(i);
						}
						if (offs == 2) {
							cursor = 4;
						}
						if (offs == 6) {
							cursor = 8;
						}
						if (offs == 10) {
							cursor = 12;
						}
					}
					break;
				case 3:
				case 7:
				case 11:
				case 15:
					if (buffer[offs - 3] == ' ') {

						// buffer[offs - 1] = str.charAt(0);
						for (int i = 0; i < str.length(); i++) {
							buffer[offs + i - 3] = str.charAt(i);
						}
					} else if (buffer[offs - 2] == ' ') {

						// buffer[offs - 1] = str.charAt(0);
						for (int i = 0; i < str.length(); i++) {
							buffer[offs + i - 2] = str.charAt(i);
						}
					} else if (buffer[offs - 1] == ' ')
					// buffer[offs - 2] = str.charAt(0);
					{
						for (int i = 0; i < str.length(); i++) {
							buffer[offs + i - 1] = str.charAt(i);
						}
						if (offs == 3) {
							cursor = 4;
						}
						if (offs == 7) {
							cursor = 8;
						}
						if (offs == 11) {
							cursor = 12;
						}
					}
					break;
				default:
					break;
				}

				buffer[3] = '.';
				buffer[7] = '.';
				buffer[11] = '.';

				try {
					if (Integer.valueOf(new String(buffer, 0, 3)).intValue() > 255) {
						buffer[0] = ' ';
						buffer[1] = ' ';
						buffer[2] = ' ';
						cursor = offs;
					}
				} catch (Exception ex) {
				}

				try {
					if (Integer.valueOf((new String(buffer, 4, 3)).trim())
							.intValue() > 255) {
						buffer[4] = ' ';
						buffer[5] = ' ';
						buffer[6] = ' ';
						cursor = offs;
					}
				} catch (Exception ex) {
				}

				try {
					if (Integer.valueOf((new String(buffer, 8, 3)).trim())
							.intValue() > 255) {
						buffer[8] = ' ';
						buffer[9] = ' ';
						buffer[10] = ' ';
						cursor = offs;
					}
				} catch (Exception ex) {
				}

				try {
					if (Integer.valueOf((new String(buffer, 12, 3)).trim())
							.intValue() > 255) {
						buffer[12] = ' ';
						buffer[13] = ' ';
						buffer[14] = ' ';
						cursor = offs;
					}
				} catch (Exception ex) {
				}

				String newString = String.copyValueOf(buffer);

				super.remove(0, getLength());
				super.insertString(0, newString, a);
				ipFil.setCaretPosition(cursor);
			}

			public void remove(int offs, int len) throws BadLocationException {
				for (int i = 0; i < len; i++) {
					if (buffer[offs + i] != ' ' && buffer[offs + i] != '.') {
						buffer[offs + i] = ' ';
					}
				}

				try {
					super.remove(0, 15);
				} catch (Exception ex) {

				} finally {
					super.insertString(0, new String(buffer), null);
					if (offs == 0 || offs == 4 || offs == 8 || offs == 12) {

						// if (offs == 0 || offs == 1 || offs == 4 || offs == 5
						// || offs == 8 || offs == 9 || offs == 12 || offs ==
						// 13)
						ipFil.setCaretPosition(offs + 1);
					} else if ((offs == 1 || offs == 5 || offs == 9 || offs == 13)
							&& buffer[offs] == ' ' && buffer[offs - 1] == ' ') {

						// if (offs == 0 || offs == 1 || offs == 4 || offs == 5
						// || offs == 8 || offs == 9 || offs == 12 || offs ==
						// 13)
						ipFil.setCaretPosition(offs + 1);
					} else {
						ipFil.setCaretPosition(offs);
					}
				}
			}
		});
	}
}