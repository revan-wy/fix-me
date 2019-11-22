package core.messages;

import core.MyChecksum;

public class ConnectionRequest extends FixMessage {

	public ConnectionRequest(String messageType) {
		super(messageType, 0);
		updateChecksum();
	}

	public ConnectionRequest() {
	}

	public String createMyChecksum() {
		StringBuilder checksumBuffer = new StringBuilder("");
		checksumBuffer.append(this.getType()).append(this.getSenderId());
		return MyChecksum.myChecksum(checksumBuffer);
	}

	@Override
	public String toString() {
		return "CONNECTION_REQUEST {" + "SENDER_ID = " + getSenderId() + "|TYPE = '" + getType() + "'"
				+ "|CHECKSUM = '" + getChecksum() + "'" + '}';
	}

	public void updateChecksum() {
		setChecksum(createMyChecksum());
	}

}
