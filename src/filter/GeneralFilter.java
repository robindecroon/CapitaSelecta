package filter;

import java.util.ArrayList;
import java.util.List;

import data.Conference;
import data.Paper;

public class GeneralFilter implements Filter {
	private final List<Integer> years = new ArrayList<Integer>();
	private final List<Conference> conferences = new ArrayList<Conference>();

	public GeneralFilter(List<Integer> years, List<Conference> conference) {
		this.years.addAll(years);
		this.conferences.addAll(conference);
	}

	public GeneralFilter() {
		for (int i = 2008; i <= 2012; i++)
			years.add(i);
		for (Conference c : Conference.values())
			this.conferences.add(c);
	}

	public GeneralFilter(List<Integer> years, Conference... conferences) {
		this.years.addAll(years);
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
		boolean goodYear = years.contains(paper.getYear());
		boolean goodCenference = conferences.contains(paper.getConference());
		return goodYear && goodCenference;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((conferences == null) ? 0 : conferences.hashCode());
		result = prime * result + ((years == null) ? 0 : years.hashCode());
		return result;
	}

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
		if (years == null) {
			if (other.years != null)
				return false;
		} else if (!years.equals(other.years))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see filter.Filter#getColor(data.Paper)
	 */
	@Override
	public PaperColor getColor(Paper paper) {
		if (paper.getYear() == 2008)
			return PaperColor.C2008;
		else if (paper.getYear() == 2009)
			return PaperColor.C2009;
		else if (paper.getYear() == 2010)
			return PaperColor.C2010;
		else if (paper.getYear() == 2011)
			return PaperColor.C2011;
		else if (paper.getYear() == 2012)
			return PaperColor.C2012;
		else
			throw new IllegalStateException();
	}
}
