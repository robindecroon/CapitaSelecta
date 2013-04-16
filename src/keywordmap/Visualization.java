package keywordmap;

import processing.core.PApplet;
import de.fhpotsdam.unfolding.UnfoldingMap;

public interface Visualization {
	public PApplet getApplet();
	public UnfoldingMap getMap();
	
	public float getZoom();
	public float getDrawScale();
	
	public void update(float frameRate);
	public void draw();
	
	public boolean leftClicked();
}
