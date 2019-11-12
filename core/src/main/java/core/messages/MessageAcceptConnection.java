package core.messages;

import core.MyChecksum;

public class MessageAcceptConnection extends FixMessage {
	private int id;

	public			MessageAcceptConnection(String messageType, int marketId, int id) {
		super(messageType, marketId);
		this.id = id;
		setChecksum(createMyChecksum());
	}
	
	public			MessageAcceptConnection() {

	}
	
	public String	createMyChecksum() {
		StringBuilder checksumBuffer = new StringBuilder("");
		checksumBuffer.append(marketId).append(messageType).append(id);
		return MyChecksum.myChecksum(checksumBuffer);
	}

	public int		getId() {
		return this.id;
	}

	public void		setId(int id) {
		this.id = id;
	}

	public void		setNewChecksum() {
		StringBuilder checksumBuffer = new StringBuilder("");
		checksumBuffer.append(this.id).append(this.messageType);
		setChecksum(MyChecksum.myChecksum(checksumBuffer));
	}

	@Override
	public String	toString() {
		return "messageAcceptConnection { " +
			"ID = " + this.getId() +
			" | MESSAGE_TYPE = " + getMessageType() +
			" | CHECKSUM = " + getChecksum() +
			"}";
	}
}
