package drawables.visualization;

import processing.core.PApplet;
import processing.core.PImage;
import data.Author;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import drawables.BoundingBox;
import drawables.PositionedDrawable;

public class AuthorDrawable extends PositionedDrawable {
	private Location centerLocation;
	private float angle;
	private int circleId;
	private int circle;
	private float zoom;
	private PImage image;
	private PImage highLight;
	private Author author;
	private UnfoldingMap map;
	private float frames = 0;
	private float drawSize;

	public AuthorDrawable(PApplet applet, Author author, UnfoldingMap map,
			PImage image, PImage highlight, Location center, float angle,
			int circleId, int circle, float zoom) {
		super(applet, map);

		this.author = author;
		this.map = map;
		this.image = image;
		this.highLight = highlight;
		this.centerLocation = center;
		this.angle = angle;
		this.circleId = circleId;
		this.circle = circle;

		setZoom(zoom);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see drawables.PositionedDrawable#setZoom(float)
	 */
	@Override
	public void setZoom(float zoom) {
		super.setZoom(zoom);
		this.drawSize = Math.min(zoom *0.3f, 24);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see drawables.PositionedDrawable#calculateScreenPosition()
	 */
	@Override
	protected ScreenPosition calculateScreenPosition() {
		ScreenPosition p = map.getScreenPosition(centerLocation);
		float theta = frames * 0.015f + angle * circleId;
		float seperation = zoom + (circle + 2.f) * 1.5f * drawSize;
		float xx = p.x + seperation * (float) Math.cos(theta);
		float yy = p.y + seperation * (float) Math.sin(theta);

		return new ScreenPosition(xx, yy);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see drawables.PositionedDrawable#calculateBoundingBox()
	 */
	@Override
	protected BoundingBox calculateScreenBox() {
		ScreenPosition p = getScreenPosition();

		float scale = drawSize / Math.max(image.width, image.height);
		float leftX = p.x - image.width * scale / 2.f;
		float topY = p.y - image.height * scale / 2.f;

		return new BoundingBox(leftX, topY, image.width * scale, image.height
				* scale);
	}

	/**
	 * 
	 * @return
	 */
	public Author getAuthor() {
		return author;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see drawables.Drawable#update(float, boolean, boolean)
	 */
	@Override
	public void update(float scale, boolean moved, boolean zoomed) {
		if (scale > 0 || moved || zoomed) {
			frames += scale;
			markScreenBoxDirty();
			markScreenPositionDirty();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see drawables.Drawable#draw()
	 */
	@Override
	public void draw() {
		if (getZoom()<4)
			return;
		PApplet a = getApplet();
		BoundingBox b = getScreenBox();
		float scale = drawSize / Math.max(image.width, image.height);

		a.pushMatrix();
		a.translate(b.x, b.y);
		a.scale(scale);

		if (getHighLight())
			a.image(highLight, 0, 0);
		else
			a.image(image, 0, 0);
		a.popMatrix();
	}
}
