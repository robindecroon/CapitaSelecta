package keywordmap;

public abstract class Drawable {
	private Visualization visualization;

	public Drawable(Visualization visualization) {
		if (visualization == null)
			throw new NullPointerException("The given visualization is null!");
		this.visualization = visualization;
	}

	public Visualization getVisualization() {
		return visualization;
	}

	public abstract void update();
	public abstract void draw(float alpha);

	public void postDraw(float scale, float alpha) {
		// To be overwritten.
	}
}
