package router;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
	public static void main(String[] args) {
		Router brokerRouter = new Router(Router.BROKER_PORT);
		new Thread(brokerRouter).start();
		Router marketRouter = new Router(Router.MARKET_PORT);
		new Thread(marketRouter).start();
		System.out.println("Awaiting Broker and Market connections.");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String command = "";
		while (true) {
			try {
				command = br.readLine();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			if (command != "" && command.toLowerCase().equals("exit")) {
				brokerRouter.shutDown();
				marketRouter.shutDown();
				break;
			}
		}
	}
}
