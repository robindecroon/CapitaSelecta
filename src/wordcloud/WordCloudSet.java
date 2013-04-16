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
	private List<WordCloud> visibleWordClouds = new ArrayList<WordCloud>();
	private final List<WordCloud> wordClouds = new ArrayList<WordCloud>();

	private int wordCount;
	private int paperCount;

	/**
	 * 
	 * @param applet
	 * @param map
	 * @param zoomlevel
	 * @param minzoom
	 * @param maxzoom
	 */
	public WordCloudSet(WordCloudManager manager, float zoom, float minzoom,
			float maxzoom) {
		super(manager.getVisualization());

		double inv_log2 = 1.0 / Math.log10(2);
		double scaledMin = Math.log10(minzoom) * inv_log2;
		double scaledMax = Math.log10(maxzoom) * inv_log2;
		double scaledZoom = Math.log10(zoom) * inv_log2;
		double lerpZoom = (scaledZoom - scaledMin) / (scaledMax - scaledMin);

		float distance = 10.f * (1.f - (float) lerpZoom);

		HashMap<UniversityCluster, PaperWordData> u = Database.getInstance()
				.getWordsPerUniversity(distance);

		wordCount = (int) (3+ Math.ceil(7 * lerpZoom));
		paperCount = (int) (2 + Math.ceil(14 * lerpZoom));

		for (Entry<UniversityCluster, PaperWordData> e : u.entrySet()) {
			try {
				wordClouds.add(new WordCloud(manager, e.getKey().getLocation(),
						e.getValue(), wordCount));
			} catch (IllegalStateException exception) {
//				String warning = "No words were added to the word cloud of university cluser:";
//				for (University uu : e.getKey().getUniversities())
//					warning += "\n\t" + uu;
//				Logger.Warning(warning);
			}
		}
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

		MultiThreadPruning<WordCloud> prune = new MultiThreadPruning<WordCloud>(
				wordClouds);

		visibleWordClouds = prune.getElements(screenBounds);

		for (WordCloud cloud : visibleWordClouds)
			cloud.draw(alpha);
	}

	public List<WordCloud> getVisibibleWordClouds() {
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
