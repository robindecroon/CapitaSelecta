package filter;


public class PaperColor {
	public final int red;
	public final int green;
	public final int blue;

	public final static PaperColor WHITE = new PaperColor(255, 255, 255);
	public final static PaperColor C2008 = new PaperColor(255, 210, 0);
	public final static PaperColor C2009 = new PaperColor(227, 27, 35);
	public final static PaperColor C2010 = new PaperColor(150, 200, 34);
	public final static PaperColor C2011 = new PaperColor(64, 34, 161);
	public final static PaperColor C2012 = new PaperColor(19, 181, 234);
	public final static PaperColor[] colors = new PaperColor[] { C2008, C2009, C2010,
			C2011, C2012 };

	public PaperColor(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
}
