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

import java.io.File;
import javax.swing.filechooser.*;

public class ImageFilter extends FileFilter {

	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String extension = ImageFilterUtils.getExtension(f);
		if (extension != null) {
			if (extension.equals(ImageFilterUtils.tiff)
					|| extension.equals(ImageFilterUtils.tif)
					|| extension.equals(ImageFilterUtils.gif)
					|| extension.equals(ImageFilterUtils.jpeg)
					|| extension.equals(ImageFilterUtils.jpg)
					|| extension.equals(ImageFilterUtils.png)) {
				return true;
			} else {
				return false;
			}
		}

		return false;
	}

	// The description of this filter
	public String getDescription() {
		return "Background images (png,tiff,gif,jpeg)";
	}
}
