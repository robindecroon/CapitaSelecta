package data;

import java.util.ArrayList;
import java.util.List;

public class Paper {
	private String name;
	private String fullText;

	public String getFullText() {
		return fullText;
	}

	public void setFullText(String fullText) {
		if (fullText == null)
			throw new NullPointerException("The given fulltext is null!");
		this.fullText = fullText;
	}

	private List<Author> authors = new ArrayList<Author>();

	public Paper(String name, String fullText) {
		setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name == null)
			throw new NullPointerException("The given name is null!");
		this.name = name;
	}

	public List<Author> getAuthors() {
		return new ArrayList<Author>(authors);
	}

	public void addAuthor(Author author) {
		if (author == null)
			throw new NullPointerException("The given author is null!");
		authors.add(author);
	}
}
