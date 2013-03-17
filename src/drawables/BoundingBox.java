package drawables;

import processing.core.PApplet;

public class BoundingBox {
	public final float x;
	public final float y;
	public final float width;
	public final float height;
	private final boolean correct;

	public BoundingBox(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.correct = true;
	}

	public BoundingBox() {
		x = Float.POSITIVE_INFINITY;
		y = Float.POSITIVE_INFINITY;
		width = Float.NEGATIVE_INFINITY;
		height = Float.NEGATIVE_INFINITY;
		correct = false;
	}

	public boolean mouseIn(float x, float y) {
		if (x < this.x || x > this.x + width)
			return false;
		if (y < this.y || y > this.y + height)
			return false;
		return true;
	}

	public boolean intersect(BoundingBox box) {

		if (x + width < box.x || x > box.x + box.width)
			return false;
		if (y + height < box.y || y > box.y + box.height)
			return false;
		return true;
	}

	public BoundingBox union(BoundingBox b) {
		if (!correct)
			return new BoundingBox(b.x, b.y, b.width, b.height);
		float minx = Math.min(x, b.x);
		float miny = Math.min(y, b.y);
		float maxx = Math.max(x + width, b.x + b.width);
		float maxy = Math.max(y + height, b.y + b.height);

		return new BoundingBox(minx, miny, maxx - minx, maxy - miny);
	}

	public float getArea() {
		return width * height;
	}
	
	public void draw(PApplet a) {
		a.stroke(255,255,255);
		a.fill(0, 0, 0, 0);
		a.rect(x, y, width, height);
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + "," + width + "," + height + ")";
	}
}
