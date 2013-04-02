package wordcloud;

import processing.core.PApplet;
import drawables.BoundingBox;

public class WordCloudDrawable {
	private String string;
	private float size;
	private BoundingBox bounds;

	public WordCloudDrawable(String string, float size, BoundingBox bounds) {
		this.string = string;
		this.bounds = bounds;
		this.size = size;
	}

	public BoundingBox getBounds() {
		return bounds;
	}

	public String getString() {
		return string;
	}

	public void draw(PApplet applet, float x, float y, float scale) {
		applet.textSize(size*scale);
		applet.text(string, bounds.x*scale+x, bounds.y*scale+y+scale*size/2);
	}
}
