package wordcloud;

import drawables.BoundingBox;
import processing.core.PApplet;

public class WordBox {
	private BoundingBox box;

	public WordBox(PApplet p, String string, float size, float x, float y) {

		p.textSize(size);
		float width = p.textWidth(string);
		float height = Math.abs(p.textAscent()) + Math.abs(p.textDescent());

		box = new BoundingBox(x, y, width, height);
	}

	public BoundingBox getBoundingBox() {
		return box;
	}
}
