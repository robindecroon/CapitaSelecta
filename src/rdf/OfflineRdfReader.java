package rdf;

import java.io.File;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.nativerdf.NativeStore;

import util.Logger;

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

		try {
			repository.initialize();
			connection = repository.getConnection();

			for (File file : rdfFiles)
				try {
					connection.add(file, "http://example.org/example/local",
							RDFFormat.RDFXML);
				} catch (Exception e) {
					Logger.Severe("error while connecting to the file \""
							+ file.getAbsolutePath() + "\"", e);
				}
			open = true;
		} catch (RepositoryException e1) {
			Logger.Severe("error while initializing the rdf repository", e1);
			close();
		}
	}
}
