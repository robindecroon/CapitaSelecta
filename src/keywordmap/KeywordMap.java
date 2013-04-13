package keywordmap;

import processing.core.PApplet;
import wordcloud.WordCloudManager;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;

public class KeywordMap {
	private UnfoldingMap map;
	private PApplet applet;
	private final WordCloudManager manager;

	public KeywordMap(PApplet applet) {
		if (applet == null)
			throw new NullPointerException("The given map is null!");
		this.applet = applet;
		String connStr = "jdbc:sqlite:"
				+ applet.sketchPath("data/edmlakmap.mbtiles");
		map = new UnfoldingMap(applet, new MBTilesMapProvider(connStr));
		MapUtils.createDefaultEventDispatcher(applet, map);

		
		map.setTweening(true);
		map.setZoomRange(2.f, 8.f);
		map.zoomAndPanTo(new Location(50.85, 4.35), 8);

		manager = new WordCloudManager(applet, map, 2.f, 256.f, 2.f, 4.f, 8.f,
				16.f, 32.f, 64.f, 128.f, 256.f);

	}

	public PApplet getApplet() {
		return applet;
	}

	public float getZoom() {
		return map.getZoom();
	}

	public static float getScaledZoom(float zoom) {
		return Math.min(1.f, zoom / 36.f);
	}

	public void update(float scale) {

	}

	public void draw() {
////		Location mapUL = new Location(85, -180);
////		Location mapBR = new Location(-85, 180);
////
////		Location screenUL = map.getLocation(0, 0);
////		Location screenBR = map.getLocation(applet.width,applet.height);
//		ScreenPosition mapUL = map.getScreenPosition(new Location(85, -180));
//		ScreenPosition mapBR = map.getScreenPosition(new Location(-85, 180));
//
//		ScreenPosition screenUL = map.getScreenPosition(map.getTopLeftBorder());
//		ScreenPosition screenBR = map.getScreenPosition(map
//				.getBottomRightBorder());
//
//		//System.out.println("screen: " +screenUL.x + "," + screenUL.y);
//		//System.out.println("map: "+mapUL.x + "," + mapUL.y);
//
//		
////		if (screenUL.y < mapUL.y)
////			map.panTo(screenUL.x, mapUL.y);

		map.draw();
		manager.draw(map.getZoom());
	}
}
