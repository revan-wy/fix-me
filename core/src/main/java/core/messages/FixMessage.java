package core.messages;

public class FixMessage {
	private int		checksumLength;
	private int		typeLength;
	private String	checksum;
	private String	messageType;

	public FixMessage(String messageType) {
		this.messageType = messageType;
		this.typeLength = messageType.length();
	}

	public FixMessage() {
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
		typeLength = messageType.length();
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
		checksumLength = checksum.length();
	}

	public int getTypeLength() {
		return typeLength;
	}

	public int getChecksumLength() {
		return checksumLength;
	}

	
}

// TODO format