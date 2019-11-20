package core.exceptions;

public class ChecksumIsInvalid extends Exception {
	private static final long serialVersionUID = 7029294679968048090L;

	public ChecksumIsInvalid() {
		super("Checksum is invalid.");
	}
}
