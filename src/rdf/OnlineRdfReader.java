package rdf;

import org.apache.log4j.Logger;
import org.openrdf.repository.http.HTTPRepository;

public class OnlineRdfReader extends RdfReader {

	public OnlineRdfReader(String url) {
		repository = new HTTPRepository(url);
		Logger.getRootLogger().removeAllAppenders();
		try {
			repository.initialize();
			connection = repository.getConnection();
			open = true;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			close();
		}
	}
}
