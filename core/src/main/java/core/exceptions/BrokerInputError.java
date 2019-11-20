package core.exceptions;

public class BrokerInputError extends Exception {
	private static final long serialVersionUID = -6236451257357996173L;

	public BrokerInputError() {
		super("Error: Please check your input.");
	}
}
