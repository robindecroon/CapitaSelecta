package keywordmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import processing.core.PApplet;
import util.Logger;
import wordcloud.WordCloud;
import data.Database;
import data.PaperWordData;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;

public class KeywordMap {
	private UnfoldingMap map;
	private PApplet applet;
	private final List<WordCloud> wordClouds = new ArrayList<WordCloud>();

	public KeywordMap(PApplet applet) {
		if (applet == null)
			throw new NullPointerException("The given map is null!");
		this.applet = applet;

		Database.getInstance();

		String connStr = "jdbc:sqlite:"
				+ applet.sketchPath("data/edmlakmap.mbtiles");
		map = new UnfoldingMap(applet, new MBTilesMapProvider(connStr));
		MapUtils.createDefaultEventDispatcher(applet, map);

		map.setTweening(true);
		map.setZoomRange(2.f, 8.f);
		map.zoomAndPanTo(new Location(50.85, 4.35), 8);

		Logger.Info("Accessing the words in the university");
		HashMap<Location, PaperWordData> data = Database.getInstance()
				.getWordsPerUniversity();
		Logger.Info("Cached all the words from the database");

		for (Entry<Location, PaperWordData> e : data.entrySet()) {
			PaperWordData d = e.getValue();

			WordCloud cloud = new WordCloud(applet, e.getKey(), d);
			wordClouds.add(cloud);

		}
	}

	public PApplet getApplet() {
		return applet;
	}

	public float getZoom() {
		return map.getZoom();

	}

	public void update(float scale) {

	}

	public void draw() {
		map.draw();

		PApplet a = getApplet();
		for (WordCloud cloud : wordClouds)
			cloud.draw(a, map, Math.min(1.f, (float) map.getZoom() / 36.f));

	}

}
