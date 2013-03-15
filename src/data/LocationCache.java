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

import rdf.OnlineRdfReader;
import rdf.QueryFailedException;

public class LocationCache {
	private final HashMap<String, BaseLocation> map = new HashMap<String, BaseLocation>();
	private final HashSet<String> failures = new HashSet<String>();
	private static LocationCache instance;

	private LocationCache() {
		load();
	}

	private void load() {
		try {
			File file = new File("cache/locationcache.txt");

			FileReader reader = new FileReader(file);
			BufferedReader r = new BufferedReader(reader);

			String line;
			while ((line = r.readLine()) != null) {
				if (line.startsWith("#"))
					continue;

				String[] split = line.split("\t");

				if (split.length == 4) {
					float latitude = Float.parseFloat(split[1]);
					float longitude = Float.parseFloat(split[2]);
					BaseLocation location = new BaseLocation(latitude,
							longitude, split[3]);
					map.put(split[0], location);
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
			for (Entry<String, BaseLocation> e : map.entrySet()) {
				String line = e.getKey() + "\t" + e.getValue().getLat()
						+ "\t" + e.getValue().getLon() + "\t"
						+ e.getValue().getCountryName();
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

	public void addToCache(String code, BaseLocation location) {
		if (code == null)
			throw new NullPointerException("The given code is null!");
		if (location == null)
			throw new NullPointerException("The given location is null!");
		map.put(code, location);
	}

	public void addToFailure(String code) {
		failures.add(code);
	}

	public BaseLocation getFromCache(String code) {
		return map.get(code);
	}

	public static LocationCache getInstance() {
		if (instance == null)
			instance = new LocationCache();
		return instance;
	}

	public BaseLocation getBaseLocationFromUrl(String url)
			throws QueryFailedException {
		if (LocationCache.getInstance().hasFailure(url))
			throw new QueryFailedException("Could not locate the location!");

		if (LocationCache.getInstance().hasLocation(url))
			return new BaseLocation(getFromCache(url));
		else
			return getOnlineFromURL(url);
	}

	private BaseLocation getOnlineFromURL(String url)
			throws QueryFailedException {
		OnlineRdfReader r = new OnlineRdfReader("http://dbpedia.org/sparql");
		List<String> q = new ArrayList<String>();

		String adress = getRedirectedUrl(url);

		q.add("PREFIX foaf: <http://xmlns.com/foaf/0.1/>");
		q.add("PREFIX dbp: <http://dbpedia.org/property/>");
		q.add("PREFIX geo:<http://www.w3.org/2003/01/geo/wgs84_pos#>");
		q.add("PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
		q.add("PREFIX dbo: <http://dbpedia.org/ontology/>");
		q.add("SELECT ?name ?lat ?long WHERE {");
		q.add("<" + adress + "> foaf:name ?name .");
		q.add("<" + adress + "> geo:lat ?lat .");
		q.add("<" + adress + "> geo:long ?long .");
		q.add("}");

		TupleQueryResult result = r.executeQuery(q);

		try {
			if (result.hasNext()) {
				BindingSet set = result.next();

				float latitude = Float.parseFloat(set.getBinding("lat")
						.getValue().stringValue());
				float longtitude = Float.parseFloat(set.getBinding("long")
						.getValue().stringValue());
				String countryName = set.getBinding("name").getValue()
						.stringValue();

				BaseLocation location = new BaseLocation(latitude, longtitude,
						countryName);

				addToCache(url, location);
				addToCache(adress, location);

				return location;
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
