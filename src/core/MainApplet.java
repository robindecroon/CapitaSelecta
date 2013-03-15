package core;

import processing.core.PApplet;
import util.FrameRateManager;
import codeanticode.glgraphics.GLConstants;
import drawables.AuthorMap;

/**
 * Download the distribution with examples for many more examples and features.
 */
public class MainApplet extends PApplet {
	// Serialization id for applets
	private static final long serialVersionUID = 1L;
	private AuthorMap map;
	private final int APPLET_WIDTH = 1200;
	private final int APPLET_HEIGHT = 800;
	private final FrameRateManager manager = new FrameRateManager(60);

	/*
	 * (non-Javadoc)
	 * 
	 * @see processing.core.PApplet#setup()
	 */
	@Override
	public void setup() {	
		size(APPLET_WIDTH, APPLET_HEIGHT, GLConstants.GLGRAPHICS);
		frameRate(60);
		map = new AuthorMap(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see processing.core.PApplet#draw()
	 */
	@Override
	public void draw() {
		manager.update();
		float scale = manager.getFrameRateDeviation();
		map.update(scale);
		map.draw();
	}
}
