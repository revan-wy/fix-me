package core.messages;

import core.MyChecksum;

public class Order extends FixMessage {
	private int		actionLength;
	private String	messageAction;
	private int		id; // TODO investigate moving to fixmessage class; rename to brokerId
	private int		instrumentLength;
	private String	instrument;
	private int		quantity;
	private int		price;
	private int		marketId;

	public Order(String messageType, String messageAction, int marketId, int id, String instrument,
			int quantity, int price) {
		super(messageType);
		this.marketId = marketId;
		this.messageAction = messageAction;
		this.actionLength = messageAction.length();
		this.id = id;
		this.instrument = instrument;
		this.instrumentLength = instrument.length();
		this.quantity = quantity;
		this.price = price;
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

// TODO format