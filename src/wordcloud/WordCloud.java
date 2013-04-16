package wordcloud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import keywordmap.Drawable;
import keywordmap.Visualization;
import paperset.PaperSet;
import processing.core.PApplet;
import acceleration.Bounded;
import core.BoundingBox;
import data.PaperWordData;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.utils.ScreenPosition;

/**
 * An object representing a word cloud.
 * 
 * @author niels
 * 
 */
public class WordCloud extends Drawable implements Bounded {
	private WordCloudManager manager;

	private int minimumCount;
	private int maximumCount;

	private int minimumFont = 12;
	private int maximumFont = 24;

	private final int NUMBEROFWORDS = 10;

	private Location location;
	private BoundingBox wordCloudSize;
	private float cachedZoomLevel;
	private BoundingBox cachedScreenBox;
	private PaperWordData data;

	private Map<String, WordCloudDrawable> drawables = new HashMap<String, WordCloudDrawable>();
	private HashMap<String, PaperSet> paperSets = new HashMap<String, PaperSet>();

	/**
	 * Creates a new word cloud from the given data.
	 * 
	 * @param p
	 * @param location
	 * @param data
	 */
	public WordCloud(WordCloudManager manager, Location location,
			PaperWordData data) {
		super(manager.getVisualization());

		if (location.getLat() != location.getLat()
				|| location.getLon() != location.getLon())
			throw new NullPointerException("The given location is not valid");

		this.manager = manager;
		this.location = location;
		this.data = data;

		construct(getVisualization(), data);
		updatePaperSet(null);
	}

	public PaperWordData getPaperWordData() {
		return data;
	}

	/**
	 * 
	 * @param p
	 * @param data
	 */
	public void construct(Visualization visualization, PaperWordData data) {
		// Get the applet
		PApplet p = visualization.getApplet();

		// Initialize the bounding box the wordcloud will have on screen.
		wordCloudSize = new BoundingBox();

		// Initialize the size to place the drawables in.
		int width = 48;
		int height = 48;

		// Initialize the minimum and maximum word count.
		minimumCount = Integer.MAX_VALUE;
		maximumCount = Integer.MIN_VALUE;

		Random random = new Random(System.currentTimeMillis()
				+ location.hashCode());

		/*
		 * Analyse the word data.
		 */
		HashMap<String, Integer> map = data.getWordCount();
		List<CountedString> allWords = new ArrayList<CountedString>();
		for (Entry<String, Integer> e : map.entrySet())
			allWords.add(new CountedString(e.getKey(), e.getValue()));
		Collections.sort(allWords);

		List<CountedString> words = new ArrayList<CountedString>();
		for (int i = 0; i < Math.min(NUMBEROFWORDS, allWords.size()); i++)
			words.add(allWords.get(i));

		for (CountedString word : words) {
			minimumCount = Math.min(minimumCount, word.getCount());
			maximumCount = Math.max(maximumCount, word.getCount());
		}

		/*
		 * Place the words in no overlapping bounding boxes.
		 */
		int index = 0;
		for (CountedString word : words) {
			float fontSize = countToFontSize(word.getCount());
			p.textSize(fontSize);

			float ww = p.textWidth(word.getString());
			float hh = (Math.abs(p.textAscent()) + Math.abs(p.textDescent()));

			boolean finished = false;

			while (!finished) {
				float bestxx = 0;
				float bestyy = 0;
				float bestdistance = Float.POSITIVE_INFINITY;
				boolean bestHorizontal = true;
				boolean found = false;

				for (int i = 0; i < 800; i++) {
					float xx = (random.nextFloat() - 0.5f) * width - ww / 2.f;
					float yy = (random.nextFloat() - 0.5f) * height - hh / 2.f;
					boolean horizontal = index == 0 || i % 8 > 0;

					BoundingBox b;

					if (horizontal)
						b = new BoundingBox(xx - 1, yy - 1, ww + 2, hh + 2);
					else
						b = new BoundingBox(xx - 1, yy - 1, hh + 2, ww + 2);

					boolean valid = true;
					for (WordCloudDrawable dd : drawables.values())
						if (b.intersect(dd.getBounds())) {
							valid = false;
							break;
						}

					if (valid) {
						found = true;
						float newdistance = xx * xx + yy * yy;
						if (newdistance < bestdistance) {
							bestdistance = newdistance;
							bestxx = xx;
							bestyy = yy;
							bestHorizontal = horizontal;
						}

					}
				}

				if (found) {
					BoundingBox b;
					if (bestHorizontal)
						b = new BoundingBox(bestxx - 1, bestyy - 1, ww + 2,
								hh + 2);
					else
						b = new BoundingBox(bestxx - 1, bestyy - 1, hh + 2,
								ww + 2);
					WordCloudDrawable d = new WordCloudDrawable(manager,
							location, word.getString(), fontSize,
							bestHorizontal, b);
					addWordDrawable(d);
					index++;
					break;

				} else {
					width += 8;
					height += 8;
				}
			}
		}
		if (index == 0)
			throw new IllegalStateException("No words were added!");
	}

