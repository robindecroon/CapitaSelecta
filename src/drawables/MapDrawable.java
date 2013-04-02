package drawables;

import processing.core.PApplet;
import acceleration.Bounded;
import de.fhpotsdam.unfolding.UnfoldingMap;

public abstract class MapDrawable implements Comparable<MapDrawable>, Bounded {
	private PApplet applet;
	private UnfoldingMap map;
	private boolean dirtyScreenBox = true;
	private BoundingBox cachedScreenBox;
	protected static int counter = 0;
	public final int id = counter++;
	private boolean highLight = false;

	public MapDrawable(PApplet applet, UnfoldingMap map) {
		setApplet(applet);
		setMap(map);
	}

	public void setApplet(PApplet applet) {
		if (applet == null)
			throw new NullPointerException("The given applet is null!");
		this.applet = applet;
	}

	public PApplet getApplet() {
		return applet;
	}

	public void setMap(UnfoldingMap map) {
		if (map == null)
			throw new NullPointerException("The given map is null!");
		this.map = map;
	}

	public UnfoldingMap getMap() {
		return map;
	}

	public abstract void update(float scale, boolean moved, boolean zoomed);

	public abstract void draw();

	public BoundingBox getScreenBox() {
		if (dirtyScreenBox) {
			cachedScreenBox = calculateScreenBox();
			dirtyScreenBox = false;
		}
		return cachedScreenBox;
	}

	public BoundingBox getBoundingBox() {
		return getScreenBox();
	}

	protected void markScreenBoxDirty() {
		dirtyScreenBox = true;
	}

	protected abstract BoundingBox calculateScreenBox();

	public void setHighLight(boolean highLight) {
		this.highLight = highLight;
	}

	public boolean getHighLight() {
		return highLight;
	}

	public int compareTo(MapDrawable m) {
		int cc = MapDrawable.counter;

		return id + (getHighLight() ? cc : 0) - m.id
				- (m.getHighLight() ? cc : 0);
	}
}
