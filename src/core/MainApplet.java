package core;

import processing.core.PApplet;
import util.FrameRateManager;
import wordcloud.WordCloud;
import codeanticode.glgraphics.GLConstants;
import drawables.visualization.AuthorMap;

import java.util.ArrayList;
import java.util.List;
import wordcloud.CountedString;

/**
 * Download the distribution with examples for many more examples and features.
 */
public class MainApplet extends PApplet {
	// Serialization id for applets
	private static final long serialVersionUID = 1L;
	private AuthorMap map;
	private final int APPLET_WIDTH = 1024;
	private final int APPLET_HEIGHT = 800;
	private final FrameRateManager manager = new FrameRateManager(30);
	private WordCloud cloud;

	/*
	 * (non-Javadoc)
	 * 
	 * @see processing.core.PApplet#setup()
	 */
	@Override
	public void setup() {
		size(APPLET_WIDTH, APPLET_HEIGHT, GLConstants.GLGRAPHICS);
		frameRate(30);
		map = new AuthorMap(this);

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

		cloud = new WordCloud(this,test,16,16);
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

		fill(0, 0, 0);
		textSize(8);
		textAlign(PApplet.LEFT);
		text("framerate: " + manager.getFrameRate(), 16, 16);
		cloud.draw(this, 256, 256);
	}
}
