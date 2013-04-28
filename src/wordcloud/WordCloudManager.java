package wordcloud;

import java.util.HashMap;

import keywordmap.Drawable;
import keywordmap.Visualization;
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
	
	private Filter newFilter;
	private Filter filter = new GeneralFilter();
	private HashMap<Float, WordCloudSet> zoomMap = new HashMap<Float, WordCloudSet>();

	private float filterAlpha = 1.f;
	private boolean changingFilter = false;

	/**
	 * 
	 * @param visualization
	 * @param zoomLevels
	 */
	public WordCloudManager(Visualization visualization, float... zoomLevels) {
		super(visualization);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see keywordmap.Drawable#update()
	 */
	@Override
	public void update() {
		float alphaSpeed = 0.08f;
		if (changingFilter) {
			if (filterAlpha > alphaSpeed)
				filterAlpha -= alphaSpeed;
			else {
				changingFilter = false;
				filterAlpha = 0.f;
				filter=newFilter;
				zoomMap.clear();
			}
		} else {
			if (filterAlpha + alphaSpeed < 1.f)
				filterAlpha += alphaSpeed;
			else
				filterAlpha = 1.f;
		}

		float currentZoom = getVisualization().getZoom();
		float previousZoom = getPreviousZoom(currentZoom);
		float nextZoom = getNextZoom(currentZoom);

		if (!zoomMap.containsKey(previousZoom))
			this.zoomMap
					.put(previousZoom, new WordCloudSet(this, previousZoom));

		if (!zoomMap.containsKey(nextZoom))
			this.zoomMap.put(nextZoom, new WordCloudSet(this, previousZoom));

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
			zoomMap.get(zoom).draw(filterAlpha * alpha);
		else {
			float previousZoom = getPreviousZoom(zoom);
			float nextZoom = getNextZoom(zoom);

			float layerAlpha = linearInterpolation(zoom, previousZoom, nextZoom);

			WordCloudSet prev = zoomMap.get(previousZoom);
			WordCloudSet next = zoomMap.get(nextZoom);

			if (prev != null)
				prev.draw(filterAlpha * alpha * (1.f - layerAlpha));
			if (next != null)
				next.draw(filterAlpha * alpha * layerAlpha);
		}
	}

	public Highlight getHighlight() {
		return highlight;
	}

	public void setFilter(Filter filter) {
		if (filter == null)
			throw new NullPointerException("The given filter is null!");
		this.newFilter=filter;
		changingFilter = true;
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
