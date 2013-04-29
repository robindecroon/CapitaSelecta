package wordcloud;

import keywordmap.Drawable;
import processing.core.PApplet;
import acceleration.Bounded;
import core.BoundingBox;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.utils.ScreenPosition;

/**
 * Represents a single word which is drawn.
 * 
 * @author niels
 * 
 */
public class WordCloudDrawable extends Drawable implements Bounded {
	private WordCloudManager manager;
	private WordCloud cloud;

	private float cachedZoom;
	private BoundingBox cachedBoundingBox;

	private String word;
	private float size;
	private BoundingBox bounds;
	private boolean horizontal;

	public WordCloudDrawable(WordCloudManager manager, WordCloud cloud,
			String word, float size, boolean horizontal, BoundingBox bounds) {
		super(manager.getVisualization());
		this.manager = manager;
		this.cloud = cloud;
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
		ScreenPosition p = getVisualization().getMap().getScreenPosition(
				cloud.getDrawLocation());

		return new ScreenPosition(p.x + (bounds.x + bounds.width * 0.5f)
				* scale, p.y + (bounds.y + bounds.height * 0.5f) * scale);
	}

	public void draw(float layerAlpha) {
		PApplet applet = getVisualization().getApplet();
		UnfoldingMap map = getVisualization().getMap();

		float scale = getVisualization().getDrawScale();
		float textSize = size * scale
				* manager.getHighlight().getScale(getPaperWord());
		float highlightAlpha = manager.getHighlight().getAlpha(getPaperWord());

		if (map.getZoom() < 4.2f && textSize < 4.f)
			return;

		ScreenPosition p = map.getScreenPosition(cloud.getDrawLocation());

		applet.fill(0, 0, 0, 255.f * layerAlpha * highlightAlpha);

		if (!horizontal) {
			applet.pushMatrix();
			applet.translate((bounds.x + bounds.width * 0.5f) * scale + p.x,
					(bounds.y + bounds.height * 0.5f) * scale + p.y);
			applet.rotate((float) (Math.PI * 0.5));
			applet.textSize(Math.min(48, textSize));
			applet.textAlign(PApplet.CENTER, PApplet.CENTER);
			applet.text(word, 0, 0);
			applet.popMatrix();
		} else {
			applet.textSize(Math.min(48, textSize));
			applet.textAlign(PApplet.CENTER, PApplet.CENTER);
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
		UnfoldingMap map = getVisualization().getMap();

		if (cachedBoundingBox == null || map.getZoom() != cachedZoom) {
			cachedZoom = getVisualization().getDrawScale();
			ScreenPosition p = map.getScreenPosition(cloud.getDrawLocation());

			cachedBoundingBox = new BoundingBox(p.x + bounds.x * cachedZoom,
					p.y + bounds.y * cachedZoom, bounds.width * cachedZoom,
					bounds.height * cachedZoom);

		}
		return cachedBoundingBox;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see keywordmap.Drawable#update()
	 */
	@Override
	public void update() {
	}
}
