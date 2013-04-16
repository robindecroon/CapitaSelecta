package paperset;

import keywordmap.Visualization;
import processing.core.PApplet;
import processing.core.PImage;
import wordcloud.WordCloudDrawable;
import core.BoundingBox;
import data.Paper;
import de.fhpotsdam.unfolding.utils.ScreenPosition;

public class PaperDrawable {
	private Paper paper;
	private int circle;
	private float angle;
	private float index;
	private float offset;
	private Visualization visualization;

	private WordCloudDrawable drawable;
	private BoundingBox bounds;

	public static PImage regularImage;
	public static PImage highlightImage;

	public PaperDrawable(Visualization visualization, Paper paper,
			WordCloudDrawable drawable, float angle, float offset, int index,
			int circle) {
		this.offset = offset;
		this.paper = paper;
		this.visualization = visualization;
		this.drawable = drawable;
		this.angle = angle;
		this.index = index;
		this.circle = circle;

		if (regularImage == null)
			regularImage = visualization.getApplet().loadImage(
					"image/paper.png");
		if (highlightImage == null)
			highlightImage = visualization.getApplet().loadImage(
					"image/paperHighLight.png");
	}

	public Paper getPaper() {
		return paper;
	}

	public boolean mouseIn(float mouseX, float mouseY) {
		if (bounds == null)
			return false;
		else
			return bounds.mouseIn(mouseX, mouseY);
	}

	public void drawName(float scale, float alpha) {
		PApplet applet = visualization.getApplet();

		if (mouseIn(applet.mouseX, applet.mouseY)) {
			applet.stroke(0, alpha * 255.f);
			applet.fill(0, alpha * 255.f);
			applet.textAlign(PApplet.CENTER, PApplet.CENTER);
			applet.textSize(scale * 16.f);
			applet.text(paper.getName(), bounds.x + bounds.width / 2.f,
					bounds.y - 16.f);
		}
	}

	public void draw(float scale, float alpha) {
		PApplet applet = visualization.getApplet();
		
		float radius = (2.f + circle) * 56.f * scale;
		float theta = angle * index + offset;
		ScreenPosition p = drawable.getScreenPosition(scale);

		float cos = (float) Math.cos(theta);
		float sin = (float) Math.sin(theta);
		float x1 = p.x + cos * radius;
		float y1 = p.y + sin * radius;
		float x2 = p.x + cos * 32.f * scale;
		float y2 = p.y + sin * 32.f * scale;

		float ix1 = x1 - regularImage.width * 0.5f * scale;
		float iy1 = y1 - regularImage.height * 0.5f * scale;

		bounds = new BoundingBox(ix1, iy1, regularImage.width * scale,
				regularImage.height * scale);

		boolean inside = bounds.mouseIn(applet.mouseX, applet.mouseY);

		applet.stroke(0, alpha * 128.f);
		applet.line(x1, y1, x2, y2);
		applet.pushMatrix();
		applet.tint(255, alpha * 255.f);
		applet.translate(ix1, iy1);
		applet.scale(scale);
		applet.image(inside ? highlightImage : regularImage, 0, 0);
		applet.popMatrix();
		applet.tint(255, 255);
	}
}
