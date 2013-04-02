package keywordmap;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import wordcloud.CountedString;
import wordcloud.WordCloud;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;

public class KeywordMap {
	private UnfoldingMap map;
	private PApplet applet;

	private WordCloud cloud;

	public KeywordMap(PApplet applet) {
		if (applet == null)
			throw new NullPointerException("The given map is null!");
		this.applet = applet;

		String connStr = "jdbc:sqlite:" + applet.sketchPath("data/edmlakmap.mbtiles");
		map = new UnfoldingMap(applet, new MBTilesMapProvider(connStr));
		MapUtils.createDefaultEventDispatcher(applet, map);
//		map.setZoomRange(2, 4);

//		map = new UnfoldingMap(applet);
//		MapUtils.createDefaultEventDispatcher(applet, map);
//		map.setTweening(true);
		map.setZoomRange(2.f, 256.f);
//		map.zoomAndPanTo(new Location(50.85, 4.35), 8);

		List<CountedString> test = new ArrayList<CountedString>();
		test.add(new CountedString("hey", 10));
		test.add(new CountedString("what", 9));
		test.add(new CountedString("up", 3));
		test.add(new CountedString("hey", 2));
		test.add(new CountedString("what", 3));
		test.add(new CountedString("up", 20));
		test.add(new CountedString("hey", 5));
		test.add(new CountedString("what", 2));
		test.add(new CountedString("up", 1));
		test.add(new CountedString("hey", 5));
		test.add(new CountedString("what", 14));
		test.add(new CountedString("up", 12));
		test.add(new CountedString("hey", 16));
		test.add(new CountedString("what", 2));
		test.add(new CountedString("up", 5));

		cloud = new WordCloud(applet, new Location(50, 30), test);
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
		cloud.draw(getApplet(), map, 1.f);
	}

}
