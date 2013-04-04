package name.jenkins.paul.john.concordia.exception;

/**
 * The superclass for any exceptions that Concordia may throw.
 * 
 * @author John Jenkins
 */
public class ConcordiaException extends Exception {
	/**
	 * The version of this exception class.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an exception with a reason why this exception was thrown.
	 * 
	 * @param reason
	 *        The user-friendly reason this exception was thrown.
	 */
	public ConcordiaException(final String reason) {
		super(reason);
	}

	/**
	 * Creates an exception with an underlying cause.
	 * 
	 * @param cause
	 *        The cause for the exception.
	 */
	public ConcordiaException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Creates an exception with a reason and an underlying cause.
	 * 
	 * @param reason
	 *        The reason this exception was thrown.
	 * 
	 * @param cause
	 *        The underlying cause of this exception.
	 */
	public ConcordiaException(final String reason, final Throwable cause) {
		super(reason, cause);
	}
}