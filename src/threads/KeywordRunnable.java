/**
 * 
 */
package threads;

import java.util.List;

import util.KeywordColor;
import data.Database;
import data.Paper;

/**
 * @author Robin
 * 
 */
public class KeywordRunnable implements Runnable {

	private Database db;
	private String keyword1;
	private String keyword2;

	public KeywordRunnable(String keyword1, String keyword2) {
		this.keyword2 = keyword2;
		this.keyword1 = keyword1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		db = Database.getInstance();

		List<Paper> papers = db.getPapers();

		for (Paper paper : papers) {
			if (keyword1!=null&&!keyword1.equals("") && paper.containsWord(keyword1))
				paper.setColor(KeywordColor.GREEN);
			else if (keyword2!=null&&!keyword2.equals("") && paper.containsWord(keyword2))
				paper.setColor(KeywordColor.RED);
			else
				paper.setColor(KeywordColor.BLUE);
		}
	}
	// private boolean containsIgnoreCase(String s1, String s2) {
	// return Pattern.compile(Pattern.quote(s1), Pattern.CASE_INSENSITIVE)
	// .matcher(s2).find();
	// }

}