	/**
	 * 
	 * @param drawable
	 */
	private void addWordDrawable(WordCloudDrawable drawable) {
		drawables.put(drawable.getPaperWord(), drawable);
		wordCloudSize = wordCloudSize.union(drawable.getBounds());
	}

	/**
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	public WordCloudDrawable getMouseOver(float mouseX, float mouseY) {
		for (WordCloudDrawable d : drawables.values())
			if (d.getScreenBox().mouseIn(mouseX, mouseY))
				return d;
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see acceleration.Bounded#getScreenBox()
	 */
	@Override
	public BoundingBox getScreenBox() {
		UnfoldingMap map = getVisualization().getMap();

		if (cachedScreenBox == null || map.getZoom() != cachedZoomLevel) {
			cachedZoomLevel = getVisualization().getDrawScale();

			ScreenPosition c = map.getScreenPosition(location);
			BoundingBox b = wordCloudSize;
			ScreenPosition leftup = new ScreenPosition(c.x + b.x
					* cachedZoomLevel, c.y + b.y * cachedZoomLevel);
			cachedScreenBox = new BoundingBox(leftup.x, leftup.y, b.width
					* cachedZoomLevel, b.height * cachedZoomLevel);
		}
		return cachedScreenBox;
	}

	/**
	 * Draws the word cloud.
	 * 
	 * @param p
	 * @param map
	 * @param scale
	 */
	public void draw(float alpha) {
		if (!data.hasVisibilePapers(manager.getFilter()))
			return;

		Iterator<PaperSet> it = paperSets.values().iterator();

		while (it.hasNext()) {
			PaperSet set = it.next();

			if (set.canBeDeleted()) {
				it.remove();
				continue;
			}
			set.update();
			set.draw(alpha);
		}

		for (WordCloudDrawable d : drawables.values())
			d.draw(alpha);
	}

	/**
	 * 
	 * @param word
	 */
	public void updatePaperSet(String word) {
		for (PaperSet set : paperSets.values())
			set.deactivate();
		if (word == null)
			return;
		if (paperSets.containsKey(word))
			paperSets.get(word).activate();
		else if (drawables.get(word) != null)
			paperSets.put(word, new PaperSet(manager, drawables.get(word),
					word, data));
	}

	/**
	 * Returns the word size for a word which occurs with the given count.
	 * 
	 * @param count
	 * @return
	 */
	private float countToFontSize(int count) {
		if (count == maximumCount) {
			return maximumFont * 2.f;
		} else {
			float numerator = count - minimumCount;
			float denominator = maximumCount - minimumCount;
			float scale = numerator / denominator;
			return (float) minimumFont * (1.f - scale) + (float) maximumFont
					* scale;
		}
	}

	/**
	 * Inner class which represents a word which occurs a given number of times.
	 * 
	 * @author niels
	 * 
	 */
	private class CountedString implements Comparable<CountedString> {
		private final String string;
		private final int count;

		private CountedString(String string, int count) {
			if (string == null)
				throw new NullPointerException("The given string was null!");
			if (count < 0)
				throw new IllegalArgumentException(
						"The given count was smaller than zero!");
			this.string = string;
			this.count = count;
		}

		public String getString() {
			return string;
		}

		public int getCount() {
			return count;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(CountedString o) {
			return -count + o.count;
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
}
