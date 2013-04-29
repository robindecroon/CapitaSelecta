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

import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;

import rdf.OfflineRdfReader;
import rdf.QueryFailedException;
import util.Dictionary;
import util.Logger;
import de.fhpotsdam.unfolding.geo.Location;
import filter.Filter;

public class Database {
	// The singleton instance
	private static Database database;

	// RdfReader for the offline database
	private OfflineRdfReader reader;

	// List with all the authors
	private final List<Author> authors = new ArrayList<Author>();
	private final List<Paper> papers = new ArrayList<Paper>();
	private final List<Country> countries = new ArrayList<Country>();
	private final List<University> universities = new ArrayList<University>();

	// List with bad words
	private final HashSet<String> badWords = new HashSet<String>();
	private final HashMap<String, Location> affiliations = new HashMap<String, Location>();
	private final HashMap<String, String> affiliationAliases = new HashMap<String, String>();

	// Needed during construction
	private HashMap<String, Paper> titlePaperMap = new HashMap<String, Paper>();

	// Whether the database has been initialized
	private boolean initialized = false;

	/**
	 * 
	 */
	private Database() {
		initialize();
	}

	public static Database getInstance() {
		if (database == null)
			database = new Database();
		return database;
	}

	public void initialize() {
		reader = new OfflineRdfReader("data/rdf/edm2008.rdf",
				"data/rdf/edm2009.rdf", "data/rdf/edm2010.rdf",
				"data/rdf/edm2011.rdf", "data/rdf/edm2012.rdf",
				"data/rdf/2011_fulltext_.rdf", "data/rdf/2012_fulltext_.rdf",
				"data/rdf/jets12_fulltext_.rdf");
		readAffiliationAliases();
		readAffiliations();
		readBadWords();
		readAllAuthors();
		readAllPapers();
		linkAuthorsToPapers();
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
		initialized = true;

		WordDatabase.getInstance().exportWordList();
	}

