package acceleration.bvh;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import drawables.BoundingBox;

import acceleration.Acceleration;
import acceleration.Bounded;

public class BVH<T extends Bounded> implements Acceleration<T> {
	private BVH<T> left;
	private BVH<T> right;
	private List<T> elements;
	private BoundingBox box;

	/**
	 * 
	 * @param list
	 */
	public BVH(Collection<T> e, int axis) {
		List<T> list = new ArrayList<T>(e);
		box = new BoundingBox();
		for (T t : list)
			box = box.union(t.getBoundingBox());

		if (list.size() <= 4) {
			elements = new ArrayList<T>(list);
			return;
		}

		Collections.sort(list, getComparator(axis));
		List<T> left = new ArrayList<T>();
		List<T> right = new ArrayList<T>();
		for (int i = 0; i < list.size(); i++) {
			if (i < list.size() / 2)
				left.add(list.get(i));
			else
				right.add(list.get(i));
		}

		this.left = new BVH<T>(left, axis + 1);
		this.right = new BVH<T>(right, axis + 1);
	}

	public Comparator<T> getComparator(int axis) {
		if (axis % 2 == 0)
			return getXComparator();
		else
			return getYComparator();
	}

	/**
	 * 
	 * @return
	 */
	public Comparator<T> getXComparator() {
		return new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				BoundingBox b1 = o1.getBoundingBox();
				BoundingBox b2 = o2.getBoundingBox();

				return (int) (b1.x + b1.width / 2.f - b2.x - b2.width / 2.f + 0.5f);
			}
		};
	}

	/**
	 * 
	 * @return
	 */
	public Comparator<T> getYComparator() {
		return new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				BoundingBox b1 = o1.getBoundingBox();
				BoundingBox b2 = o2.getBoundingBox();

				return (int) (b1.x + b1.width / 2.f - b2.x - b2.width / 2.f + 0.5f);
			}
		};
	}

	@Override
	public BoundingBox getBoundingBox() {
		return box;
	}

	@Override
	public List<T> getElements(BoundingBox b) {
		if (box.intersect(b)) {
			List<T> result = new ArrayList<T>();
			if (left != null && right != null) {
				result.addAll(left.getElements(b));
				result.addAll(right.getElements(b));
			} else
				result.addAll(elements);
			return result;
		} else
			return new ArrayList<T>();
	}
}
