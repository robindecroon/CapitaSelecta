package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import wordcloud.CountedString;

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

	public List<CountedString> getCountedWords() {
		List<CountedString> result = new ArrayList<CountedString>();

		for (Entry<String, Integer> e : wordCount.entrySet())
			result.add(new CountedString(e.getKey(), e.getValue()));
		Collections.sort(result);
		
		return result;
	}
}
