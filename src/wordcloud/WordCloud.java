package wordcloud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import processing.core.PApplet;
import data.PaperWordData;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import drawables.BoundingBox;

public class WordCloud {
	private int minimumCount;
	private int maximumCount;
	private int minimumFont = 12;
	private int maximumFont = 24;
	private Location location;
	private List<WordCloudDrawable> drawables = new ArrayList<WordCloudDrawable>();

	public WordCloud(PApplet p, Location location, PaperWordData data) {
		this.location = location;

		int width = 48;
		int height = 48;

		minimumCount = Integer.MAX_VALUE;
		maximumCount = Integer.MIN_VALUE;

		Random random = new Random(System.currentTimeMillis());

		List<CountedString> allWords = data.getCountedWords();
		List<CountedString> words =new ArrayList<CountedString>();
		for(int i=0;i<Math.min(10, allWords.size());i++)
			words.add(allWords.get(i));
		

		for (CountedString word : words) {
			minimumCount = Math.min(minimumCount, word.getCount());
			maximumCount = Math.max(maximumCount, word.getCount());
		}

		Collections.sort(words);

		for (CountedString word : words) {
			float fontSize = countToFontSize(word.getCount());
			p.textSize(fontSize);

			float ww = p.textWidth(word.getString());
			float hh =2* Math.abs(p.textAscent()) + Math.abs(p.textDescent());

			boolean finished = false;

			while (!finished) {
				float bestxx = 0;
				float bestyy = 0;
				float bestdistance = Float.POSITIVE_INFINITY;
				boolean found = false;

				for (int i = 0; i < 800; i++) {
					float xx = (random.nextFloat() - 0.5f) * width;
					float yy = (random.nextFloat() - 0.5f) * height;

					BoundingBox b = new BoundingBox(xx - 1, yy - 1, ww + 2,
							hh + 2);

					boolean valid = true;
					for (WordCloudDrawable dd : drawables)
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
						}

					}
				}

				if (found) {
					BoundingBox b = new BoundingBox(bestxx - 1, bestyy - 1,
							ww + 2, hh + 2);
					WordCloudDrawable d = new WordCloudDrawable(
							word.getString(), fontSize, b);
					drawables.add(d);

					break;

				} else {
					width += 8;
					height += 8;
				}
			}

		}
	}

	public void draw(PApplet p, UnfoldingMap map, float scale) {
		ScreenPosition screen = map.getScreenPosition(location);

		p.smooth();
		for (WordCloudDrawable d : drawables) {
			d.draw(p, screen.x, screen.y, scale);
		}
	}

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
}
