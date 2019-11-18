package market;

import core.Client;

public class Market {
	public static void main(String[] args) {
		Client client = new Client(Client.Type.MARKET);
		Thread clientThread = new Thread(client);
		clientThread.start();
		try {
			clientThread.join();
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
		Client.handleInput(client);
	}
}
