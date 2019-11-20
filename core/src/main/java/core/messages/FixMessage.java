package core.messages;

public class FixMessage {
	private int		typeLength;
	private String	messageType;
	private int		marketId; // TODO investigate moving to buysell class
	private int		checksumLength;
	private String	checksum;

	public FixMessage(String messageType, int marketId) {
		this.messageType = messageType;
		this.typeLength = messageType.length();
		this.marketId = marketId;
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

	public int getMarketId() {
		return marketId;
	}

	public void setMarketId(int marketId) {
		this.marketId = marketId;
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