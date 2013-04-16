package core;

import keywordmap.KeywordMap;
import processing.core.PApplet;
import util.FrameRateManager;
import codeanticode.glgraphics.GLConstants;

/**
 * Download the distribution with examples for many more examples and features.
 */
public class MainApplet extends PApplet {
	// Serialization id for applets
	private static final long serialVersionUID = 1L;
	public static final int APPLET_WIDTH = 1024;
	public static final int APPLET_HEIGHT = 800;
	private final FrameRateManager manager = new FrameRateManager(30);
	private KeywordMap map;

	/*
	 * (non-Javadoc)
	 * 
	 * @see processing.core.PApplet#setup()
	 */
	@Override
	public void setup() {
		size(APPLET_WIDTH, APPLET_HEIGHT, GLConstants.GLGRAPHICS);
		frameRate(30);
		map = new KeywordMap(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see processing.core.PApplet#draw()
	 */
	@Override
	public void draw() {
manager.update();
map.update(manager.getFrameRate());
map.draw();

fill(0, 0, 0);
textSize(8);
textAlign(PApplet.LEFT);
text("framerate: " + manager.getFrameRate(), 16, 16);
	}

	public KeywordMap getMap() {
		return map;
	}
}
