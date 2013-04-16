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
}
