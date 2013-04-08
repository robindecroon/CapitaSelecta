package wordcloud;

import core.BoundingBox;
import processing.core.PApplet;

/**
 * Represents a single word which is drawn.
 * 
 * @author niels
 * 
 */
public class WordCloudDrawable {
	private String word;
	private float size;
	private BoundingBox bounds;
	private boolean horizontal;

	public WordCloudDrawable(String word, float size, boolean horizontal,
			BoundingBox bounds) {
		this.word = word;
		this.bounds = bounds;
		this.size = size;
		this.horizontal = horizontal;
	}

	public BoundingBox getBounds() {
		return bounds;
	}

	public String getPaperWord() {
		return word;
	}

	public void draw(PApplet applet, float x, float y, float scale,float alpha) {
		applet.textSize(size * scale);
		applet.pushMatrix();
		applet.translate((bounds.x + bounds.width / 2) * scale + x,
				(bounds.y + bounds.height / 2) * scale + y);
		applet.textAlign(PApplet.CENTER, PApplet.CENTER);

		if (!horizontal)
			applet.rotate((float) Math.PI / 2.f);
		applet.fill(0, 0, 0, 255.f*alpha);
		applet.text(word, 0, 0);
		applet.popMatrix();
	}
}
