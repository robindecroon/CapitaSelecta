package filter;

import java.util.ArrayList;
import java.util.List;

import data.Conference;
import data.Paper;

public class GeneralFilter implements Filter {
	private int startYear;
	private int endYear;
	private final List<Conference> conferences = new ArrayList<Conference>();

	public GeneralFilter(int startYear, int endYear, List<Conference> conference) {
		this.startYear = startYear;
		this.endYear = endYear;
		this.conferences.addAll(conference);
	}

	public GeneralFilter(int startYear, int endYear, Conference... conferences) {
		this.startYear = startYear;
		this.endYear = endYear;

		for (Conference conference : conferences)
			this.conferences.add(conference);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see filter.Filter#allowed(data.Paper)
	 */
	@Override
	public boolean allowed(Paper paper) {
		boolean goodYear = paper.getYear() >= startYear
				&& paper.getYear() <= endYear;
		boolean goodCenference = conferences.contains(paper.getConference());
		return goodYear&&goodCenference;
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
				+ ((conferences == null) ? 0 : conferences.hashCode());
		result = prime * result + endYear;
		result = prime * result + startYear;
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
		GeneralFilter other = (GeneralFilter) obj;
		if (conferences == null) {
			if (other.conferences != null)
				return false;
		} else if (!conferences.equals(other.conferences))
			return false;
		if (endYear != other.endYear)
			return false;
		if (startYear != other.startYear)
			return false;
		return true;
	}
}
