package java.megalib.checker;

public class InputStreamError extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4677349186745805764L;
	String message;
	
	public InputStreamError(String exceptionMessage) {
		message = exceptionMessage;
	}
}
