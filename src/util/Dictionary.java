package util;

import java.util.HashSet;
import java.util.Set;

public class Dictionary {
	private static Dictionary instance;
	private HashSet<String> words = new HashSet<String>();
	private static final String legalCharacters = "abcdefghijklmnopqrstuvwxyz";

	private Dictionary() {
	}

	public Set<String> getWords() {
		return words;
	}

	public static Dictionary getInstance() {
		if (instance == null)
			instance = new Dictionary();
		return instance;
	}

	public String addWord(String word) {
		String result = format(word);
		words.add(result);
		return result;
	}

	public static String format(String word) {
		String trimmed = word.trim().toLowerCase();
		String result = "";
		for (int i = 0; i < trimmed.length(); i++)
			if (legalCharacters.contains("" + trimmed.charAt(i)))
				result += trimmed.charAt(i);
		return result;
	}
}
