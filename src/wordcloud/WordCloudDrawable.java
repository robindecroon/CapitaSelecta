package wordcloud;

import processing.core.PApplet;
import drawables.BoundingBox;

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

	public void draw(PApplet applet, float x, float y, float scale) {
		applet.textSize(size * scale);
		applet.pushMatrix();
		applet.translate(bounds.x * scale + x, (bounds.y+applet.textAscent()) * scale + y);

		applet.stroke(128, 128, 128);
		applet.fill(0, 0, 0, 0);
		applet.rect(0, 0, bounds.width * scale, bounds.height * scale);

		applet.textAlign(PApplet.CENTER);

		if (!horizontal) {
			applet.translate(bounds.width / 2.f * scale, (bounds.height / 2.f)
					* scale);
			applet.rotate((float) Math.PI / 2.f);
			applet.translate(-bounds.width / 2.f * scale,
					-(bounds.height / 2.f) * scale);
		}
		applet.text(word, 0, 0);
		applet.fill(0, 0, 0, 255);
		applet.text(word, bounds.width / 2 * scale, bounds.height / 2 * scale);
		applet.popMatrix();
	}
}
