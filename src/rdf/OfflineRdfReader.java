package rdf;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.nativerdf.NativeStore;

public class OfflineRdfReader extends RdfReader {
	public static final File REPOSITORY = new File("repository");

	public OfflineRdfReader(String... filenames) {
		File[] files = new File[filenames.length];
		for (int i = 0; i < filenames.length; i++)
			files[i] = new File(filenames[i]);
		initialize(files);
	}

	public OfflineRdfReader(File... rdfFiles) {
		initialize(rdfFiles);
	}

	private void initialize(File... rdfFiles) {
		repository = new SailRepository(new NativeStore(REPOSITORY));
		Logger.getRootLogger().removeAllAppenders();
		try {
			repository.initialize();

			connection = repository.getConnection();

			for (File file : rdfFiles)
				try {
					connection.add(file, "http://example.org/example/local",
							RDFFormat.RDFXML);
				} catch (RDFParseException e) {
					e.printStackTrace();
				} catch (RepositoryException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			open = true;
		} catch (RepositoryException e1) {
			System.err.println("Error while initializing the repository!");
			close();
		}
	}
}
