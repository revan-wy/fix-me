package core.messages;

import core.MyChecksum;

public class Order extends FixMessage {
	private int		instrumentLength;
	private int		marketId;
	private int		price;
	private int		quantity;
	private int		responseLength;
	private String	instrument;
	private String	response;

	public Order(String messageType, String response, int marketId, int senderId, String instrument,
			int quantity, int price) {
		super(messageType, senderId);
		this.instrument = instrument;
		this.instrumentLength = instrument.length();
		this.marketId = marketId;
		this.price = price;
		this.quantity = quantity;
		this.response = response;
		this.responseLength = response.length();
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

	public String getResponse() {
		return this.response;
	}

	public void setResponse(String response) {
		this.response = response;
		this.responseLength = response.length();
	}

	public int getResponseLength() {
		return responseLength;
	}

	@Override
	public String toString() {
		return "MessageSellOrBuy {" +
				"ID = " + getSenderId() +
				"|MSG_TYPE = '" + getType() + "'" +
				"|MSG_ACTION = '" + getResponse() + "'" +
				"|INSTRUMENT = '" + getInstrument() + "'" +
				"|MARKET_ID = " + getMarketId() +
				"|QUANTITY = " + getQuantity() +
				"|PRICE = " + getPrice() +
				"|CHECKSUM = '" + getChecksum() + "'" +
				'}';
	}

	public String	createMyChecksum() {
		StringBuilder checksumBuffer = new StringBuilder("");
		checksumBuffer.append(this.getMarketId()).append(this.getType()).append(getSenderId()).
				append(getPrice()).append(getQuantity()).append(getInstrument()).
				append(getResponse());
		return MyChecksum.myChecksum(checksumBuffer);
	}

}

// TODO format