package wordcloud;

import keywordmap.HighlightData;
import keywordmap.KeywordMap;
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
	private PApplet applet;
	private UnfoldingMap map;
	private Location location;

	private float cachedZoom;
	private BoundingBox cachedBoundingBox;

	private String word;
	private float size;
	private BoundingBox bounds;
	private boolean horizontal;

	public WordCloudDrawable(PApplet applet, UnfoldingMap map,
			Location location, String word, float size, boolean horizontal,
			BoundingBox bounds) {
		this.map = map;
		this.location = location;
		this.applet = applet;

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

	public void draw(float scale, float layerAlpha, HighlightData data) {
		ScreenPosition p = map.getScreenPosition(location);


		float highlightAlpha = data.getAlpha(getPaperWord());
		applet.fill(0, 0, 0, 255.f * layerAlpha * highlightAlpha);

		applet.textSize(Math.min(48,size * scale*data.getHighlightScale(getPaperWord())));
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
