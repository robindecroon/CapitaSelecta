package drawables;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import processing.core.PApplet;

public abstract class PositionedDrawable extends MapDrawable {
	private float zoom = 0;

	private boolean dirtyPosition = true;
	private ScreenPosition cachedPosition;

	

	public PositionedDrawable(PApplet applet, UnfoldingMap map) {
		super(applet, map);
		this.zoom = getMap().getZoom();
	}


	public void setZoom(float zoom) {
		if (this.zoom == zoom)
			return;
		this.zoom = zoom;
		markScreenPositionDirty();
		markScreenBoxDirty();
	}

	public float getZoom() {
		return zoom;
	}

	protected void markScreenPositionDirty() {
		dirtyPosition = true;
	}

	public ScreenPosition getScreenPosition() {
		if (dirtyPosition) {
			cachedPosition = calculateScreenPosition();
			dirtyPosition = false;
		}
		return cachedPosition;
	}

	protected abstract ScreenPosition calculateScreenPosition();
}
