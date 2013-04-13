package wordcloud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import keywordmap.UniversityCluster;
import processing.core.PApplet;
import acceleration.MultiThreadPruning;
import core.BoundingBox;
import data.Database;
import data.PaperWordData;
import data.University;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.utils.ScreenPosition;

/**
 * Set of word clouds for a given zoom level.
 * 
 * @author niels
 * 
 */
public class WordCloudSet {
	private final List<WordCloud> wordClouds = new ArrayList<WordCloud>();
	private UnfoldingMap map;
	private PApplet applet;
	private boolean isPressed = false;

	/**
	 * 
	 * @param applet
	 * @param map
	 * @param zoomlevel
	 * @param minzoom
	 * @param maxzoom
	 */
	public WordCloudSet(PApplet applet, UnfoldingMap map, float zoom,
			float minzoom, float maxzoom) {
		this.map = map;
		this.applet = applet;

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
				wordClouds.add(new WordCloud(applet, map, e.getKey()
						.getLocation(), e.getValue()));
			} catch (IllegalStateException exception) {
				System.out
						.println("No words were added to the word cloud of university cluser:");
				for (University uu : e.getKey().getUniversities())
					System.out.println("\t" + uu);
			}
		}
	}

	public void draw(float zoom, float alpha, Highlight highlight) {
		BoundingBox screenBounds = getScreenBounds();

		MultiThreadPruning<WordCloud> prune = new MultiThreadPruning<WordCloud>(
				wordClouds);

		List<WordCloud> visible = prune.getElements(screenBounds);

		if (isPressed && !applet.mousePressed)
			highlight.setHighlightedWord(getHighlightWord(visible));
		isPressed=applet.mousePressed;
		
		for (WordCloud cloud : visible)
			cloud.draw(zoom, alpha, highlight);
	}

	public String getHighlightWord(List<WordCloud> visible) {
		WordCloudDrawable highLighted = null;
		for (WordCloud cloud : visible) {
			WordCloudDrawable high = cloud.getMouseOver(applet.mouseX,
					applet.mouseY);
			if (high != null)
				highLighted = high;
		}

		return highLighted == null ? null : highLighted.getPaperWord();
	}

	private BoundingBox getScreenBounds() {
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
