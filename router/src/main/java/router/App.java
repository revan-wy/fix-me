package router;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class App {
	private App() {
	}

	public static void main(String[] args) {
		Router brokerRouter = new Router(5000);
		Thread brokerRouterThread = new Thread(brokerRouter);
		brokerRouterThread.start();
		Router marketRouter = new Router(5001);
		Thread marketRouterThread = new Thread(marketRouter);
		marketRouterThread.start();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input;
		while (true) {
			input = "";
			try {
				input = br.readLine();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
			if (!input.equals("") && input.toUpperCase().equals("EXIT")) {
				brokerRouter.shutdown();
				marketRouter.shutdown();
				break;
			}
		}
	}
}
