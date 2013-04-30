package data.online;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;

import rdf.OfflineRdfReader;
import rdf.QueryFailedException;
import util.Logger;
import data.Author;
import data.Conference;
import data.Country;
import data.Database;
import data.LocationCache;
import data.Paper;
import data.University;
import data.offline.OfflineDatabase;
import de.fhpotsdam.unfolding.geo.Location;

public class OnlineDatabase extends Database {
	// The singleton instance
	private static OnlineDatabase database;

	// RdfReader for the offline database
	private OfflineRdfReader reader;

	/**
	 * 
	 */
	private OnlineDatabase() {

	}

	public static OnlineDatabase getInstance() {
		if (database == null) {
			database = new OnlineDatabase();
			database.initialize();
		}
		return database;
	}

	protected void close() {
		try {
			File file = new File("data/cache/authors.txt");
			FileWriter writer = new FileWriter(file);
			BufferedWriter w = new BufferedWriter(writer);
			for (Author author : getAuthors())
				w.write(OfflineDatabase.authorToString(author) + "\n");
			w.close();
			writer.close();

			file = new File("data/cache/papers.txt");
			writer = new FileWriter(file);
			w = new BufferedWriter(writer);
			for (Paper p : getPapers()) {
				String filename = p.getName().toLowerCase().replaceAll(";", " ").replaceAll(" ", "")
						+ ".txt";
				File fullText = new File("data/cache/papertext/" + filename);
				FileWriter fullTextWriter = new FileWriter(fullText);
				BufferedWriter fullTextW = new BufferedWriter(fullTextWriter);
				fullTextW.write(p.getFullText());
				fullTextW.close();
				fullTextWriter.close();

				w.write(OfflineDatabase.paperToString(p) + "\n");
			}
			w.close();
			writer.close();

			file = new File("data/cache/countries.txt");
			writer = new FileWriter(file);
			w = new BufferedWriter(writer);
			for (Country p : getCountries())
				w.write(OfflineDatabase.countryToString(p) + "\n");
			w.close();
			writer.close();

			file = new File("data/cache/university.txt");
			writer = new FileWriter(file);
			w = new BufferedWriter(writer);
			for (University p : getUniversities())
				w.write(OfflineDatabase.universityToString(p) + "\n");
			w.close();
			writer.close();

			file = new File("data/cache/authorpaperlink.txt");
			writer = new FileWriter(file);
			w = new BufferedWriter(writer);
			for (Author p : getAuthors()) {
				w.write(OfflineDatabase.authorPaperLink(p));
				w.newLine();
			}
			w.close();
			writer.close();
		} catch (Exception e) {
			Logger.Severe("an error occured while creating the offline cache",
					e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.Database#allocateResources()
	 */
	@Override
	protected void allocateResources() {
		reader = new OfflineRdfReader("data/rdf/edm2008.rdf",
				"data/rdf/edm2009.rdf", "data/rdf/edm2010.rdf",
				"data/rdf/edm2011.rdf", "data/rdf/edm2012.rdf",
				"data/rdf/2011_fulltext_.rdf", "data/rdf/2012_fulltext_.rdf",
				"data/rdf/jets12_fulltext_.rdf");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.Database#readAllAuthors()
	 */
	@Override
	protected void readAllAuthors() {
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

					University university = new University(
							getAffiliationAlias(affiliation), country,
							universityLocation);

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
			Logger.Severe("an error occured while retrieving the authors", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.Database#readAllPapers()
	 */
	@Override
	protected void readAllPapers() {
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
			Logger.Severe("an error occured while reading the papers", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.Database#getPapersForAuthor()
	 */
	@Override
	protected List<Paper> getPapersForAuthor(Author author) {
		List<String> query = new ArrayList<String>();
		query.add("PREFIX dc:<http://purl.org/dc/elements/1.1/>");
		query.add("PREFIX foaf:<http://xmlns.com/foaf/0.1/>");
		query.add("PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
		query.add("SELECT ?title WHERE {");
		query.add("?paper dc:title ?title .");
		query.add("?paper dc:creator <" + author.getAuthorResource() + "> .");
		query.add("}");
		TupleQueryResult result = reader.executeQuery(query);

		List<Paper> rr = new ArrayList<Paper>();

		try {
			while (result.hasNext()) {
				BindingSet set = result.next();
				String title = set.getBinding("title").getValue().stringValue();

				if (getPaperFromTitle(title) != null)
					rr.add(getPaperFromTitle(title));
				else
					Logger.Warning("could not find paper with title " + title);
			}
		} catch (Exception e) {
			Logger.Severe(
					"an error occured while retrieving the papers for author \""
							+ author.getFullName() + "\"", e);
		}

		return rr;
	}
}
