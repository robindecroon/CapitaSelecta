package util;

import java.util.Random;

public class RNG {
	public static final Random random = new Random();
	
	public static int nextInt(int max) {
		return random.nextInt(max);
	}
}
