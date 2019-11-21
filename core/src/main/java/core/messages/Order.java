package core.messages;

import core.MyChecksum;

public class Order extends FixMessage {
	private int		actionLength;
	private int		instrumentLength;
	private int		marketId;
	private int		price;
	private int		quantity;
	private String	instrument;
	private String	messageAction; // TODO rename to response

	public Order(String messageType, String messageAction, int marketId, int senderId, String instrument,
			int quantity, int price) {
		super(messageType, senderId);
		this.actionLength = messageAction.length();
		this.instrument = instrument;
		this.instrumentLength = instrument.length();
		this.marketId = marketId;
		this.messageAction = messageAction;
		this.price = price;
		this.quantity = quantity;
		updateChecksum();
	}

	public Order() {
	}

	public void setMarketId(int marketId) {
		this.marketId = marketId;
	}

	public int getMarketId() {
		return this.marketId;
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

	public void updateChecksum() {
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
				"ID = " + getSenderId() +
				"|MSG_TYPE = '" + getMessageType() + "'" +
				"|MSG_ACTION = '" + getMessageAction() + "'" +
				"|INSTRUMENT = '" + getInstrument() + "'" +
				"|MARKET_ID = " + getMarketId() +
				"|QUANTITY = " + getQuantity() +
				"|PRICE = " + getPrice() +
				"|CHECKSUM = '" + getChecksum() + "'" +
				'}';
	}

	public String	createMyChecksum() {
		StringBuilder checksumBuffer = new StringBuilder("");
		checksumBuffer.append(this.getMarketId()).append(this.getMessageType()).append(getSenderId()).
				append(price).append(quantity).append(instrument).
				append(messageAction);
		return MyChecksum.myChecksum(checksumBuffer);
	}

}

// TODO format