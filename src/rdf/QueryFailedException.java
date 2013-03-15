package rdf;

public class QueryFailedException extends RuntimeException {
	private static final long serialVersionUID = -6340735345890197861L;

	public QueryFailedException(String message) {
		super(message);
	}
}
