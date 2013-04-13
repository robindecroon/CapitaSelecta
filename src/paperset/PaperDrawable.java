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
	private float offset;
	private PApplet applet;
	private WordCloudDrawable drawable;

	public static PImage regularImage;
	public static PImage highlightImage;

	public PaperDrawable(PApplet p, UnfoldingMap map, Paper paper,
			WordCloudDrawable drawable, float angle, float offset,int index, int circle) {
		this.offset=offset;
		this.paper = paper;
		this.applet = p;
		this.drawable = drawable;
		this.angle = angle;
		this.index = index;
		this.circle = circle;

		if (regularImage == null)
			regularImage = p.loadImage("image/paper.png");
		if (highlightImage == null)
			highlightImage = p.loadImage("image/paperHighLight.png");
	}

	public Paper getPaper() {
		return paper;
	}

	public void draw(float scale, float alpha) {
		float radius = (2.f + circle) * 56.f * scale;
		float theta = angle * index+offset;
		ScreenPosition p = drawable.getScreenPosition(scale);

		float cos = (float) Math.cos(theta);
		float sin = (float) Math.sin(theta);
		float x1 = p.x + cos * radius;
		float y1 = p.y + sin * radius;
		float x2 = p.x + cos * 32.f * scale;
		float y2 = p.y + sin * 32.f * scale;

		float ix1 = x1 - regularImage.width * 0.5f * scale;
		float iy1 = y1 - regularImage.height * 0.5f * scale;
		float ix2 = ix1 + regularImage.width * scale;
		float iy2 = iy1 + regularImage.height * scale;

		boolean inside = false;
		if (applet.mouseX >= ix1 && applet.mouseX <= ix2
				&& applet.mouseY >= iy1 && applet.mouseY <= iy2)
			inside = true;

		applet.stroke(0, alpha * 128.f);
		applet.line(x1, y1, x2, y2);

		applet.pushMatrix();
		applet.tint(255, alpha * 255.f);
		applet.translate(ix1, iy1);
		applet.scale(scale);
		applet.image(inside ? highlightImage : regularImage, 0, 0);
		applet.popMatrix();
		
		applet.tint(255, 255);
		if (inside) {
			applet.stroke(0,255.f);
			applet.fill(0,255.f);
			applet.textAlign(PApplet.CENTER,PApplet.CENTER);
			applet.textSize(scale * 16.f);
			applet.text(paper.getName(), x1, iy1- 16);
		}
	}
}
