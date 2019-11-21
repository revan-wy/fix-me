package core.messages;

public class Message {

	public enum Response {
		EXECUTED, REJECTED
	}

	public enum Type {
		CONNECTION_REQUEST, BUY, SELL
	}

}
