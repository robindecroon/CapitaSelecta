package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	private final Map<Location, List<Author>> countryAuthorMap = new HashMap<Location, List<Author>>();
	private final Map<Location, List<Author>> affiliationAuthorMap = new HashMap<Location, List<Author>>();

	// List with all the papers
	private final List<Paper> papers = new ArrayList<Paper>();
	// Map which links titles to papers.
	private final Map<String, Paper> titlePaperMap = new HashMap<String, Paper>();

	// List with all the countries and their locations
	private final List<BaseLocation> locations = new ArrayList<BaseLocation>();
	private final Map<String, Location> affiliationLocations = new HashMap<String, Location>();

	// Whether the database has been initialized
	private boolean initialized = false;

	private int minimumAuthorsPerCountry = Integer.MAX_VALUE;
	private int maximumAuthorsPerCountry = Integer.MIN_VALUE;

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
		// reader = new OfflineRdfReader("rdf/edm2008.rdf", "rdf/edm2009.rdf",
		// "rdf/edm2010.rdf", "rdf/edm2011.rdf", "rdf/edm2012.rdf",
		// "rdf/2011_fulltext_.rdf", "rdf/2012_fulltext_.rdf",
		// "rdf/jets12_fulltext_.rdf");
		reader = new OfflineRdfReader("rdf/edm2008.rdf");
		// reader = new OfflineRdfReader("rdf/2011_fulltext_.rdf",
		// "rdf/2012_fulltext_.rdf", "rdf/jets12_fulltext_.rdf");
		readAffiliations();
		readAllAuthors();
		readAllPapers();
		linkAuthorsToPapers();
		updateStats();
		initialized = true;
	}

	public void addAuthor(Author author) {
		if (author == null)
			throw new NullPointerException("The given author is null!");
		if (authors.contains(author))
			return;
		authors.add(author);

		Location countryLocation = author.getCountryLocation();
		Location affiliationLocation = author.getAffiliationLocation();

		if (!countryAuthorMap.containsKey(countryLocation))
			countryAuthorMap.put(countryLocation, new ArrayList<Author>());
		countryAuthorMap.get(countryLocation).add(author);

		if (!affiliationAuthorMap.containsKey(affiliationLocation))
			affiliationAuthorMap.put(affiliationLocation,
					new ArrayList<Author>());
		affiliationAuthorMap.get(affiliationLocation).add(author);
	}

	public List<Author> getAuthors() {
		return new ArrayList<Author>(authors);
	}

	public int getNbOfAuthorsInCountry(String country) {
		if (countryAuthorMap.containsKey(country))
			return countryAuthorMap.get(country).size();
		return 0;
	}

	public List<BaseLocation> getCountries() {
		return locations;
	}

	public Map<String, Location> getAffiliationLocation() {
		return affiliationLocations;
	}

	// public Map<Location, List<Author>> getCountryAuthorMap() {
	// return new HashMap<Location, List<Author>>(countryAuthorMap);
	// }

	public Map<Location, List<Author>> getAffiliationAuthorMap() {
		return new HashMap<Location, List<Author>>(affiliationAuthorMap);
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
		return new ArrayList<Paper>(papers);
	}

	public int getMinimumAuthorsPerCountry() {
		return minimumAuthorsPerCountry;
	}

	public int getMaximumAuthorsPerCountry() {
		return maximumAuthorsPerCountry;
	}

	public boolean isInitialized() {
		return initialized;
	}

	private void readAffiliations() {
		try {
			File file = new File("location/location.txt");
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

					affiliationLocations.put(affiliation, l);
				}
				index++;
			}

			r.close();
		} catch (IOException e) {
		}
	}

	private void readAllAuthors() {
		List<String> query = new ArrayList<String>();
		query.add("PREFIX foaf:<http://xmlns.com/foaf/0.1/>");
		query.add("PREFIX swrc:<http://swrc.ontoware.org/ontology#>");
		query.add("PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>");
		query.add("SELECT ?person ?firstName ?lastName ?location ?affiliation ?af WHERE {");
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
				String af = set.getBinding("af").getValue().stringValue();

				try {
					BaseLocation countryLocation = LocationCache.getInstance()
							.getBaseLocationFromUrl(location);
					// BaseLocation countryLocation = new BaseLocation(0, 0,
					// "bla");
					Location affiliationLocation = affiliationLocations
							.get(affiliation);

					if (affiliationLocation == null)
						throw new IllegalStateException(
								"Could not find affiliation \"" + affiliation
										+ "\" - \"" + af + "\"");

					Author author = new Author(resource, firstName, lastName,
							affiliation, countryLocation, affiliationLocation);
					System.err.println(author);
					addAuthor(author);
				} catch (QueryFailedException e) {
					System.err
							.println("Could not retrieve the location for author "
									+ firstName + " " + lastName);
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	private void readAllPapers() {
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

				Paper paper = new Paper(title, text);
				addPaper(paper);
			}
		} catch (Exception e) {
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

	private void updateStats() {
		for (Entry<Location, List<Author>> e : countryAuthorMap.entrySet()) {
			minimumAuthorsPerCountry = Math.min(minimumAuthorsPerCountry, e
					.getValue().size());
			maximumAuthorsPerCountry = Math.max(maximumAuthorsPerCountry, e
					.getValue().size());
		}
	}
}
