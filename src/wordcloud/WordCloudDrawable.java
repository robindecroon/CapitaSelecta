package wordcloud;

import keywordmap.KeywordMap;
import keywordmap.Visualization;
import processing.core.PApplet;
import acceleration.Bounded;
import core.BoundingBox;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.utils.ScreenPosition;

/**
 * Represents a single word which is drawn.
 * 
 * @author niels
 * 
 */
public class WordCloudDrawable implements Bounded {
	private Visualization visualization;
	private Location location;

	private float cachedZoom;
	private BoundingBox cachedBoundingBox;

	private String word;
	private float size;
	private BoundingBox bounds;
	private boolean horizontal;

	public WordCloudDrawable(Visualization visualization, Location location,
			String word, float size, boolean horizontal, BoundingBox bounds) {
		this.visualization = visualization;
		this.location=location;
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

	public ScreenPosition getScreenPosition(float scale) {
		ScreenPosition p = visualization.getMap().getScreenPosition(location);

		return new ScreenPosition(p.x + (bounds.x + bounds.width * 0.5f)
				* scale, p.y + (bounds.y + bounds.height * 0.5f) * scale);
	}

	public void draw(float layerAlpha, Highlight data) {
		PApplet applet = visualization.getApplet();
		UnfoldingMap map = visualization.getMap();

		ScreenPosition p = map.getScreenPosition(location);

		float scale = visualization.getDrawScale();
		float highlightAlpha = data.getAlpha(getPaperWord());
		applet.fill(0, 0, 0, 255.f * layerAlpha * highlightAlpha);

		applet.textSize(Math.min(48,
				size * scale * data.getScale(getPaperWord())));
		applet.textAlign(PApplet.CENTER, PApplet.CENTER);

		if (!horizontal) {
			applet.pushMatrix();
			applet.translate((bounds.x + bounds.width * 0.5f) * scale + p.x,
					(bounds.y + bounds.height * 0.5f) * scale + p.y);
			applet.rotate((float) (Math.PI * 0.5));
			applet.text(word, 0, 0);
			applet.popMatrix();
		} else {
			applet.text(word, (bounds.x + bounds.width * 0.5f) * scale + p.x,
					(bounds.y + bounds.height * 0.5f) * scale + p.y);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see acceleration.Bounded#getScreenBox()
	 */
	@Override
	public BoundingBox getScreenBox() {
		UnfoldingMap map = visualization.getMap();

		if (cachedBoundingBox == null || map.getZoom() != cachedZoom) {
			cachedZoom = KeywordMap.getScaledZoom(map.getZoom());
			ScreenPosition p = map.getScreenPosition(location);

			cachedBoundingBox = new BoundingBox(p.x + bounds.x * cachedZoom,
					p.y + bounds.y * cachedZoom, bounds.width * cachedZoom,
					bounds.height * cachedZoom);

		}
		return cachedBoundingBox;
	}
}
