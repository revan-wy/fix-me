package core.messages;

public class FixMessage {
	private int			checksumLength;
	protected int		marketId;
	private int			typeLength;
	private String		checksum;
	protected String	messageType;

	public			FixMessage(String messageType, int marketId) {
		this.messageType = messageType;
		this.typeLength = messageType.length();
		this.marketId = marketId;
	}

	public			FixMessage() {

	}

	public String	getChecksum() {
		return this.checksum;
	}

	public int		getChecksumLength() {
		return this.checksumLength;
	}	

	public int		getMarketId() {
		return this.marketId;
	}

	public String	getMessageType() {
		return this.messageType;
	}		

	public int		getTypeLength() {
		return this.typeLength;
	}		

	public void		setChecksum(String checksum) {
		this.checksum = checksum;
		this.checksumLength = checksum.length();
	}

	public void		setMessageType(String messageType) {
		this.messageType = messageType;
	}			

	// TODO complete implementation of this class

}
