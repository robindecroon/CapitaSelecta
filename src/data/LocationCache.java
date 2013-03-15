package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;

import de.fhpotsdam.unfolding.geo.Location;

import rdf.OnlineRdfReader;
import rdf.QueryFailedException;

public class LocationCache {
	private final HashMap<String, Country> map = new HashMap<String, Country>();
	private final HashSet<String> failures = new HashSet<String>();
	private static LocationCache instance;

	private LocationCache() {
		load();
	}

	private void load() {
		try {
			File file = new File("data/cache/locationcache.txt");

			FileReader reader = new FileReader(file);
			BufferedReader r = new BufferedReader(reader);

			String line;
			while ((line = r.readLine()) != null) {
				if (line.startsWith("#"))
					continue;

				String[] split = line.split("\t");

				if (split.length == 5) {
					String name = split[0];
					String abbrevition = split[1];
					float latitude = Float.parseFloat(split[2]);
					float longitude = Float.parseFloat(split[3]);

					Country country = new Country(name, abbrevition,
							new Location(latitude, longitude));
					map.put(split[0], country);
				} else if (split.length == 1) {
					failures.add(split[0]);
				}
			}

			r.close();
			reader.close();
		} catch (IOException e) {
			System.err.println("Error while reading the location cache!"
					+ e.getMessage());
		}
	}

	public void save() {
		try {
			File file = new File("cache/locationcache.txt");
			FileWriter writer = new FileWriter(file);
			BufferedWriter w = new BufferedWriter(writer);

			w.write("#\turl\tlattitude\tlongtitude");
			w.newLine();
			for (Entry<String, Country> e : map.entrySet()) {
				Country c = e.getValue();
				String line = c.getName() + "\t" + c.getAbbreviation() + "\t"
						+ c.getLocation().getLat() + "\t"
						+ c.getLocation().getLon();
				w.write(line);
				w.newLine();
				System.out.println(line);
			}

			for (String string : failures) {
				w.write(string);
				w.newLine();
			}
			w.close();
			writer.close();
		} catch (IOException e) {
			System.err.println("Error while saving the location cache!"
					+ e.getMessage());
		}
	}

	public boolean hasLocation(String code) {
		return map.containsKey(code);
	}

	public boolean hasFailure(String code) {
		return failures.contains(code);
	}

	public void addToCache(String code, Country location) {
		if (code == null)
			throw new NullPointerException("The given code is null!");
		if (location == null)
			throw new NullPointerException("The given location is null!");
		map.put(code, location);
	}

	public void addToFailure(String code) {
		failures.add(code);
	}

	public Country getFromCache(String code) {
		return map.get(code);
	}

	public static LocationCache getInstance() {
		if (instance == null)
			instance = new LocationCache();
		return instance;
	}

	public Country getCountryFromURL(String url) throws QueryFailedException {
		if (LocationCache.getInstance().hasFailure(url))
			throw new QueryFailedException("Could not locate the location!");

		if (LocationCache.getInstance().hasLocation(url))
			return new Country(getFromCache(url));
		else
			return getOnlineFromURL(url);
	}

	private Country getOnlineFromURL(String url) throws QueryFailedException {
		OnlineRdfReader r = new OnlineRdfReader("http://dbpedia.org/sparql");
		List<String> q = new ArrayList<String>();

		String adress = getRedirectedUrl(url);

		q.add("PREFIX foaf: <http://xmlns.com/foaf/0.1/>");
		q.add("PREFIX dbp: <http://dbpedia.org/property/>");
		q.add("PREFIX geo:<http://www.w3.org/2003/01/geo/wgs84_pos#>");
		q.add("PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
		q.add("PREFIX dbo: <http://dbpedia.org/ontology/>");
		q.add("PREFIX dbprop: <http://dbpedia.org/property/>");

		q.add("SELECT ?name ?code ?lat ?long WHERE {");
		q.add("<" + adress + "> foaf:name ?name .");
		q.add("<" + adress + "> geo:lat ?lat .");
		q.add("<" + adress + "> geo:long ?long .");
		q.add("<" + adress + "> dbpprop:countryCode ?code.");
		q.add("}");

		TupleQueryResult result = r.executeQuery(q);

		try {
			if (result.hasNext()) {
				BindingSet set = result.next();

				float latitude = Float.parseFloat(set.getBinding("lat")
						.getValue().stringValue());
				float longtitude = Float.parseFloat(set.getBinding("long")
						.getValue().stringValue());
				String countryCode = set.getBinding("countryCode").getValue()
						.stringValue();
				String countryName = set.getBinding("name").getValue()
						.stringValue();

				Country country = new Country(countryName, countryCode,
						new Location(latitude, longtitude));

				addToCache(url, country);
				addToCache(adress, country);

				return country;
			} else
				throw new QueryFailedException(
						"No bindings were found for the following url: <"
								+ adress + ">");

		} catch (Exception e) {
			addToFailure(url);
			addToFailure(adress);
			throw new QueryFailedException(e.getMessage());
		}
	}

	private String getRedirectedUrl(String adress) {
		try {
			// Create the url
			URL url = new URL(adress);
			URLConnection c = url.openConnection();

			// Force redirection by accessing the input stream.
			c.connect();
			c.getInputStream();

			// Replace the erroneous name
			String realUrl = c.getURL().toString();
			String[] splitRealUrl = realUrl.split("/");
			String[] splitOldUrl = adress.split("/");

			System.out.println("old = " + adress);
			System.out.println("new = " + realUrl);
			if (splitRealUrl[splitRealUrl.length - 1].equals("resource"))
				return adress;
			splitOldUrl[splitOldUrl.length - 1] = splitRealUrl[splitRealUrl.length - 1];
			String result = "";
			for (int i = 0; i < splitOldUrl.length; i++)
				if (i != splitOldUrl.length - 1)
					result += splitOldUrl[i] + "/";
				else
					result += splitOldUrl[i];
			return result;
		} catch (IOException e) {
			return adress;
		}
	}
}
