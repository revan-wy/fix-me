package core.messages;

import core.MyChecksum;

public class MessageAcceptConnection extends FixMessage {
	private int id;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setNewChecksum() {
		StringBuilder checksumBuffer = new StringBuilder("");
		checksumBuffer.append(this.id).append(this.messageType);
		setChecksum(MyChecksum.myChecksum(checksumBuffer));
	}

	// search for this // complete this implementation

}
