package data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import filter.Filter;

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
	private HashMap<Filter, Boolean> visibility = new HashMap<Filter, Boolean>();

	public PaperWordData() {
	}

	public boolean hasVisibilePapers(Filter filter) {
		if (visibility.containsKey(filter))
			return visibility.get(filter);

		int passedPapers = 0;

		for (Set<Paper> sets : wordPaperMap.values())
			for (Paper paper : sets)
				if (filter.allowed(paper))
					passedPapers++;

		boolean result = passedPapers > 0;

		visibility.put(filter, result);

		return result;
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

	public Set<Paper> getPapers(String word) {
		return wordPaperMap.get(word);
	}

	public HashMap<String, Integer> getWordCount() {
		return new HashMap<String, Integer>(wordCount);
	}
}
