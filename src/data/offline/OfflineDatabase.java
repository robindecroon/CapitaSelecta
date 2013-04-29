package data.offline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import util.Logger;
import data.Author;
import data.Conference;
import data.Country;
import data.Database;
import data.Paper;
import data.University;
import de.fhpotsdam.unfolding.geo.Location;

public class OfflineDatabase extends Database {
	private static OfflineDatabase instance;

	private HashMap<Author, List<Paper>> cache;
	private final HashMap<String, Author> authorMap = new HashMap<String, Author>();
	private final HashMap<String, University> universities = new HashMap<String, University>();
	private final HashMap<String, Country> countries = new HashMap<String, Country>();

	private OfflineDatabase() {
	}

	@Override
	protected void addCountry(Country country) {
		super.addCountry(country);
		countries.put(country.getName(), country);
	}

	@Override
	protected void addUniversity(University university) {
		super.addUniversity(university);
		universities.put(university.getName(), university);
	}

	/**
	 * 
	 * @return
	 */
	public static OfflineDatabase getInstance() {
		if (instance == null) {
			instance = new OfflineDatabase();
			instance.initialize();
		}
		return instance;
	}

	protected void addAuthor(Author author) {
		super.addAuthor(author);

		authorMap.put(author.getFullName(), author);
	}

	protected void allocateResources() {
		try {
			String line;
			File file = new File("data/cache/countries.txt");
			FileReader reader = new FileReader(file);
			BufferedReader r = new BufferedReader(reader);
			while ((line = r.readLine()) != null)
				addCountry(countryFromString(line));
			r.close();
			reader.close();

			file = new File("data/cache/university.txt");
			reader = new FileReader(file);
			r = new BufferedReader(reader);
			while ((line = r.readLine()) != null)
				addUniversity(universityFromString(line));
			r.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.Database#readAllAuthors()
	 */
	@Override
	protected void readAllAuthors() {
		try {
			File file = new File("data/cache/authors.txt");
			FileReader reader = new FileReader(file);
			BufferedReader r = new BufferedReader(reader);
			String line;
			while ((line = r.readLine()) != null)
				addAuthor(authorFromString(line));
			r.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.Severe(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.Database#readAllPapers()
	 */
	@Override
	protected void readAllPapers() {
		try {
			File file = new File("data/cache/papers.txt");
			FileReader reader = new FileReader(file);
			BufferedReader r = new BufferedReader(reader);
			String line;
			while ((line = r.readLine()) != null)
				addPaper(paperFromString(line));
			r.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.Severe(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.Database#getPapersForAuthor(data.Author)
	 */
	@Override
	protected List<Paper> getPapersForAuthor(Author author) {
		if (cache == null) {
			try {
				cache = new HashMap<Author, List<Paper>>();
				File file = new File("data/cache/authorpaperlink.txt");
				FileReader reader = new FileReader(file);
				BufferedReader r = new BufferedReader(reader);
				String line;
				while ((line = r.readLine()) != null) {
					String[] split = line.split(";");
					Author aa = authorMap.get(split[0]);
					List<Paper> papers = new ArrayList<Paper>();
					for (int i = 1; i < split.length; i++)
						papers.add(getPaperFromTitle(split[i]));
					cache.put(aa, papers);
				}
				r.close();
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
				Logger.Severe(e.getMessage());
			}
		}

		if (cache.containsKey(author))
			return cache.get(author);
		else
			return new ArrayList<Paper>();
	}

	/**
	 * 
	 * @param author
	 * @return
	 */
	public static String authorToString(Author author) {
		StringBuilder b = new StringBuilder();
		b.append(author.getAuthorResource());
		b.append(";");
		b.append(author.getFirstName());
		b.append(";");
		b.append(author.getLastName());
		b.append(";");
		b.append(author.getUniversity().getName());
		b.append(";");
		b.append(author.getCountry().getName());
		return b.toString();
	}

	public static String countryToString(Country country) {
		StringBuilder b = new StringBuilder();
		b.append(country.getName());
		b.append(";");
		b.append(country.getAbbreviation());
		b.append(";");
		b.append(country.getLocation().x);
		b.append(";");
		b.append(country.getLocation().y);
		return b.toString();
	}

	public static String universityToString(University university) {
		StringBuilder b = new StringBuilder();
		b.append(university.getName());
		b.append(";");
		b.append(university.getCountry().getName());
		b.append(";");
		b.append(university.getLocation().x);
		b.append(";");
		b.append(university.getLocation().y);
		return b.toString();
	}

	public static String paperToString(Paper paper) {
		StringBuilder b = new StringBuilder();
		b.append(paper.getName());
		b.append(";");
		b.append(paper.getName().toLowerCase().replaceAll(" ", "") + ".txt");
		b.append(";");
		b.append(paper.getYear());
		b.append(";");
		b.append(paper.getConference().toString());
		return b.toString();
	}

	public Paper paperFromString(String string) {
		String[] split = string.split(";");
		int year = Integer.parseInt(split[2]);
		Conference c = Conference.getConferenceFromAcro(split[3]);

		String fullText = "";

		try {
			File file = new File("data/cache/papertext/" + split[1]);
			FileReader reader = new FileReader(file);
			BufferedReader r = new BufferedReader(reader);
			String line;
			while ((line = r.readLine()) != null)
				fullText += line + "\n";
			r.close();
			reader.close();
		} catch (Exception e) {
			Logger.Severe(e.getMessage());
		}

		return new Paper(split[0], fullText, year, c);
	}

	public Author authorFromString(String string) {
		String[] split = string.split(";");
		University university = universities.get(split[3]);
		Country country = countries.get(split[4]);
		return new Author(split[0], split[1], split[2], university, country);
	}

	public Country countryFromString(String string) {
		String[] split = string.split(";");
		float xx = Float.parseFloat(split[2]);
		float yy = Float.parseFloat(split[3]);
		return new Country(split[0], split[1], new Location(xx, yy));
	}

	public University universityFromString(String string) {
		String[] split = string.split(";");
		Country country = countries.get(split[1]);
		float xx = Float.parseFloat(split[2]);
		float yy = Float.parseFloat(split[3]);
		return new University(split[0], country, new Location(xx, yy));
	}

	public static String authorPaperLink(Author author) {
		StringBuilder builder = new StringBuilder();

		builder.append(author.getFullName());

		for (Paper paper : author.getPapers()) {
			builder.append(";");
			builder.append(paper.getName());
		}

		return builder.toString();
	}
}
