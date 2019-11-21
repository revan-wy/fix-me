package core.messages;

public class FixMessage {
	private int checksumLength;
	private int senderId;
	private int typeLength;
	private String checksum;
	private String type;

	public FixMessage(String type, int senderId) {
		this.type = type;
		this.typeLength = type.length();
		this.senderId = senderId;
	}

	public FixMessage() {
	}

	public String getChecksum() {
		return checksum;
	}

	public int getChecksumLength() {
		return checksumLength;
	}

	public int getSenderId() {
		return senderId;
	}

	public String getType() {
		return type;
	}

	public int getTypeLength() {
		return typeLength;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
		checksumLength = checksum.length();
	}

	public void setSenderId(int id) {
		this.senderId = id;
	}

	public void setType(String type) {
		this.type = type;
		typeLength = type.length();
	}

}
