package data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import util.Logger;

public class WordDatabase {
	private static WordDatabase singleton;

	private HashMap<String, Integer> allWords = new HashMap<String, Integer>();

	private WordDatabase() {
	}

	public static WordDatabase getInstance() {
		if (singleton == null)
			singleton = new WordDatabase();
		return singleton;
	}

	public void addWord(String string) {
		String word = string.trim().toLowerCase();

		if (allWords.containsKey(word))
			allWords.put(word, allWords.get(word) + 1);
		else
			allWords.put(word, 2);

	}

	public void exportWordList() {
		HashMap<Integer, List<String>> output = new HashMap<Integer, List<String>>();

		for (Entry<String, Integer> e : allWords.entrySet()) {
			if (!output.containsKey(e.getValue()))
				output.put(e.getValue(), new ArrayList<String>());
			output.get(e.getValue()).add(e.getKey());
		}

		Set<Integer> hashedCount = new HashSet<Integer>(allWords.values());
		List<Integer> descendedCount = new ArrayList<Integer>(hashedCount);
		Collections.sort(descendedCount);
		List<Integer> count = new ArrayList<Integer>();
		for(int i=descendedCount.size()-1;i>=0;i--)
			count.add(descendedCount.get(i));
				
		try {
			FileWriter writer = new FileWriter(new File("allwords.txt"));
			BufferedWriter w = new BufferedWriter(writer);

			for (int i=0;i<count.size();i++) {
				List<String> words = output.get(count.get(i));
				for (String word : words) {
					w.write(word);
					w.newLine();
				}
			}

			w.close();
		} catch (IOException e) {
			Logger.Warning("could not export the word frequency list");
		}
	}
}
