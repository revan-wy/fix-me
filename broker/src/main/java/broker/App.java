package broker;

public final class App {

	public static void main(String[] args) {
		try{
			BrokerLogic bl = new BrokerLogic();
			bl.brokerLoop();
		}catch(Exception ex){ex.printStackTrace();}
	}
}
