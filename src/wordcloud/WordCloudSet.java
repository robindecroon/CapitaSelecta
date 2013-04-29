package wordcloud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import keywordmap.Drawable;
import processing.core.PApplet;
import acceleration.MultiThreadPruning;
import core.BoundingBox;
import data.Database;
import data.PaperWordData;
import data.UniversityCluster;

/**
 * Set of word clouds for a given zoom level.
 * 
 * @author niels
 * 
 */
public class WordCloudSet extends Drawable {
	private List<WordCloud> visibleWordClouds;
	private final List<WordCloud> wordClouds = new ArrayList<WordCloud>();

	private int wordCount;
	private int paperCount;
	private float zoom;
	private WordCloudManager manager;

	/**
	 * 
	 * @param applet
	 * @param map
	 * @param zoomlevel
	 * @param minzoom
	 * @param maxzoom
	 */
	public WordCloudSet(WordCloudManager manager, float zoom) {
		super(manager.getVisualization());

		this.zoom = zoom;
		this.manager = manager;

		updateWordCloudSet();
		updateWordCloudLocations();
	}

	private void updateWordCloudLocations() {
		// UnfoldingMap map = manager.getVisualization().getMap();
		//
		// boolean finished = false;
		//
		// while (!finished) {
		// finished = true;
		// for (int i = 0; i < wordClouds.size(); i++) {
		// for (int k = i + 1; k < wordClouds.size(); k++) {
		//
		// WordCloud c1 = wordClouds.get(i);
		// WordCloud c2 = wordClouds.get(k);
		// BoundingBox b1 = c1.getScreenBox();
		// BoundingBox b2 = c2.getScreenBox();
		//
		// int index = 0;
		// while (b1.intersect(b2)) {
		// finished = false;
		//
		// if (index == 0) {
		// if (b1.x < b2.x) {
		// ScreenPosition nl1 = new ScreenPosition(
		// b1.x - 1, b1.y);
		// ScreenPosition nl2 = new ScreenPosition(
		// b2.x + 1, b2.y);
		// c1.setDrawLocation(map.getLocation(nl1));
		// c2.setDrawLocation(map.getLocation(nl2));
		// } else {
		// ScreenPosition nl1 = new ScreenPosition(
		// b1.x + 1, b1.y);
		// ScreenPosition nl2 = new ScreenPosition(
		// b2.x - 1, b2.y);
		// c1.setDrawLocation(map.getLocation(nl1));
		// c2.setDrawLocation(map.getLocation(nl2));
		// }
		// } else {
		// if (b1.y < b2.y) {
		// ScreenPosition nl1 = new ScreenPosition(b1.x,
		// b1.y - 1);
		// ScreenPosition nl2 = new ScreenPosition(b2.x,
		// b2.y + 1);
		// c1.setDrawLocation(map.getLocation(nl1));
		// c2.setDrawLocation(map.getLocation(nl2));
		// } else {
		// ScreenPosition nl1 = new ScreenPosition(b1.x,
		// b1.y + 1);
		// ScreenPosition nl2 = new ScreenPosition(b2.x,
		// b2.y - 1);
		// c1.setDrawLocation(map.getLocation(nl1));
		// c2.setDrawLocation(map.getLocation(nl2));
		// }
		// }
		//
		// index=(index+1)%2;
		// b1 = c1.getScreenBox();
		// b2 = c2.getScreenBox();
		// }
		// }
		// }
		// }
	}

	private void updateWordCloudSet() {
		double inv_log2 = 1.0 / Math.log10(2);
		double scaledMin = Math.log10(getVisualization().getMinimumZoom())
				* inv_log2;
		double scaledMax = Math.log10(getVisualization().getMaximumZoom())
				* inv_log2;
		double scaledZoom = Math.log10(zoom) * inv_log2;
		double lerpZoom = (scaledZoom - scaledMin) / (scaledMax - scaledMin);

		float distance = 1.f + 10.f * (1.f - (float) lerpZoom);

		HashMap<UniversityCluster, PaperWordData> u = Database.getInstance()
				.getWordsPerUniversity(distance, manager.getFilter());

		wordCount = (int) (5 + Math.ceil(7 * lerpZoom));
		paperCount = (int) (2 + Math.ceil(128 * lerpZoom));

		List<WordCloud> newWordClouds = new ArrayList<WordCloud>();
		for (Entry<UniversityCluster, PaperWordData> e : u.entrySet()) {
			try {
				newWordClouds.add(new WordCloud(manager, e.getKey()
						.getLocation(), e.getValue(), wordCount));
			} catch (IllegalStateException exception) {
			}
		}

		wordClouds.clear();
		wordClouds.addAll(newWordClouds);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see keywordmap.Drawable#update()
	 */
	@Override
	public void update() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see keywordmap.Drawable#draw(float)
	 */
	@Override
	public void draw(float alpha) {
		BoundingBox screenBounds = getVisualization().getScreenBounds();

		if (getVisualization().moved() || visibleWordClouds == null) {
			MultiThreadPruning<WordCloud> prune = new MultiThreadPruning<WordCloud>(
					wordClouds);
			visibleWordClouds = prune.getElements(screenBounds);
		}

		for (WordCloud cloud : visibleWordClouds)
			cloud.draw(alpha);
	}

	public List<WordCloud> getVisibibleWordClouds() {
		if (visibleWordClouds == null)
			return new ArrayList<WordCloud>();
		else
			return visibleWordClouds;
	}

	public String getHighlightWord() {
		return getHighlightWord(getVisibibleWordClouds());
	}

	private String getHighlightWord(List<WordCloud> visible) {
		WordCloudDrawable highLighted = null;
		PApplet a = getVisualization().getApplet();

		for (WordCloud cloud : visible) {
			WordCloudDrawable high = cloud.getMouseOver(a.mouseX, a.mouseY);
			if (high != null)
				highLighted = high;
		}

		return highLighted == null ? null : highLighted.getPaperWord();
	}

	public void updatePaperSet(String word) {
		for (WordCloud c : wordClouds)
			c.updatePaperSet(word, paperCount);
	}
}
