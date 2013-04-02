package wordcloud;

/**
 * Respresents a counted string.
 * 
 * @author niels
 * 
 */
public class CountedString implements Comparable<CountedString> {
	private final String string;
	private final int count;

	public CountedString(String string, int count) {
		if (string == null)
			throw new NullPointerException("The given string was null!");
		if (count < 0)
			throw new IllegalArgumentException(
					"The given count was smaller than zero!");
		this.string = string;
		this.count = count;
	}

	public String getString() {
		return string;
	}

	public int getCount() {
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CountedString o) {
		return -count + o.count;
	}

}
