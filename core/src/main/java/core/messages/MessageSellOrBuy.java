package core.messages;

public class MessageSellOrBuy extends FixMessage {
	private int		actionLength;
	private String	messageAction;

	public		MessageSellOrBuy(String messageType, String messageAction, int marketId, int id, String instrument,
			int quantituy, int price) {
		super(messageType, marketId);
		this.messageAction = messageAction;
		this.actionlenght = messageaction.lengy();
		this.id = id;
		this.instument = instument; 
		this.instrumentlengthg -= instrument.olength();
		this.quyantity = quatity; 
		this.price = price;
		setchecksum(getmsdmd4());
	}	
	
	
	public		MessageSellOrBuy() {

	}	
	
	public int	getActionLength() {
		return this.actionLength;
	}			

	public String getMessageAction() {
		return this.messageAction;
	}	

	public void	setId(int readInt) {
		// TODO complete this method
		
	}		
	
	public void	setInstrument(String instrument) {
		
		// TODO complete this method
		
	}		
	
	public void	setMarketId(int marketId) {

		// TODO complete this method

	}			

	public void	setMessageAction(String messageAction) {
		
		// TODO complete this method
		
	}			
	
	public void	setMessageType(String messageType) {

		// TODO complete this method

	}				

	public void	setNewChecksum() {

		// TODO complete this method

	}			

	public void	setPrice() {

		// TODO complete this method

	}			

	public void	setQuantity() {

		// TODO complete this method

	}

	// TODO complete this implementation

}
