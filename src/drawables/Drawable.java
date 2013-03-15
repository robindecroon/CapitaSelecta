package drawables;

import processing.core.PApplet;

public abstract class Drawable {
	private PApplet applet;

	public Drawable(PApplet applet) {
		setApplet(applet);
	}

	public void setApplet(PApplet applet) {
		if (applet == null)
			throw new NullPointerException("The given applet is null!");
		this.applet = applet;
	}

	public PApplet getApplet() {
		return applet;
	}

	public abstract void update(float scale);

	public abstract void draw();
}
