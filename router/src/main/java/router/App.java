package router;

public final class App {
	private App() {
	}

	public static void main(String[] args) {
		Router brokerRouter = new Router(5000);
		Thread brokerRouterThread = new Thread(brokerRouter);
		brokerRouterThread.start();
	}
}