	public void readAffiliationAliases() {
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

	public void readBadWords() {
		try {
			FileReader reader = new FileReader(new File("wordstofilter.txt"));
			BufferedReader r = new BufferedReader(reader);

			String line;

			while ((line = r.readLine()) != null)
				badWords.add(line.toLowerCase().trim());

			r.close();
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isBadWord(String word) {
		return badWords.contains(word);
	}

	public void addAuthor(Author author) {
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

	public void addPaper(Paper paper) {
		if (paper == null)
			throw new NullPointerException("The given paper is null!");
		if (papers.contains(paper))
			return;
		if (titlePaperMap.containsKey(paper.getName()))
			return;
		papers.add(paper);
		titlePaperMap.put(paper.getName(), paper);
	}

	public List<Paper> getPapers() {
		return papers;
	}

	public boolean isInitialized() {
		return initialized;
	}

	private void readAffiliations() {
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

	private String getAffiliationAlias(String affiliation) {
		String result = affiliation;
		if (affiliationAliases.containsKey(affiliation)) {
			result = affiliationAliases.get(affiliation);
			Logger.Severe("redirected \"" + affiliation + "\" to \"" + result
					+ "\"");
		}
		return result;
	}

	private Location getUniversityLocationFromAffiliation(String affiliation) {
		return affiliations.get(getAffiliationAlias(affiliation));
	}

	private void readAllAuthors() {
		List<String> query = new ArrayList<String>();
		query.add("PREFIX foaf:<http://xmlns.com/foaf/0.1/>");
		query.add("PREFIX swrc:<http://swrc.ontoware.org/ontology#>");
		query.add("PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>");
		query.add("SELECT ?person ?firstName ?lastName ?location ?affiliation WHERE {");
		query.add("?person foaf:firstName ?firstName .");
		query.add("?person foaf:lastName ?lastName .");
		query.add("?person foaf:based_near ?location .");
		query.add("?person swrc:affiliation ?af .");
		query.add("?af rdfs:label ?affiliation .");
		query.add("}");

		TupleQueryResult result = reader.executeQuery(query);

		try {
			while (result.hasNext()) {

				BindingSet set = result.next();

				String firstName = set.getBinding("firstName").getValue()
						.stringValue();
				String lastName = set.getBinding("lastName").getValue()
						.stringValue();
				String location = set.getBinding("location").getValue()
						.stringValue();
				String resource = set.getBinding("person").getValue()
						.stringValue();
				String affiliation = set.getBinding("affiliation").getValue()
						.stringValue();

				try {
					Location universityLocation = getUniversityLocationFromAffiliation(affiliation);

					if (universityLocation == null) {
						Logger.Warning("Could not find the university for "
								+ firstName + " " + lastName
								+ " with affiliation name: \"" + affiliation
								+ "\". Add location for \"" + affiliation
								+ "\" to location.txt");
						continue;
					}

					Country country = LocationCache.getInstance()
							.getCountryFromURL(location);

					University university = new University(getAffiliationAlias(affiliation),
							country, universityLocation);

					Author author = new Author(resource, firstName, lastName,
							university, country);
					addAuthor(author);
				} catch (QueryFailedException e) {
					Logger.Warning("Could not retrieve the country for author "
							+ firstName + " " + lastName + "! "
							+ e.getMessage());
				}
			}
		} catch (Exception e) {
			Logger.Severe("An exception has prevented from reading all the authors!");
			e.printStackTrace();
		}
	}

	private void readAllPapers() {
		List<String> query = new ArrayList<String>();
		query.add("PREFIX foaf:<http://xmlns.com/foaf/0.1/>");
		query.add("PREFIX dc:<http://purl.org/dc/elements/1.1/>");
		query.add("PREFIX led:<http://data.linkededucation.org/ns/linked-education.rdf#>");
		query.add("PREFIX swrc:<http://swrc.ontoware.org/ontology#>");
		query.add("PREFIX swc:<http://data.semanticweb.org/ns/swc/ontology#>");
		query.add("SELECT ?title ?text ?year ?acro WHERE {");
		query.add("?paper dc:title ?title .");
		query.add("?paper led:body ?text .");
		query.add("?conference swc:hasPart ?paper .");
		query.add("?conference swrc:year ?year .");
		query.add("?event swc:hasRelatedDocument ?conference .");
		query.add("?event swc:hasAcronym ?acro .");
		query.add("}");

		TupleQueryResult result = reader.executeQuery(query);

		try {
			while (result.hasNext()) {
				BindingSet set = result.next();

				String title = set.getBinding("title").getValue().stringValue();
				String text = set.getBinding("text").getValue().stringValue();
				String yearString = set.getBinding("year").getValue()
						.stringValue();
				int year = Integer.parseInt(yearString);
				String acro = set.getBinding("acro").getValue().stringValue();

				Conference c = Conference.getConferenceFromAcro(acro);

				Paper paper = new Paper(title, text, year, c);
				addPaper(paper);
			}
		} catch (Exception e) {
			Logger.Severe("An error has prevented all papers from being read");
			e.printStackTrace();
		}
	}

	private void linkAuthorsToPapers() {
		for (Author author : authors) {
			List<String> query = new ArrayList<String>();
			query.add("PREFIX dc:<http://purl.org/dc/elements/1.1/>");
			query.add("PREFIX foaf:<http://xmlns.com/foaf/0.1/>");
			query.add("PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
			query.add("SELECT ?title WHERE {");
			query.add("?paper dc:title ?title .");
			query.add("?paper dc:creator <" + author.getAuthorResource()
					+ "> .");
			query.add("}");

			TupleQueryResult result = reader.executeQuery(query);

			try {
				while (result.hasNext()) {
					BindingSet set = result.next();

					String title = set.getBinding("title").getValue()
							.stringValue();

					Paper p = titlePaperMap.get(title);

					if (p != null) {
						p.addAuthor(author);
						author.addPaper(p);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
