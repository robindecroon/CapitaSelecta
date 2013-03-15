/**
 * 
 */
package threads;

import java.util.List;
import java.util.regex.Pattern;

import util.KeywordColor;
import data.Database;
import data.Paper;

/**
 * @author Robin
 *
 */
public class KeywordRunnable implements Runnable {

	private Database db;
	private String keyword;
	private KeywordColor color;
	
	
	public KeywordRunnable(String keyword, KeywordColor color) {
		this.keyword = keyword;
		this.color = color;
	}
	
	@Override
	public void run() {
		db = Database.getInstance();
		
		List<Paper> papers = db.getPapers();
		
		for(Paper paper : papers) {
			if(containsIgnoreCase(paper.getFullText(),keyword)) {
				paper.setColor(color);
			}
		}
	}
	
	private boolean containsIgnoreCase(String s1, String s2) {
		return Pattern.compile(Pattern.quote(s1), Pattern.CASE_INSENSITIVE)
				.matcher(s2).find();
	}

}
