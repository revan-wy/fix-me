package core.exceptions;

public class ClientNotRegistered extends Exception { //TODO rename this to clientnotregistered
	private static final long serialVersionUID = -1114883444409482670L;

	public ClientNotRegistered() {
		super("The requested market is not a registered client.");
	}
}
