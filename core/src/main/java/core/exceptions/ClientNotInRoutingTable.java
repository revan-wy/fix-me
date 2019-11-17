package core.exceptions;

public class ClientNotInRoutingTable extends Exception {
	/**
	 * added to circumvent warning
	 */
	private static final long serialVersionUID = -1354177855341301317L;

	public ClientNotInRoutingTable() {
		super("This client is not in routing table!");
	}
}
