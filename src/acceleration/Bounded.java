package acceleration;

import core.BoundingBox;

public interface Bounded {
	public BoundingBox getBoundingBox();
	public BoundingBox getScreenBox();
}
