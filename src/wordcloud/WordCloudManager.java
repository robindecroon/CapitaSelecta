package wordcloud;

import java.util.HashMap;

import keywordmap.Drawable;
import keywordmap.Visualization;
import data.Conference;
import filter.Filter;
import filter.GeneralFilter;

/**
 * A manager for the word clouds sets. A word cloud set is a set of word clouds
 * for a specific zoom level.
 * 
 * @author niels
 * 
 */
public class WordCloudManager extends Drawable {
	private Highlight highlight = new Highlight();
	// private Filter filter = AllAllowedFilter.getInstance();
	private Filter filter = new GeneralFilter(2008, 2012, Conference.EDM);
	private HashMap<Float, WordCloudSet> zoomMap = new HashMap<Float, WordCloudSet>();

	public WordCloudManager(Visualization visualization, float minzoom,
			float maxzoom, float... zoomLevels) {
		super(visualization);

		for (Float zoomlevel : zoomLevels)
			this.zoomMap.put(zoomlevel, new WordCloudSet(this, zoomlevel,
					minzoom, maxzoom));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see keywordmap.Drawable#update()
	 */
	@Override
	public void update() {
		float currentZoom = getVisualization().getZoom();

		if (getVisualization().leftClicked()
				&& zoomMap.containsKey(currentZoom))
			setHighlightedWord(zoomMap.get(currentZoom).getHighlightWord());

		highlight.update();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see keywordmap.Drawable#draw(float)
	 */
	@Override
	public void draw(float alpha) {
		float zoom = getVisualization().getZoom();

		if (zoomMap.containsKey(zoom))
			zoomMap.get(zoom).draw(alpha);
		else {
			float previousZoom = getPreviousZoom(zoom);
			float nextZoom = getNextZoom(zoom);

			float layerAlpha = linearInterpolation(zoom, previousZoom, nextZoom);

			WordCloudSet prev = zoomMap.get(previousZoom);
			WordCloudSet next = zoomMap.get(nextZoom);

			if (prev != null)
				prev.draw(alpha * (1.f - layerAlpha));
			if (next != null)
				next.draw(alpha * layerAlpha);
		}
	}

	public Highlight getHighlight() {
		return highlight;
	}

	public void setFilter(Filter filter) {
		if (filter == null)
			throw new NullPointerException("The given filter is null!");
		this.filter = filter;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setHighlightedWord(String word) {
		highlight.setChanged(false);
		highlight.setHighlightedWord(word);

		if (highlight.isChanged())
			for (WordCloudSet set : zoomMap.values())
				set.updatePaperSet(highlight.getHighlightedWord());
	}

	private float getPreviousZoom(float zoom) {
		return (float) Math.pow(2.f, Math.floor(Math.log(zoom) / Math.log(2)));
	}

	private float getNextZoom(float zoom) {
		return (float) Math.pow(2.f, Math.ceil(Math.log(zoom) / Math.log(2)));
	}

	private float linearInterpolation(float zoom, float zoom1, float zoom2) {
		float inv_log2 = 1.f / (float) Math.log(2);
		float scaledZoom = (float) Math.log(zoom) * inv_log2;
		float scaledZoom1 = (float) Math.log(zoom1) * inv_log2;
		float scaledZoom2 = (float) Math.log(zoom2) * inv_log2;

		return (scaledZoom - scaledZoom1) / (scaledZoom2 - scaledZoom1);
	}
}
