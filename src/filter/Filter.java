package filter;

import data.Paper;

public interface Filter {
	public boolean allowed(Paper paper);

	public PaperColor getColor(Paper paper);
}
