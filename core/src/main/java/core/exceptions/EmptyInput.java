package core.exceptions;

public class EmptyInput extends Exception {
	/**
	 * added to circumvent warning
	 */
	private static final long serialVersionUID = 9128473251675469018L;

	public EmptyInput() {
		super("Input is empty!");
	}
}

// TODO  format