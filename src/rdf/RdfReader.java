package rdf;

import java.util.List;

import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import util.Logger;

public abstract class RdfReader {
	protected Repository repository;
	protected boolean open = false;
	protected RepositoryConnection connection;

	/**
	 * Executes a query and returns the result of the query.
	 * 
	 * @param query
	 *            The query to execute.
	 */
	public TupleQueryResult executeQuery(List<String> query)
			throws QueryFailedException {
		if (!open)
			throw new QueryFailedException("RdfReader is closed!");

		StringBuilder q = new StringBuilder();
		for (String string : query)
			q.append(string + "\n");
		Logger.Debug("Executing query:\n" + q.toString());

		try {
			TupleQuery tq = connection.prepareTupleQuery(QueryLanguage.SPARQL,
					q.toString());
			return tq.evaluate();
		} catch (Exception e) {
			throw new QueryFailedException(e.getMessage());
		}
	}

	/**
	 * Closes the reader.
	 */
	public void close() {
		try {
			if (connection != null)
				connection.close();
			if (repository != null)
				repository.shutDown();
			open = false;
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
}
