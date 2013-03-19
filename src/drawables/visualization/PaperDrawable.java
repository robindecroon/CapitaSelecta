package drawables.visualization;

import java.util.List;

import processing.core.PApplet;
import processing.core.PImage;
import util.KeywordColor;
import util.RandomGenerator;
import data.Author;
import data.Paper;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import drawables.BoundingBox;
import drawables.PositionedDrawable;

public class PaperDrawable extends PositionedDrawable {
	private UnfoldingMap map;
	private Location location;
	private Paper paper;
	private PImage red;
	private PImage green;
	private PImage image;
	private PImage highlight;
	private float drawSize;

	private float highLightScale = 1.f;
	private KeywordColor prevColor = KeywordColor.BLUE;

	/**
	 * 
	 * @param applet
	 */
	public PaperDrawable(PApplet applet, Paper paper, UnfoldingMap map,
			PImage image, PImage highlight, PImage red, PImage green, float zoom) {
		super(applet, map);

		float xx = 0;
		float yy = 0;

		List<Author> authors = paper.getAuthors();
		for (Author author : authors) {
			Location l = author.getUniversity().getLocation();
			xx += l.getLat();
			yy += l.getLon();
		}

		xx /= authors.size();
		yy /= authors.size();

		float radius = (float) Math.sqrt(RandomGenerator.RandomFloat());
		float theta = (float) (RandomGenerator.RandomFloat() * 2.f * Math.PI);

		xx += radius * Math.cos(theta);
		yy += radius * Math.sin(theta);

		this.location = new Location(xx, yy);
		this.paper = paper;
		this.image = image;
		this.highlight = highlight;
		this.red = red;
		this.green = green;
		this.map = map;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see drawables.PositionedDrawable#calculateScreenPosition()
	 */
	@Override
	protected ScreenPosition calculateScreenPosition() {
		return map.getScreenPosition(location);
	}

	@Override
	public void setZoom(float zoom) {
		super.setZoom(zoom);
		this.drawSize = highLightScale * Math.min(zoom * 0.3f, 24);
	}

	/**
	 * 
	 * @return
	 */
	public Paper getPaper() {
		return paper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see drawables.Drawable#update(float)
	 */
	@Override
	public void update(float scale, boolean moved, boolean zoomed) {
		if (!paper.getColor().equals(prevColor)) {
			prevColor = paper.getColor();
			if (paper.getColor().equals(KeywordColor.BLUE))
				highLightScale = 1.f;
			else
				highLightScale = 3.f;
			this.drawSize = highLightScale * Math.min(getZoom() * 0.3f, 24);
		}
		if (moved || zoomed) {
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
		PApplet a = getApplet();
		BoundingBox b = getScreenBox();

		float scale = drawSize / Math.max(image.width, image.height);

		a.pushMatrix();
		a.translate(b.x, b.y);
		a.scale(scale);

		if (getHighLight())
			a.image(highlight, 0, 0);
		else if (paper.getColor().equals(KeywordColor.BLUE))
			a.image(image, 0, 0);
		else if (paper.getColor().equals(KeywordColor.RED))
			a.image(red, 0, 0);
		else if (paper.getColor().equals(KeywordColor.GREEN))
			a.image(green, 0, 0);
		a.popMatrix();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((paper == null) ? 0 : paper.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PaperDrawable other = (PaperDrawable) obj;
		if (paper == null) {
			if (other.paper != null)
				return false;
		} else if (!paper.equals(other.paper))
			return false;
		return true;
	}
}
