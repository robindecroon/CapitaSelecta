package wordcloud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import keywordmap.KeywordMap;

import processing.core.PApplet;
import de.fhpotsdam.unfolding.UnfoldingMap;

/**
 * A manager for the word clouds. Every zoom level will have a different set of
 * word clouds in order for more detail for higher zoom levels.
 * 
 * @author niels
 * 
 */
public class WordCloudManager {
	private List<Float> zoomLevels = new ArrayList<Float>();
	private HashMap<Float, WordCloudSet> map = new HashMap<Float, WordCloudSet>();
	private Highlight highlight = new Highlight();

	public WordCloudManager(PApplet applet, UnfoldingMap map, float minzoom,
			float maxzoom, float... zoomLevels) {

		for (Float zoomlevel : zoomLevels) {
			this.map.put(zoomlevel, new WordCloudSet(applet, map, zoomlevel,
					minzoom, maxzoom));
			this.zoomLevels.add(zoomlevel);
		}

		Collections.sort(this.zoomLevels);
	}

	public void draw(float zoom) {
		highlight.setChanged(false);
		highlight.update();

		float scaledZoom = KeywordMap.getScaledZoom(zoom);

		if (map.containsKey(zoom))
			map.get(zoom).draw(scaledZoom, 1.f, highlight);
		else {
			float previousZoom = getPreviousZoom(zoom);
			float nextZoom = getNextZoom(zoom);

			float alpha = linearInterpolation(zoom, previousZoom, nextZoom);

			WordCloudSet prev = map.get(previousZoom);
			WordCloudSet next = map.get(nextZoom);

			if (prev != null)
				prev.draw(scaledZoom, 1.f - alpha, highlight);
			if (next != null)
				next.draw(scaledZoom, alpha, highlight);
		}
		
		if (highlight.isChanged())
			for(WordCloudSet set : map.values())
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
