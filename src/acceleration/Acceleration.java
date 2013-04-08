package acceleration;

import java.util.List;

import core.BoundingBox;


public interface Acceleration<T extends Bounded> {
	public BoundingBox getBoundingBox();

	public List<T> getElements(BoundingBox b);
}
