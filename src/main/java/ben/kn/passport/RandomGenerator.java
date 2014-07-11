package ben.kn.passport;

import java.util.Random;

class RandomGenerator {
	private static Random random = new Random();

	public static String generateUniqueString(int length) {
		return "" + generateRandomBetween(0, length + 1);
	}

	public static int generateRandomBetween(int min, int max) {
		int randomNumber = random.nextInt(max - min + 1);
		randomNumber += min;
		return randomNumber;
	}
}