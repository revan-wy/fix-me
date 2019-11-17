package core.exceptions;

public class ChecksumIsNotEqual extends Exception {
	/**
	 * added to circumvent warning
	 */
	private static final long serialVersionUID = -3408599660178491251L;

	public ChecksumIsNotEqual() {
		super("Checksum is wrong!");
	}
}
