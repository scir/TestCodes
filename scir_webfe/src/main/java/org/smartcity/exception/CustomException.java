package org.smartcity.exception;

public class CustomException extends RuntimeException {

	private static final long serialVersionUID = -5050924598626688305L;
	private String message;
	private Exception e;
	
	/**
	 * Constructs an instance of <code>DatabaseException</code> with the
	 * specified detail message.
	 * 
	 * @param message
	 *            ---> the detail message.
	 */
	public CustomException(String message) {
		this.message = message;
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
	public CustomException(String message, Exception e) {
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

}
