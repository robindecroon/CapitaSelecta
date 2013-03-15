package util;

import java.util.Random;

public class RandomGenerator {
	private static final Random random = new Random(System.currentTimeMillis());

	public static float RandomFloat() {
		return random.nextFloat();
	}
}
