package paperset;

import keywordmap.Drawable;
import processing.core.PApplet;
import processing.core.PImage;
import wordcloud.WordCloudDrawable;
import wordcloud.WordCloudManager;
import acceleration.Bounded;
import core.BoundingBox;
import data.Paper;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import filter.PaperColor;

public class PaperDrawable extends Drawable implements Bounded {
	private Paper paper;
	private int circle;
	private final float cos;
	private final float sin;

	private WordCloudManager manager;
	private WordCloudDrawable drawable;
	private BoundingBox bounds;

	public static PImage regularImage;
	public static PImage highlightImage;
	public static PImage circleImage;

	private BoundingBox cachedBounds;
	private float cachedZoomLevel;

	public PaperDrawable(WordCloudManager manager, Paper paper,
			WordCloudDrawable drawable, float angle, float offset, int index,
			int circle) {
		super(manager.getVisualization());
		this.manager = manager;
		this.paper = paper;
		this.drawable = drawable;
		this.circle = circle;

		float theta = angle * index + offset;
		this.cos = (float) Math.cos(theta);
		this.sin = (float) Math.sin(theta);

		if (circleImage == null)
			circleImage = getVisualization().getApplet().loadImage(
					"image/circle2.png");
		if (regularImage == null)
			regularImage = getVisualization().getApplet().loadImage(
					"image/lightPaper.png");
		if (highlightImage == null)
			highlightImage = getVisualization().getApplet().loadImage(
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

	public void drawName(float alpha) {
		PApplet applet = getVisualization().getApplet();

		if (mouseIn(applet.mouseX, applet.mouseY)) {
			applet.stroke(0, alpha * 255.f);
			applet.fill(0, alpha * 255.f);
			applet.textAlign(PApplet.CENTER, PApplet.CENTER);
			applet.textSize(getVisualization().getDrawScale() * 16.f);
			applet.text(paper.getName(), bounds.x + bounds.width / 2.f,
					bounds.y - 16.f);
		}
	}

	public void draw(float alpha) {
		float scale = getVisualization().getDrawScale();

		PApplet applet = getVisualization().getApplet();

		float radius = (2.f + circle) * 56.f * scale;
		ScreenPosition p = drawable.getScreenPosition(scale);

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

		PaperColor c = manager.getFilter().getColor(getPaper());

		applet.pushMatrix();
		applet.translate(x1 - circleImage.width * 0.5f * scale, y1
				- circleImage.height * 0.5f * scale);
		applet.scale(scale);
		if (inside)
			applet.tint(c.red, c.green, c.blue, (int) (alpha * 256.f));
		else
			applet.tint(c.red, c.green, c.blue, (int) (alpha * 128.f));
		applet.image(circleImage, 0, 0);
		applet.popMatrix();

		applet.pushMatrix();
		applet.translate(ix1, iy1);
		applet.scale(scale);
		applet.tint(255.f, alpha * 255.f);
		applet.image(inside ? highlightImage : regularImage, 0, 0);
		applet.popMatrix();
		applet.tint(255, 255);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see keywordmap.Drawable#update()
	 */
	@Override
	public void update() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see acceleration.Bounded#getScreenBox()
	 */
	@Override
	public BoundingBox getScreenBox() {
		if (cachedBounds == null
				|| getVisualization().getMap().getZoom() == cachedZoomLevel) {
			cachedZoomLevel = getVisualization().getMap().getZoom();

			float scale = getVisualization().getDrawScale();
			float radius = (2.f + circle) * 56.f * scale;
			ScreenPosition p = drawable.getScreenPosition(scale);

			float x1 = p.x + cos * radius - scale * 0.5f * circleImage.width;
			float y1 = p.y + sin * radius - scale * 0.5f * circleImage.height;
			cachedBounds = new BoundingBox(x1, y1, scale * circleImage.width,
					scale * circleImage.height);
		}
		return cachedBounds;
	}
}
