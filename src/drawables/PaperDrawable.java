package drawables;

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

public class PaperDrawable extends PositionedDrawable {
	private UnfoldingMap map;
	private Location location;
	private Paper paper;
	private PImage red;
	private PImage green;
	private PImage image;
	private PImage highlight;
	private float drawSize;

	/**
	 * 
	 * @param applet
	 */
	public PaperDrawable(PApplet applet, Paper paper, UnfoldingMap map,
			PImage image, PImage red, PImage green, PImage highlight, float zoom) {
		super(applet, zoom);

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
	protected BoundingBox calculateBoundingBox() {
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
		this.drawSize = Math.min(zoom / 1.5f, 40);
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
	public void update(float scale) {
		super.update(scale);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see drawables.Drawable#draw()
	 */
	@Override
	public void draw() {
		PApplet a = getApplet();
		BoundingBox b = getBoundingBox();

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
}
