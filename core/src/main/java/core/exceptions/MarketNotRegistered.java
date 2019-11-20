package core.exceptions;

public class MarketNotRegistered extends Exception { //TODO rename this to clientnotregistered
	private static final long serialVersionUID = -1114883444409482670L;

	public MarketNotRegistered() {
		super("The requested market is not a registered client.");
	}
}
