package core.exceptions;

public class ChecksumIsNotEqual extends Exception{
	private static final long serialVersionUID = -2547322269427832763L;

	public ChecksumIsNotEqual() {
		super("Checksum is wrong.");
	}
}
