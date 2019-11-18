package core.messages;

public class Message {

	public enum Type {
		CONNECTION_REQUEST, 
		BUY, 
		SELL
	}

	public enum Action {
		EXECUTED, 
		REJECTED
	}

}

// TODO format