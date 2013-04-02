package data;

public class PaperWord implements Comparable<PaperWord> {
	public final String word;
	public final int occurences;
	public final Paper paper;

	public PaperWord(String word, int occurences, Paper paper) {
		this.word = word;
		this.occurences = occurences;
		this.paper = paper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(PaperWord o) {
		return o.occurences - occurences;
	}
}
