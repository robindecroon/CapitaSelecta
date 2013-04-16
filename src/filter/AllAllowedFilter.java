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
		// TODO Auto-generated method stub
		return null;
	}
}
