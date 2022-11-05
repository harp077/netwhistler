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

import java.util.regex.Pattern;

/**
 * Utility class to validate input data. Currently only contain one method to
 * validate IP addresses but many more can be added using the existing template.
 * The class is final so it can not be extended. The regular expression is
 * compiled once on class load. The utility method is static of course so it is
 * easy to use in the following fashion: ValidationUtils.isValidIp("127.0.0.1");
 * 
 * @author Kent Yang - www.javathehut.org
 */
public final class ValidIP {
	/**
	 * Private because this is an utility library not meant to be instantiated.
	 */
	private ValidIP() {
	}

	/**
	 * Utility method for checking to see if a String is a valid IP Address.
	 * 
	 * @param input
	 *            The String of input to be validated as an IP address string.
	 * @return True if it is a valid IP address, false otherwise.
	 */
	public static boolean isValidIp(String input) {
		return IP_PATTERN.matcher(input).matches();
	}

	private static final Pattern IP_PATTERN = Pattern.compile(
	// Found this bad boy at;
			// http://www.regular-expressions.info/examples.html
			"\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.)"
					+ "{3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b");
}
