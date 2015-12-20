package org.smartcity.exception;

public class DatabaseException extends RuntimeException {

	public static final int EXCEPTION_DUPLICATE_KEY = 1 ;
	
	private static final long serialVersionUID = -8621568771654593218L;
	private String message;
	private Exception e;
	
	private int exceptionType;

	/**
	 * Constructs an instance of <code>DatabaseException</code> with the
	 * specified detail message.
	 * 
	 * @param message
	 *            ---> the detail message.
	 */
	public DatabaseException(String message) {
		this.message = message;
	}

	public DatabaseException(String message, int exceptionType) {
		this.message = message;
		this.exceptionType = exceptionType;
	}

	/**
	 * Constructs an instance of <code>DatabaseException</code> with the
	 * specified detail message and exception.
	 * 
	 * @param message
	 *            ---> the detail message.
	 * @param e
	 *            ---> the exception being thrown
	 */
	public DatabaseException(String message, Exception e) {
		super(e);
		this.e = e;
		this.message = message;
	}

	/**
	 * Returns the message which was passed while creating the object.
	 * 
	 * @return the message which was passed while creating the object.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Returns the exception which was passed to it while creating the object.
	 * 
	 * @return the exception which was passed to it while creating the object.
	 */
	public Exception getException() {
		return e;
	}

	public int getExceptionType() {
		return exceptionType;
	}
}
