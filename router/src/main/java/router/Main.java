package router;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
	public static void main(String[] args) {
		Router brokerRouter = new Router(Router.BROKER_PORT);
		new Thread(brokerRouter).start();
		Router marketServer = new Router(Router.MARKET_PORT);
		new Thread(marketServer).start();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String command;
		while (true) {
			command = null;
			try {
				command = br.readLine();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
			if (command != null && command.toLowerCase().equals("exit")) {
				brokerRouter.shutDown();
				marketServer.shutDown();
				break;
			}
		}
	}
}

// TODO format