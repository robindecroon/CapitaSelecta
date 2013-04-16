package wordcloud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import keywordmap.Visualization;
import processing.core.PApplet;
import util.Logger;
import acceleration.MultiThreadPruning;
import core.BoundingBox;
import data.Database;
import data.PaperWordData;
import data.University;
import data.UniversityCluster;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.utils.ScreenPosition;

/**
 * Set of word clouds for a given zoom level.
 * 
 * @author niels
 * 
 */
public class WordCloudSet {
	private Visualization visualization;
	private List<WordCloud> visibleWordClouds = new ArrayList<WordCloud>();
	private final List<WordCloud> wordClouds = new ArrayList<WordCloud>();
	
	/**
	 * 
	 * @param applet
	 * @param map
	 * @param zoomlevel
	 * @param minzoom
	 * @param maxzoom
	 */
	public WordCloudSet(Visualization visualization, float zoom,
			float minzoom, float maxzoom) {
		this.visualization=visualization;

		double inv_log2 = 1.0 / Math.log10(2);
		double scaledMin = Math.log10(minzoom) * inv_log2;
		double scaledMax = Math.log10(maxzoom) * inv_log2;
		double scaledZoom = Math.log10(zoom) * inv_log2;
		double lerpZoom = (scaledZoom - scaledMin) / (scaledMax - scaledMin);

		float distance = 10.f * (1.f - (float) lerpZoom);

		HashMap<UniversityCluster, PaperWordData> u = Database.getInstance()
				.getWordsPerUniversity(distance);

		for (Entry<UniversityCluster, PaperWordData> e : u.entrySet()) {
			try {
				wordClouds.add(new WordCloud(visualization, e.getKey()
						.getLocation(), e.getValue()));
			} catch (IllegalStateException exception) {
				String warning = "No words were added to the word cloud of university cluser:";
				for (University uu : e.getKey().getUniversities())
					warning+="\n\t"+uu;
				Logger.Warning(warning);
			}
		}
	}

	public void draw(float alpha, Highlight highlight) {
		BoundingBox screenBounds = getScreenBounds();

		MultiThreadPruning<WordCloud> prune = new MultiThreadPruning<WordCloud>(
				wordClouds);

		visibleWordClouds = prune.getElements(screenBounds);
		
		for (WordCloud cloud : visibleWordClouds)
			cloud.draw( alpha, highlight);
	}
	
	public List<WordCloud> getVisibibleWordClouds() {
		return visibleWordClouds;
	}
	
	public String getHighlightWord() {
		return getHighlightWord(getVisibibleWordClouds());
	}

	public String getHighlightWord(List<WordCloud> visible) {
		WordCloudDrawable highLighted = null;
		PApplet a = visualization.getApplet();
		
		for (WordCloud cloud : visible) {
			WordCloudDrawable high = cloud.getMouseOver(a.mouseX,
					a.mouseY);
			if (high != null)
				highLighted = high;
		}

		return highLighted == null ? null : highLighted.getPaperWord();
	}

	private BoundingBox getScreenBounds() {
		UnfoldingMap map = visualization.getMap();
		
		ScreenPosition l = map.getScreenPosition(map.getTopLeftBorder());
		ScreenPosition r = map.getScreenPosition(map.getBottomRightBorder());

		return new BoundingBox(l.x, l.y, Math.abs(r.x - l.x), Math.abs(r.y
				- l.y));
	}

	public void updatePaperSet(String word) {
		for (WordCloud c : wordClouds)
			c.updatePaperSet(word);
	}
}
