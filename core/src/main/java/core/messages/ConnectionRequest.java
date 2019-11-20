package core.messages;

import core.MyChecksum;

public class ConnectionRequest extends FixMessage {
	private int		id;

	public ConnectionRequest(String messageType) {
		super(messageType, 0);
		this.id = 0;
		updateChecksum();
	}

	public ConnectionRequest() {
	}

	public String	createMyChecksum() {
		StringBuilder checksumBuffer = new StringBuilder("");
		checksumBuffer.append(this.getMarketId()).append(this.getMessageType()).append(id);
		return MyChecksum.myChecksum(checksumBuffer);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void		updateChecksum() {
		setChecksum(createMyChecksum());
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