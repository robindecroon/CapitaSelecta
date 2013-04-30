package keywordmap;

import processing.core.PApplet;
import swt.SWTGui;
import wordcloud.WordCloudManager;
import core.BoundingBox;
import data.Database;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;

public class KeywordMap implements Visualization {
	private BoundedUnfoldingMap map;
	private PApplet applet;
	private final WordCloudManager manager;

	private boolean leftDown = false;
	private boolean leftClicked = false;
	private long leftTime = 0;

	private Location lastLocation;
	private float previousZoom;
	private boolean moved;

	/**
	 * 
	 * @param applet
	 */
	public KeywordMap(PApplet applet) {
		if (applet == null)
			throw new NullPointerException("The given map is null!");
		Database.getInstance();

		this.applet = applet;
		String connStr = "jdbc:sqlite:"
				+ applet.sketchPath("data/edmlakmap.mbtiles");
		map = new BoundedUnfoldingMap(applet, new MBTilesMapProvider(connStr));
		MapUtils.createDefaultEventDispatcher(applet, map);

		map.setTweening(true);
		map.setZoomRange(2.f,
				(float) (Math.log(getMaximumZoom()) / Math.log(2)));
		map.zoomAndPanTo(new Location(50.85, 4.35), 4);

		manager = new WordCloudManager(this);
	}

	public float getMinimumZoom() {
		return 2.f;
	}

	public float getMaximumZoom() {
		return 256.f;
	}

	public float getZoom() {
		return map.getZoom();
	}

	public WordCloudManager getWordCloudManager() {
		return manager;
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
		if (SWTGui.instance!=null)
			SWTGui.instance.canChange=true;
		
		leftClicked = false;
		if (!leftDown && applet.mousePressed)
			leftTime = System.currentTimeMillis();
		else if (!applet.mousePressed && leftDown
				&& System.currentTimeMillis() - leftTime < 300)
			leftClicked = true;

		leftDown = applet.mousePressed;

		moved = false;
		if (previousZoom != getZoom())
			moved = true;
		if (lastLocation == null
				|| !lastLocation.equals(map.getTopLeftBorder()))
			moved = true;
		previousZoom = getZoom();
		lastLocation = map.getTopLeftBorder();
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
		return Math.max(0.2f, Math.min(1.f, map.getZoom() * 0.0277f));
	}

	/**
	 * 
	 * @return
	 */
	public BoundingBox getScreenBounds() {
		ScreenPosition l = map.getScreenPosition(map.getTopLeftBorder());
		ScreenPosition r = map.getScreenPosition(map.getBottomRightBorder());

		return new BoundingBox(l.x, l.y, Math.abs(r.x - l.x), Math.abs(r.y
				- l.y));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see keywordmap.Visualization#moved()
	 */
	@Override
	public boolean moved() {
		return moved;
	}
}
