package core.exceptions;

public class ChecksumIsInvalid extends Exception {
	private static final long serialVersionUID = -3408599660178491251L;

	public ChecksumIsInvalid() {
		super("Checksum is invalid.");
	}
}

// TODO format