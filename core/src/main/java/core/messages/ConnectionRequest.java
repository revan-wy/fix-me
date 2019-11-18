package core.messages;

import core.MyChecksum;

public class ConnectionRequest extends FIXMessage {
	private int		id;

	public ConnectionRequest(String messageType) {
		super(messageType, 0);
		this.id = 0;
		StringBuilder checksumBuffer = new StringBuilder("");
		checksumBuffer.append(this.id).append(this.getMessageType());
		setChecksum(MyChecksum.myChecksum(checksumBuffer));
	}

	public ConnectionRequest() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void		setNewChecksum() {
		StringBuilder checksumBuffer = new StringBuilder("");
		checksumBuffer.append(this.id).append(this.getMessageType());
		setChecksum(MyChecksum.myChecksum(checksumBuffer));
	}

	@Override
	public String toString() {
		return "MessageAcceptConnection {" +
				"ID = " + id +
				"|MSG_TYPE = '" + getMessageType() + "'" +
				"|CHECKSUM = '" + getChecksum() + "'" +
				'}';
	}
}

// TODO format