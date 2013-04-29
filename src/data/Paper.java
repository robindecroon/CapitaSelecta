package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import util.Dictionary;
import de.fhpotsdam.unfolding.geo.Location;

public class Paper {
	private String name;
	private String fullText;
	private int year;
	private Conference conference;
	private List<Author> authors = new ArrayList<Author>();
	private Location firstLocation;

	// Paper content
	private final ArrayList<PaperWord> mostOccuring = new ArrayList<PaperWord>();
	private HashMap<String, Integer> seperateWords = new HashMap<String, Integer>();

	public Paper(String name, String fullText, int year, Conference conference) {
		setName(name);
		setFullText(fullText);
		setYear(year);
		setConference(conference);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name == null)
			throw new NullPointerException("The given name is null!");
		this.name = name;
	}

	public List<Author> getAuthors() {
		return new ArrayList<Author>(authors);
	}

	public void addAuthor(Author author) {
		if (author == null)
			throw new NullPointerException("The given author is null!");
		authors.add(author);

		if (authors.size() == 1)
			firstLocation = author.getUniversity().getLocation();
	}

	public String getFullText() {
		return fullText;
	}

	public void setFullText(String fullText) {
		if (fullText == null)
			throw new NullPointerException("The given fulltext is null!");
		this.fullText = fullText;

		String[] split = fullText.split(" ");
		for (String string : split) {
			String word = Dictionary.getInstance().addWord(string);

			if (word.equals(""))
				continue;

			if (seperateWords.containsKey(word))
				seperateWords.put(word, seperateWords.get(word) + 1);
			else
				seperateWords.put(word, 1);
			WordDatabase.getInstance().addWord(word);
		}

		for (Entry<String, Integer> e : seperateWords.entrySet())
			mostOccuring.add(new PaperWord(e.getKey(), e.getValue(), this));
		Collections.sort(mostOccuring);
	}

	/**
	 * Returns the location of the university of the first author.
	 * 
	 * @return
	 */
	public Location getFirstLocation() {
		if (firstLocation == null)
			throw new IllegalStateException(
					"The location has not been initialized yet");
		return firstLocation;
	}

	public boolean containsWord(String word) {
		if (seperateWords.keySet().contains(word.trim().toLowerCase()))
			return true;
		else
			return false;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Conference getConference() {
		return conference;
	}

	public void setConference(Conference conference) {
		if (conference == null)
			throw new NullPointerException("The given conference is null!");
		this.conference = conference;
	}

	public List<PaperWord> getRelevantWords() {
		List<PaperWord> result = new ArrayList<PaperWord>();
		Database d = Database.getInstance();

		for (PaperWord w : mostOccuring)
			if (!d.isBadWord(w.word))
				result.add(w);
		return result;
	}

	public List<PaperWord> getAllWords() {
		return getMostOccuringWords(mostOccuring.size());
	}

	public List<PaperWord> getMostOccuringWords(int n) {
		List<PaperWord> result = new ArrayList<PaperWord>();

		for (int i = 0; i < Math.min(mostOccuring.size(), n); i++)
			result.add(mostOccuring.get(i));

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((conference == null) ? 0 : conference.hashCode());
		result = prime * result
				+ ((fullText == null) ? 0 : fullText.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + year;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Paper other = (Paper) obj;
		if (authors == null) {
			if (other.authors != null)
				return false;
		} else {
			if (authors.size() != other.authors.size())
				return false;
			for (int i = 0; i < authors.size(); i++)
				if (!authors.get(i).equals(other.authors.get(i)))
					return false;
		}
		if (conference != other.conference)
			return false;
		if (fullText == null) {
			if (other.fullText != null)
				return false;
		} else if (!fullText.equals(other.fullText))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (year != other.year)
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName() + " with " + getAuthors().size() + " authors";
	}
}
