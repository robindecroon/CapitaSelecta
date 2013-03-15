package drawables;

public class BoundingBox {
	public final float x;
	public final float y;
	public final float width;
	public final float height;

	public BoundingBox(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public boolean mouseIn(float x, float y) {
		if (x < this.x || x > this.x + width)
			return false;
		if (y < this.y || y > this.y + height)
			return false;
		return true;
	}
}
