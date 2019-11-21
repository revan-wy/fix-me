package core.messages;

import core.MyChecksum;

public class ConnectionRequest extends FixMessage {
	private int senderId;

	public ConnectionRequest(String messageType) {
		super(messageType, 0);
		this.senderId = 0;
		updateChecksum();
	}

	public ConnectionRequest() {
	}

	public String createMyChecksum() {
		StringBuilder checksumBuffer = new StringBuilder("");
		checksumBuffer.append(this.getMarketId()).append(this.getMessageType()).append(this.getSenderId());
		return MyChecksum.myChecksum(checksumBuffer);
	}

	public int getSenderId() {
		return senderId;
	}

	public void setSenderId(int id) {
		this.senderId = id;
	}

	@Override
	public String toString() {
		return "MessageAcceptConnection {" + "ID = " + getSenderId() + "|MSG_TYPE = '" + getMessageType() + "'"
				+ "|CHECKSUM = '" + getChecksum() + "'" + '}';
	}

	public void updateChecksum() {
		setChecksum(createMyChecksum());
	}

}

// TODO format