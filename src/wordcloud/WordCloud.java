package wordcloud;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class WordCloud {
	private int totalNumberOfCharacters;
	private HashMap<Integer, List<String>> wordSet;
	private int gridWidth;
	private int gridHeight;
	private boolean[][] free;

	public WordCloud(Collection<String> words) {
		initialize(words);
	}

	private void initialize(Collection<String> words) {
		totalNumberOfCharacters = 0;

		for (String word : words) {
			int length = word.length();
			totalNumberOfCharacters += length;

			if (!wordSet.containsKey(length))
				wordSet.put(length, new ArrayList<String>());
			wordSet.get(length).add(word);
		}

	}
}
