package rdf;

import org.openrdf.repository.http.HTTPRepository;

public class OnlineRdfReader extends RdfReader {

	public OnlineRdfReader(String url) {
		repository = new HTTPRepository(url);
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
