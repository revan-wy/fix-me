package broker;

import core.Client;

/**
 * Hello world!
 */
public final class App {

	public static void main(String[] args) {
		Client client = new Client("Broker");
		Thread clientThread = new Thread(client);
		clientThread.start();
		try {
			clientThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Client.handleInput(client);
	}
}
