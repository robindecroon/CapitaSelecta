package acceleration;

import java.util.Collection;
import java.util.List;

import drawables.BoundingBox;
import drawables.MapDrawable;

public interface Acceleration<T extends MapDrawable> {
	public int size();

	public BoundingBox getBoundingBox();

	public List<T> getDrawables(BoundingBox b);
	
	public Collection<T> getAllDrawables();
}
