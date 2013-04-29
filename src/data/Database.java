package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import util.Dictionary;
import util.Logger;
import data.offline.OfflineDatabase;
import data.online.OnlineDatabase;
import de.fhpotsdam.unfolding.geo.Location;
import filter.Filter;

public abstract class Database {
	private HashSet<String> badWords = new HashSet<String>();

	// List with all the authors
	private final List<Author> authors = new ArrayList<Author>();
	private final List<Paper> papers = new ArrayList<Paper>();
	private final List<Country> countries = new ArrayList<Country>();
	private final List<University> universities = new ArrayList<University>();

	private final HashMap<String, Location> affiliations = new HashMap<String, Location>();
	private final HashMap<String, String> affiliationAliases = new HashMap<String, String>();
	// Needed data during construction
	private final HashMap<String, Paper> titlePaperMap = new HashMap<String, Paper>();

	public static Database getInstance() {
		return OfflineDatabase.getInstance();
	}
	
	public Database() {
	}

	/**
	 * Initialize
	 */
	protected void initialize() {
		allocateResources();
		readAffiliationAliases();
		readAffiliations();
		readAllAuthors();
		readAllPapers();
		linkAuthorsToPapers();
		readBadWords();

		LocationCache.getInstance().save();
		Paper paper;
		Iterator<Paper> it = papers.iterator();
		while (it.hasNext() && (paper = it.next()) != null) {
			if (paper.getAuthors().size() == 0) {
				Logger.Warning("Paper \"" + paper.getName()
						+ " has no authors! It has been removed!");
				it.remove();
			}
		}
		WordDatabase.getInstance().exportWordList();

		close();
	}

	protected void close() {
		// Template method
	}

	protected void allocateResources() {
		// Template method
	}

	protected void addCountry(Country country) {
		this.countries.add(country);
	}

	protected void addUniversity(University university) {
		this.universities.add(university);
	}

	protected void addPaper(Paper paper) {
		if (paper == null)
			throw new NullPointerException("The given paper is null!");
		if (papers.contains(paper))
			return;
		if (titlePaperMap.containsKey(paper.getName()))
			return;
		papers.add(paper);
		titlePaperMap.put(paper.getName(), paper);
	}

	protected void addAuthor(Author author) {
		if (author == null)
			throw new NullPointerException("The given author is null!");
		if (authors.contains(author))
			return;
		authors.add(author);

		Country country = author.getCountry();
		University university = author.getUniversity();

		if (!countries.contains(country))
			countries.add(country);
		if (!universities.contains(university))
			universities.add(university);
	}

	protected abstract void readAllAuthors();

	protected abstract void readAllPapers();

	protected void readAffiliationAliases() {
		try {
			FileReader reader = new FileReader(new File(
					"data/location/locationaliases.txt"));
			BufferedReader r = new BufferedReader(reader);
			String line;

			while ((line = r.readLine()) != null) {
				String[] s = line.split(";");
				Logger.Severe(line);
				if (s.length < 2)
					continue;
				Logger.Severe("added " + s[0].trim() + " as alias for "
						+ s[1].trim());
				affiliationAliases.put(s[0].trim(), s[1].trim());
			}

			r.close();
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void readAffiliations() {
		try {
			File file = new File("data/location/location.txt");
			FileReader reader = new FileReader(file);
			BufferedReader r = new BufferedReader(reader);

			String line;
			int index = 0;

			while ((line = r.readLine()) != null) {
				if (index > 0) {
					if (line.startsWith("#"))
						continue;
					String[] split = line.split(";");

					try {
						float xx = Float.parseFloat(split[0]);
						float yy = Float.parseFloat(split[1]);
						String affiliation = split[2];
						Location l = new Location(xx, yy);
						affiliations.put(affiliation, l);
					} catch (NumberFormatException ee) {
					}
				}
				index++;
			}

			r.close();
			reader.close();
		} catch (IOException e) {
		}
	}

	protected String getAffiliationAlias(String affiliation) {
		String result = affiliation;
		if (affiliationAliases.containsKey(affiliation)) {
			result = affiliationAliases.get(affiliation);
			Logger.Severe("redirected \"" + affiliation + "\" to \"" + result
					+ "\"");
		}
		return result;
	}

	protected Location getUniversityLocationFromAffiliation(String affiliation) {
		return affiliations.get(getAffiliationAlias(affiliation));
	}

	protected abstract List<Paper> getPapersForAuthor(Author author);

	protected Paper getPaperFromTitle(String title) {
		return titlePaperMap.get(title);
	}

	protected void linkAuthorsToPapers() {
		for (Author author : getAuthors())
			for (Paper p : getPapersForAuthor(author)) {
				p.addAuthor(author);
				author.addPaper(p);
			}
	}

	public List<Author> getAuthors() {
		return authors;
	}

	public List<Paper> getPapers() {
		return papers;
	}

	public List<Country> getCountries() {
		return countries;
	}

	public List<University> getUniversities() {
		return universities;
	}

	protected void readBadWords() {
		try {
			FileReader reader = new FileReader(new File("wordstofilter.txt"));
			BufferedReader r = new BufferedReader(reader);

			String line;

			while ((line = r.readLine()) != null)
				badWords.add(line.toLowerCase().trim());

			r.close();
			reader.close();
		} catch (Exception e) {
			Logger.Severe(e.getMessage());
		}
	}

	public boolean isBadWord(String word) {
		return badWords.contains(word);
	}

	public HashMap<UniversityCluster, PaperWordData> getWordsPerUniversity(
			float distance, Filter filter) {
		Set<UniversityCluster> clusters = UniversityCluster.getClusters(
				universities, distance);
		HashMap<UniversityCluster, PaperWordData> result = new HashMap<UniversityCluster, PaperWordData>();

		for (UniversityCluster cluster : clusters) {
			PaperWordData data = new PaperWordData();

			for (University university : cluster.getUniversities()) {
				Location location = university.getLocation();

				for (Paper paper : this.papers) {
					if (!paper.getFirstLocation().equals(location))
						continue;
					if (!filter.allowed(paper))
						continue;
					List<PaperWord> words = paper.getRelevantWords();

					for (PaperWord word : words)
						data.addWord(word.word, paper, word.occurences);
				}
			}

			data.format(Dictionary.getInstance().getWords());
			result.put(cluster, data);
		}
		return result;
	}

}
