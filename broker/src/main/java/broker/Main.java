package broker;

public final class Main {

	public static void main(String[] args) {
		try{
			BrokerLogic bl = new BrokerLogic();
			bl.brokerLoop();
		}catch(Exception ex){ex.printStackTrace();}
	}
}
