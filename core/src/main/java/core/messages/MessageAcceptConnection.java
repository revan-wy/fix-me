package core.messages;

import core.MyChecksum;

public class MessageAcceptConnection extends FIXMessage {
	private int		id;

	public MessageAcceptConnection(String messageType, int marketId, int id) {
		super(messageType, marketId);
		this.id = id;
		StringBuilder checksumBuffer = new StringBuilder("");
		checksumBuffer.append(this.id).append(this.getMessageType());
		setChecksum(MyChecksum.myChecksum(checksumBuffer));
	}

	public MessageAcceptConnection() {}

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