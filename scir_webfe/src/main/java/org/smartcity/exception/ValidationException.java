package org.smartcity.exception;

public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = -7580314973415873123L;
	private String message;
	private Integer errorCode ;
	private Exception e;
	
	/**
	 * Constructs an instance of <code>ValidationException</code> with the
	 * specified detail message.
	 * 
	 * @param message
	 *            ---> the detail message.
	 */
	public ValidationException(String message) {
		this.message = message;
	}

	public ValidationException(Integer errorCode, String message) {
		this.message = message; this.errorCode=errorCode;
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
	public ValidationException(String message, Exception e) {
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

	public Integer getErrorCode() {
		return errorCode;
	}

}
