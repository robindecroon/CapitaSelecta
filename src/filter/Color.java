package filter;

import processing.core.PApplet;

public class Color {
	public final int red;
	public final int green;
	public final int blue;

	public final static Color WHITE = new Color(255, 255, 255);
	public final static Color C2008 = new Color(255, 210, 0);
	public final static Color C2009 = new Color(227, 27, 35);
	public final static Color C2010 = new Color(175, 189, 34);
	public final static Color C2011 = new Color(93, 135, 161);
	public final static Color C2012 = new Color(19, 181, 234);

	public Color(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public void setAppletToColor(PApplet applet, int alpha) {
		applet.color(red, green, blue, alpha);
	}
}
