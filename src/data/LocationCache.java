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
import util.Logger;
import util.Prompter;
import de.fhpotsdam.unfolding.geo.Location;

public class LocationCache {
	private final HashMap<String, Country> map = new HashMap<String, Country>();
	private final HashSet<String> failures = new HashSet<String>();
	private final HashMap<String, String> countryCodes = new HashMap<String, String>();
	private static LocationCache instance;

	private HashMap<String, String> redirection = new HashMap<String, String>();

	private LocationCache() {
		redirection.put("http://dbpedia.org/resource/Slovak",
				"http://dbpedia.org/resource/Slovakia");
		load();
	}

	private void load() {
		try {
			File file = new File("data/cache/countrycodes.txt");
			FileReader reader = new FileReader(file);
			BufferedReader r = new BufferedReader(reader);

			String line;
			while ((line = r.readLine()) != null) {
				if (line.startsWith("#"))
					continue;

				String[] split = line.split("\t");

				if (split.length > 1) {
					String code = split[0];
					String country = "";
					for (int i = 1; i < split.length; i++)
						country += split[i] + " ";
					countryCodes.put(country.trim().toUpperCase(), code);
				}
			}

			r.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
					map.put(split[4], country);
				} else if (split.length == 1) {
					failures.add(split[0]);
				}
			}

			r.close();
			reader.close();
		} catch (IOException e) {
			Logger.Warning("Error while reading the location cache!"
					+ e.getMessage());
		}
	}

	public void save() {
		try {
			File file = new File("data/cache/countrycodes.txt");
			FileWriter writer = new FileWriter(file);
			BufferedWriter w = new BufferedWriter(writer);

			w.newLine();
			for (Entry<String, String> e : countryCodes.entrySet()) {
				String line = e.getValue() + "\t" + e.getKey();
				w.write(line);
				w.newLine();
				;
			}
			w.close();
			writer.close();
		} catch (IOException e) {
			Logger.Warning("Error while saving the location cache!"
					+ e.getMessage());
		}

		try {
			File file = new File("data/cache/locationcache.txt");
			FileWriter writer = new FileWriter(file);
			BufferedWriter w = new BufferedWriter(writer);

			w.write("#\turl\tlattitude\tlongtitude");
			w.newLine();
			for (Entry<String, Country> e : map.entrySet()) {
				Country c = e.getValue();
				String line = c.getName() + "\t" + c.getAbbreviation() + "\t"
						+ c.getLocation().getLat() + "\t"
						+ c.getLocation().getLon() + "\t" + e.getKey();
				w.write(line);
				w.newLine();
			}

			for (String string : failures) {
				w.write(string);
				w.newLine();
			}
			w.close();
			writer.close();
		} catch (IOException e) {
			Logger.Warning("Error while saving the location cache!"
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
		url = redirection.containsKey(url) ? redirection.get(url) : url;

		if (LocationCache.getInstance().hasFailure(url)) {
			Logger.Debug("Location cache hit!");
			throw new QueryFailedException(
					"Could not get the location from the url: <" + url + ">");
		}

		if (LocationCache.getInstance().hasLocation(url)) {
			Logger.Debug("Location cache hit!");
			return new Country(getFromCache(url));

		} else
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

		q.add("SELECT ?name ?lat ?long WHERE {");
		q.add("<" + adress + "> foaf:name ?name .");
		q.add("<" + adress + "> geo:lat ?lat .");
		q.add("<" + adress + "> geo:long ?long .");
		q.add("}");

		TupleQueryResult result = r.executeQuery(q);

		try {
			if (result.hasNext()) {
				BindingSet set = result.next();

				float latitude, longtitude;

				try {
					latitude = Float.parseFloat(set.getBinding("lat")
							.getValue().stringValue());
				} catch (NumberFormatException e) {
					throw new NumberFormatException(
							"Could not parse float from "
									+ set.getBinding("lat").getValue()
											.stringValue() + " in url " + url);
				}
				try {
					longtitude = Float.parseFloat(set.getBinding("long")
							.getValue().stringValue());
				} catch (NumberFormatException e) {
					throw new NumberFormatException(
							"Could not parse float from "
									+ set.getBinding("long").getValue()
											.stringValue() + " in url " + url);
				}

				String countryName = set.getBinding("name").getValue()
						.stringValue();
				String countryCode = LocationCache.getInstance()
						.getCodeForCountry(countryName);
				Country country = new Country(countryName, countryCode,
						new Location(latitude, longtitude));

				addToCache(url, country);
				addToCache(adress, country);

				return country;
			} else
				throw new QueryFailedException(
						"No bindings were found for the following url: <"
								+ adress + ">");
		} catch (QueryFailedException e) {
			throw e;
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
			c.setConnectTimeout(1000);
			// Force redirection by accessing the input stream.
			c.connect();
			// c.getInputStream();

			c.getInputStream().close();

			// Replace the erroneous name
			String realUrl = c.getURL().toString();
			String[] splitRealUrl = realUrl.split("/");
			String[] splitOldUrl = adress.split("/");
			if (splitRealUrl[splitRealUrl.length - 1].equals("resource"))
				return adress;
			splitOldUrl[splitOldUrl.length - 1] = splitRealUrl[splitRealUrl.length - 1];
			String result = "";
			for (int i = 0; i < splitOldUrl.length; i++)
				if (i != splitOldUrl.length - 1)
					result += splitOldUrl[i] + "/";
				else
					result += splitOldUrl[i];

			if (!adress.equals(result))
				Logger.Info("Redirected <" + adress + "> to <" + result + ">");
			return result;
		} catch (IOException e) {
			return adress;
		}
	}

	public String getCodeForCountry(String country) {
		String format = country.trim().toUpperCase();
		if (countryCodes.containsKey(format))
			return countryCodes.get(format);
		else {
			String prompt = null;
			while (!countryCodes.containsKey(prompt))
				prompt = Prompter
						.prompt("Could not find code for country \"" + country
								+ "\"...\nEnter the correct country name:\n")
						.trim().toUpperCase();
			String result = countryCodes.get(prompt);
			countryCodes.put(format, result);
			return result;
		}
	}
}
