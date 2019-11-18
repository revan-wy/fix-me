package core.exceptions;

public class BrokerInputError extends Exception {
	private static final long serialVersionUID = 1447678723062650595L;

	public BrokerInputError() {
		super("Error: Please check your input.");
	}
}

// TODO format