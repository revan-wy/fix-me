package core.exceptions;

public class ErrorInput extends Exception {
	/**
	 * added to circumvent warning
	 */
	private static final long serialVersionUID = 1447678723062650595L;

	public ErrorInput() {
		super("Error on input!");
	}
}

// TODO