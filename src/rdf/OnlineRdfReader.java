package rdf;

import org.openrdf.repository.http.HTTPRepository;

import util.Logger;

public class OnlineRdfReader extends RdfReader {

	public OnlineRdfReader(String url) {
		repository = new HTTPRepository(url);
		try {
			Logger.Info("initializing online repository at " + url);
			repository.initialize();
			Logger.Info("connecting to online repository at " + url);
			connection = repository.getConnection();
			Logger.Info("connected to repository at " + url);
			open = true;
		} catch (Exception e) {
			Logger.Severe(
					"error while connecting to the online rdf repository at \""
							+ url + "\"", e);
			close();
		}
	}
}
