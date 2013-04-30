package acceleration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import util.Logger;

import core.BoundingBox;

public class MultiThreadPruning<T extends Bounded> implements Acceleration<T> {
	private final List<T> visible = new ArrayList<T>();
	private final Collection<T> drawables;
	private BoundingBox bounds = new BoundingBox();
	private List<PruneThread<T>> threads = new ArrayList<PruneThread<T>>();

	public MultiThreadPruning(Collection<T> drawables) {
		this.drawables = drawables;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see acceleration.Acceleration#getBoundingBox()
	 */
	@Override
	public BoundingBox getBoundingBox() {
		return bounds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see acceleration.Acceleration#getDrawables(drawables.BoundingBox)
	 */
	@Override
	public List<T> getElements(BoundingBox b) {
		ExecutorService s = Executors.newFixedThreadPool(8);

		threads.clear();
		for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
			PruneThread<T> t = new PruneThread<T>(b);
			threads.add(t);
		}

		int index = 0;
		for (T t : drawables)
			threads.get((index++) % threads.size()).add(t);

		for (Thread t : threads)
			s.submit(t);
		s.shutdown();

		try {
			s.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			Logger.Severe("pruning thread was interrupted unexecpectedly", e);
		}

		for (PruneThread<T> t : threads) {
			bounds = bounds.union(t.getBounds());
			visible.addAll(t.getVisibles());
		}
		return visible;
	}

	/**
	 * 
	 * @author niels
	 * 
	 * @param <K>
	 */
	public class PruneThread<K extends Bounded> extends Thread {
		private List<K> visibles = new ArrayList<K>();
		private List<K> drawables = new ArrayList<K>();
		private BoundingBox bounds;
		private BoundingBox locationBox;

		public PruneThread(BoundingBox locationBox) {
			this.locationBox = locationBox;
		}

		public void add(K k) {
			drawables.add(k);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			bounds = new BoundingBox();

			for (K k : drawables) {
				if (k.getScreenBox().intersect(locationBox)) {
					visibles.add(k);
					bounds = bounds.union(k.getScreenBox());
				}
			}
		}

		public List<K> getVisibles() {
			return visibles;
		}

		public BoundingBox getBounds() {
			return bounds;
		}
	}
}
