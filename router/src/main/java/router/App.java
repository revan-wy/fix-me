package router;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Hello world!
 */
public final class App {
	private App() {
	}

    public static void main(String[] args) {
		Router brokerRouter = new Router(5000);
		Thread brokerRouterThread = new Thread(brokerRouter);
		brokerRouterThread.start();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			try {
				String input = br.readLine();
				if (input.toUpperCase().equals("EXIT")) {
					break;
				}
			} catch(IOException e) {
				System.out.println(e.getMessage());
			}
		}
    }
}