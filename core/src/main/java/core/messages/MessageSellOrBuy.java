package core.messages;

import core.MyChecksum;

public class MessageSellOrBuy extends FIXMessage {
	private int		actionLength;
	private String	messageAction;
	private int		id;
	private int		instrumentLength;
	private String	instrument;
	private int		quantity;
	private int		price;

	public MessageSellOrBuy(String messageType, String messageAction, int marketId, int id, String instrument, int quantity, int price) {
		super(messageType, marketId);
		this.messageAction = messageAction;
		this.actionLength = messageAction.length();
		this.id = id;
		this.instrument = instrument;
		this.instrumentLength = instrument.length();
		this.quantity = quantity;
		this.price = price;
		setChecksum(createMyChecksum());
	}

	public MessageSellOrBuy() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getInstrument() {
		return instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
		instrumentLength = instrument.length();
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getInstrumentLength() {
		return instrumentLength;
	}

	public void setNewChecksum() {
		setChecksum(createMyChecksum());
	}

	public String getMessageAction() {
		return messageAction;
	}

	public void setMessageAction(String messageAction) {
		this.messageAction = messageAction;
		this.actionLength = messageAction.length();
	}

	public int getActionLength() {
		return actionLength;
	}

	@Override
	public String toString() {
		return "MessageSellOrBuy {" +
				"ID = " + id +
				"|MSG_TYPE = '" + getMessageType() + "'" +
				"|MSG_ACTION = '" + messageAction + "'" +
				"|INSTRUMENT = '" + instrument + "'" +
				"|MARKET_ID = " + getMarketId() +
				"|QUANTITY = " + quantity +
				"|PRICE = " + price +
				"|CHECKSUM = '" + getChecksum() + "'" +
				'}';
	}

	public String	createMyChecksum() {
		StringBuilder checksumBuffer = new StringBuilder("");
		checksumBuffer.append(this.getMarketId()).append(this.getMessageType()).append(id).
				append(price).append(quantity).append(instrument).
				append(messageAction);
		return MyChecksum.myChecksum(checksumBuffer);
	}

}
