package util;

public class LogAdapter implements LogListener {
	/*
	 * (non-Javadoc)
	 * 
	 * @see util.LogListener#Warning(java.lang.String)
	 */
	@Override
	public void Warning(String message) {
	}

	/*
	 * (non-Javadoc)
	 * @see util.LogListener#Info(java.lang.String)
	 */
	@Override
	public void Info(String message) {
	}

	/*
	 * (non-Javadoc)
	 * @see util.LogListener#Severe(java.lang.String)
	 */
	@Override
	public void Severe(String message) {
	}

	/*
	 * (non-Javadoc)
	 * @see util.LogListener#Debug(java.lang.String)
	 */
	@Override
	public void Debug(String message) {
	}

}
