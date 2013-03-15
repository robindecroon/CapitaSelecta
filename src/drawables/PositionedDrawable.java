package drawables;

import de.fhpotsdam.unfolding.utils.ScreenPosition;
import processing.core.PApplet;

public abstract class PositionedDrawable extends Drawable {
	private float zoom = 0;

	private boolean dirtyPosition = true;
	private ScreenPosition cachedPosition;

	private boolean dirtyBoundingBox = true;
	private BoundingBox cachedBoundingBox;

	private boolean highLight = false;

	public PositionedDrawable(PApplet applet, float zoom) {
		super(applet);
	}

	public void setHighLight(boolean highLight) {
		this.highLight = highLight;
	}

	public boolean getHighLight() {
		return highLight;
	}

	public void setZoom(float zoom) {
		if (this.zoom == zoom)
			return;
		this.zoom = zoom;
		this.dirtyPosition = true;
		this.dirtyBoundingBox = true;
	}

	public float getZoom() {
		return zoom;
	}

	/*
	 * (non-Javadoc)
	 * @see drawables.Drawable#update(float)
	 */
	@Override
	public void update(float scale) {
		dirtyPosition = true;
		dirtyBoundingBox = true;
	}

	public ScreenPosition getScreenPosition() {
		if (dirtyPosition) {
			cachedPosition = calculateScreenPosition();
			dirtyPosition = false;
		}
		return cachedPosition;

	}

	public BoundingBox getBoundingBox() {
		if (dirtyBoundingBox) {
			cachedBoundingBox = calculateBoundingBox();
			dirtyBoundingBox = false;
		}
		return cachedBoundingBox;
	}

	protected abstract BoundingBox calculateBoundingBox();

	protected abstract ScreenPosition calculateScreenPosition();
}
