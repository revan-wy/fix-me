package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import core.decoders.Decoder;
import core.encoders.ConnectionRequestEncoder;
import core.encoders.OrderEncoder;
import core.exceptions.BrokerInputError;
import core.exceptions.ChecksumIsInvalid;
import core.messages.ConnectionRequest;
import core.messages.FixMessage;
import core.messages.Message;
import core.messages.Order;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client implements Runnable {
	private Client.Type clientType;
	private EventLoopGroup workerGroup;
	private int clientID;
	private int port;
	private String host = "localhost";

	public Client(Client.Type clientType) {
		this.clientType = clientType;
		this.port = 5000;
		if (clientType == Client.Type.MARKET)
			this.port = 5001;
	}

	class ClientHandler extends ChannelInboundHandlerAdapter {
		private void announceNewConnection(ConnectionRequest request) {
			clientID = request.getSenderId();
			System.out.println("Client connected to router with ID: " + clientID);
		}

		@Override
		public void channelActive(ChannelHandlerContext context) throws Exception {
			System.out.println("Connection request sent to router.");
			ConnectionRequest message = new ConnectionRequest(Message.Type.CONNECTION_REQUEST.toString());
			context.writeAndFlush(message);
		}

		@Override
		public void channelRead(ChannelHandlerContext context, Object message) {
			FixMessage fixMessage = (FixMessage) message;
			if (messageIsConnectionRequest(fixMessage)) {
				ConnectionRequest request = (ConnectionRequest) message;
				announceNewConnection(request);
			} else if (messageIsOrder(fixMessage)) {
				Order order = (Order) message;
				try {
					if (!order.createMyChecksum().equals(order.getChecksum()))
						throw new ChecksumIsInvalid();
				} catch (Exception e) {
					System.out.println(e.getMessage());
					return;
				}
				if (messageHasBeenActioned(order))
					return;
				if (fixMessage.getType().equals(Message.Type.SELL.toString()))
					marketSellOrderHandler(context, order);
				else
					marketBuyOrderHandler(context, order);
			}
		}

		@Override
		public void channelReadComplete(ChannelHandlerContext context) {
			if (clientType == Client.Type.BROKER)
				try {
					channelWrite(context);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
		}

		private void channelWrite(ChannelHandlerContext context) throws Exception {
			Order message = null;
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String command = "0";
			int marketId = 0;
			while (!isValidCommand(command)) {
				printMenue();
				command = br.readLine();
			}
			switch (command) {
			case "1":
				System.out.println("Please input a market ID:");
				try {
					marketId = Integer.valueOf(br.readLine());
					verifyId(String.valueOf(marketId));
				} catch (Exception ex) {
					System.out.println("Invalid Market ID");
					System.out.println("Press Any Key To Continue...");
					System.in.read();
				}
				message = new Order(Message.Type.BUY.toString(), "", (marketId), clientID, randInstrument(),
						randQuantity(), randPrice());
				System.out.println("Buy command signaled -> [" + marketId + "]");
				System.out.println(message.toString());
				break;
			case "2":
				System.out.println("Please input a market ID:");
				try {
					marketId = Integer.valueOf(br.readLine());
					verifyId(String.valueOf(marketId));
				} catch (Exception ex) {
					System.out.println("Invalid Market ID");
				}
				message = new Order(Message.Type.SELL.toString(), "", (marketId), clientID, randInstrument(),
						randQuantity(), randPrice());
				System.out.println("Sell command signaled to market -> [" + marketId + "]");
				System.out.println(message.toString());
				break;
			case "3":
				System.out.println("    Broker client shutting down");
				shutdown();
				return;
			default:
				return;
			}
			message.updateChecksum();
			context.channel().writeAndFlush(message);
		}

		private boolean isValidCommand(String command) {
			try {
				int x = Integer.valueOf(command);
				if (x >= 1 && x <= 3)
					return true;
				else
					return false;
			} catch (Exception e) {
				return false;
			}
		}

		private boolean messageHasBeenActioned(Order message) {
			if (message.getResponse().equals(Message.Response.EXECUTED.toString())
					|| message.getResponse().equals(Message.Response.REJECTED.toString())) {
				System.out.println("Response to " + message.getType() + " order : " + message.getResponse());
				return true;
			}
			return false;
		}

		private void marketBuyOrderHandler(ChannelHandlerContext context, Order message) {
			Random random = new Random();
			if (random.nextBoolean()) {
				System.out.println("Buy order rejected.");
				message.setResponse(Message.Response.REJECTED.toString());
			} else {
				System.out.println("Buy order successfully executed.");
				message.setResponse(Message.Response.EXECUTED.toString());
			}
			message.updateChecksum();
			;
			context.writeAndFlush(message);
		}

		private void marketSellOrderHandler(ChannelHandlerContext context, Order message) {
			Random random = new Random();
			if (random.nextBoolean()) {
				System.out.println("Sell order successfully executed.");
				message.setResponse(Message.Response.EXECUTED.toString());
			} else {
				System.out.println("Sell order rejected.");
				message.setResponse(Message.Response.REJECTED.toString());
			}
			message.updateChecksum();
			;
			context.writeAndFlush(message);
		}

		private int verifyId(String id) throws Exception {
			if (id.length() != 6)
				throw new BrokerInputError();
			return Integer.valueOf(id);
		}

	}

	public static void inputHandler(Client client) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input = "";
		while (true) {
			try {
				input = br.readLine();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
			if (input != "" && input.toLowerCase().equals("exit")) {
				client.shutdown();
				break;
			}
		}
	}

	public static boolean messageIsOrder(FixMessage message) {
		return message.getType().equals(Message.Type.BUY.toString())
				|| message.getType().equals(Message.Type.SELL.toString());
	}

	public static boolean messageIsConnectionRequest(FixMessage message) {
		return message.getType().equals(Message.Type.CONNECTION_REQUEST.toString());
	}

	@Override
	public void run() {
		workerGroup = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(workerGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new Decoder(), new ConnectionRequestEncoder(), new OrderEncoder(),
							new ClientHandler());
				}
			}).option(ChannelOption.SO_KEEPALIVE, true);
			ChannelFuture f = b.connect(host, port).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		} finally {
			shutdown();
		}
	}

	public void shutdown() {
		workerGroup.shutdownGracefully();
	}

	public enum Type {
		BROKER, MARKET
	}

	private static void printMenue() {
		System.out.println("    1.  BUY a comodity from the market.");
		System.out.println("    2.  SELL a comodity to the market.");
		System.out.println("    3.  EXIT the Fix-Me Broker.");
	}

	private static int randPrice() {
		Random rand = new Random();
		return (rand.nextInt(100));
	}

	private static String randInstrument() {
		String[] arr = { "GOLD", "OIl", "AVOCADOS", "DIAMONDS", "COFFEE" };
		Random rand = new Random();
		return (arr[rand.nextInt(4)]);
	}

	private static int randQuantity() {
		Random rand = new Random();
		return (rand.nextInt(100));
	}
}
