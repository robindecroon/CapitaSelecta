package keywordmap;

import processing.core.PApplet;
import wordcloud.WordCloudManager;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;

public class KeywordMap implements Visualization {
	private BoundedUnfoldingMap map;
	private PApplet applet;
	private final WordCloudManager manager;

	private boolean leftDown = false;
	private boolean leftClicked = false;
	private long leftTime = 0;

	/**
	 * 
	 * @param applet
	 */
	public KeywordMap(PApplet applet) {
		if (applet == null)
			throw new NullPointerException("The given map is null!");
		this.applet = applet;
		String connStr = "jdbc:sqlite:"
				+ applet.sketchPath("data/edmlakmap.mbtiles");
		map = new BoundedUnfoldingMap(applet, new MBTilesMapProvider(connStr));
		MapUtils.createDefaultEventDispatcher(applet, map);

		map.setTweening(true);
		map.setZoomRange(2.f, 8.f);
		map.zoomAndPanTo(new Location(50.85, 4.35), 8);

		manager = new WordCloudManager(this, 2.f, 256.f, 2.f, 4.f, 8.f, 16.f,
				32.f, 64.f, 128.f, 256.f);
	}

	public float getZoom() {
		return map.getZoom();
	}

	public static float getScaledZoom(float zoom) {
		return Math.min(1.f, zoom / 36.f);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see keywordmap.Visualization#getApplet()
	 */
	public PApplet getApplet() {
		return applet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see keywordmap.Visualization#draw(float)
	 */
	@Override
	public void draw() {
		map.draw();
		manager.update();
		manager.draw(1.f);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see keywordmap.Visualization#getMap()
	 */
	@Override
	public UnfoldingMap getMap() {
		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see keywordmap.Visualization#update(float)
	 */
	@Override
	public void update(float frameRate) {
		leftClicked=false;
		if (!leftDown && applet.mousePressed)
			leftTime = System.currentTimeMillis();
		else if (!applet.mousePressed && leftDown
				&& System.currentTimeMillis() - leftTime < 200)
			leftClicked = true;

		leftDown = applet.mousePressed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see keywordmap.Visualization#leftClicked()
	 */
	@Override
	public boolean leftClicked() {
		return leftClicked;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see keywordmap.Visualization#getDrawScale()
	 */
	@Override
	public float getDrawScale() {
		return Math.min(1.f, map.getZoom() * 0.0277f);
	}
}
