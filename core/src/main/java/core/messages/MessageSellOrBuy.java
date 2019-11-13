package core.messages;

import core.MyChecksum;

public class MessageSellOrBuy extends FixMessage {
	private int		actionLength;
	private int		id;
	private int		instrumentLength;
	private int		price;
	private int		quantity;
	private String	instrument;
	private String	messageAction;

	public			MessageSellOrBuy(String messageType, String messageAction,
			int marketId, int id, String instrument, int quantity, int price) {
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
	
	public			MessageSellOrBuy() {

	}	
	
	public String	createMyChecksum() {
		StringBuilder checksumBuffer = new StringBuilder("");
		checksumBuffer.append(marketId).append(messageType).append(id).
				append(price).append(quantity).append(instrument).
				append(messageAction);
		return MyChecksum.myChecksum(checksumBuffer);
	}

	public int		getActionLength() {
		return this.actionLength;
	}			

	public int		getId() {
		return this.id;
	}

	public String	getInstrument() {
		return this.instrument;
	}

	public int		getInstrumentLength() {
		return this.instrumentLength;
	}

	public String	getMessageAction() {
		return this.messageAction;
	}	

	public int		getPrice() {
		return this.price;
	}

	public int		getQuantity() {
		return this.quantity;
	}

	public void		setId(int id) {
		this.id = id;
	}		
	
	public void		setInstrument(String instrument) {
		this.instrument = instrument;
		this.instrumentLength = instrument.length();
	}		
	
	public void		setMessageAction(String messageAction) {
		this.messageAction = messageAction;
		this.actionLength = messageAction.length();
	}			
	
	public void		setNewChecksum() {
		setChecksum(createMyChecksum());
	}			

	public void		setPrice(int price) {
		this.price = price;
	}			

	public void		setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "MessageSellOrBuy {" +
				"ID = " + 
				" | MESSAGE_TYPE = " + getMessageType() +
				" | MESSAGE_ACTION = " + getMessageAction() +
				" | INSTRUMENT = " + getInstrument() +
				" | MARKET_ID = " + getMarketId() +
				" | QUANTITY = " + getQuantity() +
				" | PRICE = " + getPrice() +
				" | CHECKSUM = " + getChecksum() +
				" }";
	}
}
