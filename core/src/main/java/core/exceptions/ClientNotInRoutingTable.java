package core.exceptions;

public class ClientNotInRoutingTable extends Exception {
	private static final long serialVersionUID = -1114883444409482670L;

	public ClientNotInRoutingTable() {
		super("The requested market is not a registered client.");
	}
}
