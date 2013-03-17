package drawables.visualization;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import drawables.BoundingBox;
import drawables.MapDrawable;
import processing.core.PApplet;

public class ConnectionDrawable extends MapDrawable {
	private AuthorDrawable author;
	private PaperDrawable paper;
	private boolean highLight = false;

	/**
	 * 
	 * @param applet
	 * @param author
	 * @param paper
	 */
	public ConnectionDrawable(PApplet applet, UnfoldingMap map,
			AuthorDrawable author, PaperDrawable paper) {
		super(applet, map);
		this.author = author;
		this.paper = paper;
	}

	public AuthorDrawable getAuthorDrawable() {
		return author;
	}

	public PaperDrawable getPaperDrawable() {
		return paper;
	}

	/**
	 * 
	 * @param highLight
	 */
	public void setHighlight(boolean highLight) {
		this.highLight = highLight;
	}

	/**
	 * 
	 * @return
	 */
	public boolean getHighLigh() {
		return highLight;
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
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + (highLight ? 1231 : 1237);
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
		ConnectionDrawable other = (ConnectionDrawable) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (highLight != other.highLight)
			return false;
		if (paper == null) {
			if (other.paper != null)
				return false;
		} else if (!paper.equals(other.paper))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see drawables.Drawable#draw()
	 */
	@Override
	public void draw() {
		PApplet a = getApplet();
		if (highLight) {
			a.stroke(0, 0, 0, 150);
			a.strokeWeight(3);
		} else {
			a.stroke(0, 0, 0, 50);
			a.strokeWeight(1);
		}

		ScreenPosition p1 = author.getScreenPosition();
		ScreenPosition p2 = paper.getScreenPosition();
		a.line(p1.x, p1.y, p2.x, p2.y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see drawables.Drawable#calculateBoundingBox()
	 */
	@Override
	protected BoundingBox calculateScreenBox() {
		ScreenPosition p1 = author.getScreenPosition();
		ScreenPosition p2 = paper.getScreenPosition();
		float minx = Math.min(p1.x, p2.x);
		float maxx = Math.max(p1.x, p2.x);
		float miny = Math.min(p1.y, p2.y);
		float maxy = Math.max(p1.y, p2.y);
		BoundingBox box = new BoundingBox(minx, miny, maxx - minx, maxy - miny);
		return box;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see drawables.Drawable#update(float, boolean, boolean)
	 */
	@Override
	public void update(float scale, boolean moved, boolean zoomed) {
		if (scale > 0 || moved || zoomed) 	
			markScreenBoxDirty();
	}
}
