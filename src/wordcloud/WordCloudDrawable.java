package wordcloud;

import processing.core.PApplet;
import drawables.BoundingBox;

public class WordCloudDrawable {
	private String word;
	private float size;
	private BoundingBox bounds;

	public WordCloudDrawable(String word, float size, BoundingBox bounds) {
		this.word = word;
		this.bounds = bounds;
		this.size = size;
	}

	public BoundingBox getBounds() {
		return bounds;
	}

	public String getPaperWord() {
		return word;
	}

	public void draw(PApplet applet, float x, float y, float scale) {
		applet.textSize(size * scale);
		applet.text(word, bounds.x * scale + x, bounds.y * scale + y + scale
				* size / 2);
	}
}
