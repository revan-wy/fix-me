package core.messages;

public class FixMessage {
	private int		checksumLength;
	private String	messageType;
	private int		typeLength;

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getMessageType() {
		return this.messageType;
	}

	public int getTypeLength() {
		return this.typeLength;
	}

	public int getChecksumLength() {
		return this.checksumLength;
	}

	// TODO complete implementation of this class

}
