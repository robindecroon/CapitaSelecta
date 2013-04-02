package acceleration;

import java.util.List;

import drawables.BoundingBox;

public interface Acceleration<T extends Bounded> {
	public BoundingBox getBoundingBox();

	public List<T> getElements(BoundingBox b);
}
