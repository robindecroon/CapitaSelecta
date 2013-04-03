package data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This class encapsulates the word count over multiple papers.
 * 
 * It allows to retrieve for a word, in which papers it occured, and for each
 * word what the total number of occurences was over all the papers.
 * 
 * @author niels
 * 
 */
public class PaperWordData {
	private HashMap<String, Set<Paper>> wordPaperMap = new HashMap<String, Set<Paper>>();
	private HashMap<String, Integer> wordCount = new HashMap<String, Integer>();

	public PaperWordData() {
	}

	public void addWord(String word, Paper paper, int count) {
		if (!wordPaperMap.containsKey(word))
			wordPaperMap.put(word, new HashSet<Paper>());
		wordPaperMap.get(word).add(paper);

		if (!wordCount.containsKey(word))
			wordCount.put(word, count);
		else
			wordCount.put(word, wordCount.get(word) + count);
	}

	public HashMap<String, Integer> getWordCount() {
		return new HashMap<String, Integer>(wordCount);
	}
}
