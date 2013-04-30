package util;

public interface LogListener {
	public void Warning(String message);
	public void Info(String message);
	public void Severe(String message);
	public void Debug(String message);
}
