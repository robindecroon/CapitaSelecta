package data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import util.Logger;
import wordcloud.CountedString;

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
		List<CountedString> list = new ArrayList<CountedString>();

		for (Entry<String, Integer> e : allWords.entrySet())
			list.add(new CountedString(e.getKey(), e.getValue()));

		Collections.sort(list);

		try {
			FileWriter writer = new FileWriter(new File("allwords.txt"));
			BufferedWriter w = new BufferedWriter(writer);

			for (CountedString string : list) {
				w.write(string.getString());
				w.newLine();
			}
			w.close();
		} catch (IOException e) {
			Logger.Warning("could not export the word frequency list");
		}
	}

}
