package database;
public class SQLPaper {

	private String title;
	private int year;
	private String conference;
	private String fullText;
	
	public SQLPaper(String t, int y, String c, String f) {
		title = t;
		year = y;
		conference = c;
		fullText = f;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getConference() {
		return conference;
	}
	public void setConference(String conference) {
		this.conference = conference;
	}
	public String getFullText() {
		return fullText;
	}
	public void setFullText(String fullText) {
		this.fullText = fullText;
	}
}
