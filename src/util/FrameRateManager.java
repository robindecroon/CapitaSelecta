package util;

/**
 * A class which manages fluctuations in frame rate. It returns a scale factor
 * 
 * @author niels
 * @version 0.1
 */
public class FrameRateManager {
	private final float frameRate;
	private final float invFrameRate;

	private final float AVERAGE_FACTOR = 0.3f;
	private float averageFrameRate;

	private long startTime;
	private long numberOfFrames = 0;
	private long lastUpdateTime;

	public static final float FRAMERATE = 30.f;
	public static final float INV_FRAMERATE = 1.f/FRAMERATE;
	
	public FrameRateManager(float frameRate) {
		if (frameRate <= 0)
			throw new IllegalArgumentException("Framerate should be positive");
		this.frameRate = frameRate;
		this.invFrameRate = 1.f / frameRate;
	}

	public void start() {
		lastUpdateTime = System.nanoTime();
		averageFrameRate = frameRate;
		startTime = System.nanoTime();
	}

	public double update() {
		long currentTime = System.nanoTime();
		long difference = currentTime - lastUpdateTime;
		double currentFramerate = 1000000000.0 / difference;

		averageFrameRate =(float) (AVERAGE_FACTOR * currentFramerate)
				+ (1.f - AVERAGE_FACTOR) * averageFrameRate;
		lastUpdateTime = currentTime;
		numberOfFrames++;

		return averageFrameRate;
	}
	
	public long getElapsedTime() {
		return System.nanoTime()-startTime;
	}
	
	public long getTotalNumberOfFrames() {
		return numberOfFrames;
	}

	public float getFrameRate() {
		return averageFrameRate;
	}

	public float getFrameRateDeviation() {
		return averageFrameRate * invFrameRate;
	}
	
	public static float convertFramesToTime(float nbOfFrames) {
		return nbOfFrames*INV_FRAMERATE;
	}
	public static float convertTimeToFrames(float seconds) {
		return seconds*FRAMERATE;
	}
}
