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
package nnm;

import java.awt.Point;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Font;

public class Shape {

	private String label;

	private Rectangle rectangle;

	private boolean selected, shadow;

	private Color boxColor;

	private Color boxBackColor;

	private Color boxTitleColor;

	private Color boxShadColor;
	private String network;
	public String[] text;

	public boolean SHAPE_MOVE = true;

	public Shape() {
		this("","", new Rectangle(0, 0, 0, 0), null);
	}

	public Shape(String aLabel,String aNetwork) {
		this(aLabel,aNetwork,new Rectangle(0, 0, 0, 0), null);
	}

	public Shape(Rectangle aRectangle) {
		this("", "", aRectangle, null);
	}

	public Shape(String aLabel, String aNetwork, Rectangle aRectangle, String[] aText) {
		label = aLabel;
		network = aNetwork;
		rectangle = aRectangle;
		text = aText;

	}

	public String getLabel() {
		return label;
	}
	public String getNetwork(){
		return network;
	} 
	public String[] getText() {
		return text;
	}

	public Rectangle getRectangle() {
		return rectangle;
	}

	public int getWidth() {
		return rectangle.width;
	}

	public int getHeight() {
		return rectangle.height;
	}

	public Point getXY() {
		return rectangle.getLocation();
	}

	public int getYcor() {
		return rectangle.y;
	}

	public void setText(String[] newText) {
		text = newText;
	}

	public void setWidth(int newwidth) {
		rectangle.width = newwidth;
	}

	public void setHeight(int newheight) {
		rectangle.height = newheight;
	}

	public void setLabel(String newLabel) {
		label = newLabel;
	}

	public void setXY(Point p) {
		rectangle.setLocation(p.x, p.y);
	}
	public void setNetwork(String net) {
		network = net;
	}
	public void setSize(Point oldP) {
		if (SHAPE_MOVE) {
			Point p = oldP;
			 int cx= rectangle.width/2;
			 int cy = rectangle.height/2;
			 rectangle.move(p.x - cx,p.y - cy);

		} else {
			Point p = rectangle.getLocation();
			int newwidth = oldP.x - p.x;
			int newheight = oldP.y - p.y;
			if (oldP.x < p.x) {
				newwidth = p.x - oldP.x;
			}
			if (oldP.y < p.y) {
				newheight = p.y - oldP.y;
			}

			rectangle.width = newwidth;
			rectangle.height = newheight;
		}
	}

	public void setShadow(boolean state) {
		shadow = state;

	}

	public void setTitleColor(Color c) {
		boxTitleColor = c;
	}

	public void setBoxColor(Color c) {
		boxColor = c;
	}

	public void setBackColor(Color c) {
		boxBackColor = c;
	}

	public void setShadColor(Color c) {
		boxShadColor = c;
	}

	public Color getTitleColor() {
		return boxTitleColor;
	}

	public Color getBackColor() {
		return boxBackColor;
	}

	public Color getBoxColor() {
		return boxColor;
	}

	public Color getShadColor() {
		return boxShadColor;
	}

	public boolean getShadow() {
		return shadow;

	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean state) {
		selected = state;
	}

	public void toggleSelected() {
		selected = !selected;
	}

	public String toString() {
		return (label + "(" + rectangle.x + "," + rectangle.y + ")");
	}

	public void draw(Graphics aPen) {
		String defaultfont = aPen.getFont().getName();
		Font small = new Font(defaultfont, Font.PLAIN, 10);
		Font base = aPen.getFont();
		aPen.setFont(base);
		if (NetworkManagerGUI.ZOOM)
			aPen.setFont(small);
		if (shadow) {
			aPen.setColor(boxShadColor);
			aPen.fillRect(rectangle.x + 5, rectangle.y + 5, rectangle.width,
					rectangle.height);
		}

		if (selected) {
			aPen.setColor(NetworkManagerGUI.selColor);
		} else {
			aPen.setColor(boxColor);
		}
		aPen.drawRect(rectangle.x, rectangle.y, rectangle.width,
				rectangle.height);
		aPen.setColor(boxBackColor);
		Rectangle r = new Rectangle(rectangle.x + 1, rectangle.y + 1,
				rectangle.width - 1, rectangle.height - 1);
		aPen.fillRect(r.x, r.y, r.width, r.height);

		int length = label.length();
		aPen.setColor(boxTitleColor);
		aPen.drawString(label, rectangle.x + rectangle.width / 2 - length * 2
				- length, rectangle.y + 12);

		// text
		int n = rectangle.y + 40;
		for (int i = 0; i < text.length; i++) {
			if (NetworkManagerGUI.textHasContent(text[i])) {
				aPen.drawString(text[i], rectangle.x + 20, n);
				n = n + 14;
			}
		}
		//
		if (!SHAPE_MOVE) {
			aPen.setColor(NetworkManagerGUI.selColor);
			aPen.fillRect(rectangle.x + rectangle.width - 4, rectangle.y
					+ rectangle.height - 4, 8, 8);
		}
	}


}
