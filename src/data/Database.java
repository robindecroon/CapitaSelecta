package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;

import rdf.OfflineRdfReader;
import rdf.QueryFailedException;
import de.fhpotsdam.unfolding.geo.Location;

public class Database {
	// The singleton instance
	private static Database database;

	// RdfReader for the offline database
	private OfflineRdfReader reader;

	// List with all the authors
	private final List<Author> authors = new ArrayList<Author>();

	// Map which links countries to authors
	private final Map<Country, List<Author>> countryAuthorMap = new HashMap<Country, List<Author>>();
	private final Map<University, List<Author>> affiliationAuthorMap = new HashMap<University, List<Author>>();

	// List with all the papers
	private final List<Paper> papers = new ArrayList<Paper>();
	private final Map<String, Paper> titlePaperMap = new HashMap<String, Paper>();

	// List with all the countries and their locations
	private final List<Country> locations = new ArrayList<Country>();
	private final Map<String, University> affiliationLocations = new HashMap<String, University>();

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
		readAllAuthors();
		readAllPapers();
		linkAuthorsToPapers();
		initialized = true;
	}

	public void addAuthor(Author author) {
		if (author == null)
			throw new NullPointerException("The given author is null!");
		if (authors.contains(author))
			return;
		authors.add(author);

		Country country = author.getCountry();
		University university = author.getUniversity();

		if (!countryAuthorMap.containsKey(country))
			countryAuthorMap.put(country, new ArrayList<Author>());
		countryAuthorMap.get(country).add(author);

		if (!affiliationAuthorMap.containsKey(university))
			affiliationAuthorMap.put(university, new ArrayList<Author>());
		affiliationAuthorMap.get(university).add(author);
	}

	public List<Author> getAuthors() {
		return new ArrayList<Author>(authors);
	}

	public int getNbOfAuthorsInCountry(String country) {
		if (countryAuthorMap.containsKey(country))
			return countryAuthorMap.get(country).size();
		return 0;
	}

	public List<Country> getCountries() {
		return locations;
	}

	public Map<String, University> getAffiliationLocation() {
		return affiliationLocations;
	}

	public Map<Country, List<Author>> getCountryAuthorMap() {
		return new HashMap<Country, List<Author>>(countryAuthorMap);
	}

	public Map<University, List<Author>> getAffiliationAuthorMap() {
		return new HashMap<University, List<Author>>(affiliationAuthorMap);
	}

	public void addPaper(Paper paper) {
		if (paper == null)
			throw new NullPointerException("The given paper is null!");
		if (papers.contains(paper))
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

	private HashMap<String, Location> readAffiliations()
			throws NumberFormatException, IOException {
		HashMap<String, Location> result = new HashMap<String, Location>();
		File file = new File("data/location/location.txt");
		FileReader reader = new FileReader(file);
		BufferedReader r = new BufferedReader(reader);

		String line;
		int index = 0;

		while ((line = r.readLine()) != null) {
			if (index > 0) {
				String[] split = line.split(";");
				float xx = Float.parseFloat(split[0]);
				float yy = Float.parseFloat(split[1]);
				String affiliation = split[2];
				Location l = new Location(xx, yy);

				result.put(affiliation, l);
			}
			index++;
		}

		r.close();

		return result;
	}

	private void readAllAuthors() {
		List<String> query = new ArrayList<String>();
		query.add("PREFIX foaf:<http://xmlns.com/foaf/0.1/>");
		query.add("PREFIX swrc:<http://swrc.ontoware.org/ontology#>");
		query.add("PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>");
		query.add("SELECT ?person ?firstName ?lastName ?location ?affiliation WHERE {");
		query.add("?person foaf:firstName ?firstName .");
		query.add("?person foaf:lastName ?lastName .");
		// query.add("?person foaf:based_near ?location .");
		query.add("?person swrc:affiliation ?af .");
		query.add("?af rdfs:label ?affiliation .");
		query.add("}");

		TupleQueryResult result = reader.executeQuery(query);

		try {
			HashMap<String, Location> affiliationLocations = readAffiliations();

			while (result.hasNext()) {

				BindingSet set = result.next();

				String firstName = set.getBinding("firstName").getValue()
						.stringValue();
				String lastName = set.getBinding("lastName").getValue()
						.stringValue();
				// String location = set.getBinding("location").getValue()
				// .stringValue();
				String resource = set.getBinding("person").getValue()
						.stringValue();
				String affiliation = set.getBinding("affiliation").getValue()
						.stringValue();

				try {
					Location universityLocation = affiliationLocations
							.get(affiliation);

					if (universityLocation == null) {
						System.err
								.println("Could not find location for university: "
										+ universityLocation);
						continue;
					}

					// Country country = LocationCache.getInstance()
					// .getCountryFromURL(location);
					Country country = new Country("x", "USA",
							new Location(0, 0));

					University university = new University(affiliation,
							country, universityLocation);

					Author author = new Author(resource, firstName, lastName,
							university, country);
					addAuthor(author);
				} catch (QueryFailedException e) {
					System.err
							.println("Could not retrieve the country for author "
									+ firstName + " " + lastName);
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	private void readAllPapers() {
		System.err.println("Reading papers");
		List<String> query = new ArrayList<String>();
		query.add("PREFIX foaf:<http://xmlns.com/foaf/0.1/>");
		query.add("PREFIX dc:<http://purl.org/dc/elements/1.1/>");
		query.add("PREFIX led:<http://data.linkededucation.org/ns/linked-education.rdf#>");
		query.add("SELECT ?title ?text WHERE {");
		query.add("?paper dc:title ?title .");
		query.add("?paper led:body ?text .");
		query.add("}");

		TupleQueryResult result = reader.executeQuery(query);

		try {
			while (result.hasNext()) {
				BindingSet set = result.next();

				String title = set.getBinding("title").getValue().stringValue();
				String text = set.getBinding("text").getValue().stringValue();

				Paper paper = new Paper(title, text, 2008, Conference.LAK);
				addPaper(paper);
				System.out.println(title);
			}
		} catch (Exception e) {
			System.err.println(e);
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
}
