package paperset;

import processing.core.PApplet;
import processing.core.PImage;
import wordcloud.WordCloudDrawable;
import data.Paper;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.utils.ScreenPosition;

public class PaperDrawable {
	private Paper paper;
	private int circle;
	private float angle;
	private float index;
	private PApplet applet;
	private WordCloudDrawable drawable;
	public static PImage image;

	public PaperDrawable(PApplet p, UnfoldingMap map, Paper paper,
			WordCloudDrawable drawable, float angle, int index, int circle) {
		this.paper = paper;
		this.applet = p;
		this.drawable = drawable;
		this.angle = angle;
		this.index = index;
		this.circle = circle;

		if (image == null)
			image = p.loadImage("image/paper.png");
	}

	public Paper getPaper() {
		return paper;
	}

	public void draw(float scale, float alpha) {
		float radius = (2.f+circle) * 56.f * scale;
		float theta = angle * index;
		ScreenPosition p = drawable.getScreenPosition(scale);

		float cos = (float) Math.cos(theta);
		float sin = (float) Math.sin(theta);
		float x1 = p.x + cos * radius;
		float y1 = p.y + sin * radius;
		float x2 = p.x + cos * 32.f*scale;
		float y2 = p.y + sin * 32.f*scale;

		applet.stroke(0, alpha * 128.f);
		applet.line(x1, y1, x2, y2);

		applet.pushMatrix();
		applet.tint(255, alpha * 256.f);
		applet.translate(x1 - image.width * scale * 0.5f, y1 - image.height
				* scale * 0.5f);
		applet.scale(scale);
		applet.image(image, 0, 0);
		applet.popMatrix();
		applet.tint(255, 255);
	}
}
