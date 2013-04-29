package filter;

import data.Paper;

public class AllAllowedFilter implements Filter {
	private AllAllowedFilter() {
	}

	private static AllAllowedFilter instance;

	public static AllAllowedFilter getInstance() {
		if (instance == null)
			instance = new AllAllowedFilter();
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see filter.Filter#allowed(data.Paper)
	 */
	@Override
	public boolean allowed(Paper paper) {
		return true;
	}

	/*
	 * (non-Javadoc)
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
