package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
// import java.util.Scanner;

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
	// private static int count = 0;
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

		// private void brokerWriteHandler(ChannelHandlerContext context, String string)
		// throws Exception {
		// String[] split = string.split("\\s+");
		// if (split.length != 5)
		// throw new BrokerInputError();
		// Order message;
		// String type = split[0].toLowerCase();
		// String response = "";
		// int marketId = verifyId(split[1]);
		// String instrument = split[2];
		// int quantity, price;
		// try {
		// quantity = Integer.valueOf(split[3]);
		// price = Integer.valueOf(split[4]);
		// } catch (Exception e) {
		// throw new BrokerInputError();
		// }
		// if (type.equals("sell") || type.equals("buy")) {
		// message = new Order(type.toUpperCase(), response, marketId, clientID,
		// instrument, quantity, price);
		// } else {
		// throw new BrokerInputError();
		// }
		// message.updateChecksum();
		// ;
		// context.writeAndFlush(message);
		// System.out.println("Sending " + message.getType() + " order to router.");
		// }

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
			// Scanner scan = new Scanner(System.in);
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String command = "0";
			int marketId = 0;
			// int validation = 0;

			while (!isValidCommand(command)) {
				printMenue();
				command = br.readLine();
				try {
					verifyId(command);
				} catch (Exception e) {
					System.out.println(e.getMessage());
					continue;
				}
			}
			switch (command) {
			case "1":
				System.out.println("Please input a market ID:");
				try {
					marketId = Integer.valueOf(br.readLine());
				} catch (Exception ex) {
					System.out.println("Invalid Market ID");
					System.out.println("Press Any Key To Continue...");
					System.in.read();
					// continue;
				}
				message = new Order(Message.Type.BUY.toString(), "", (marketId), clientID, randInstrument(),
						randQuantity(), randPrice());
				System.out.println("Buy command signaled -> [" + marketId + "]");
				System.out.println(message.toString());
				System.out.println("Press Any Key To Continue...");
				try {
					System.in.read();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				break;
			case "2":
				System.out.println("Please input a market ID:");
				try {
					marketId = Integer.valueOf(br.readLine());
				} catch (Exception ex) {
					System.out.println("Invalid Market ID");
					System.out.println("Press Any Key To Continue...");
					System.in.read();
					// continue;
				}
				message = new Order(Message.Type.SELL.toString(), "", (marketId), clientID, randInstrument(),
						randQuantity(), randPrice());
				System.out.println("Sell command signaled to market -> [" + marketId + "]");
				System.out.println(message.toString());
				System.out.println("Press Any Key To Continue...");
				try {
					System.in.read();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				break;
			case "3":
				// validation = 1;
				System.out.println("    Broker client shutting down");
				shutdown();
				break;
			default:
				return;
			}
			// }
			// Write and Flush
			message.updateChecksum();
			// scan.nextLine();
			context.channel().writeAndFlush(message);
			// System.out.println("cycle count is " + count);
			// Client.count++;
			// System.out.println("Write and Flush goes here");
			// }
			// System.out.println("Broker Loop Exited");
			// br.readLine();
			// }
			// try {
			// String input = getBrokerInput();
			// if (input.length() == 0)
			// throw new InputStringEmpty();
			// else if (input.toLowerCase().equals("exit"))
			// shutdown();
			// else if (clientType == Client.Type.BROKER)
			// brokerWriteHandler(context, input);
			// } catch (Exception e) {
			// System.out.println(e.getMessage());
			// channelWrite(context);
			// }
			// write and flush Here
			// message.updateChecksum();;
			// context.writeAndFlush(message);
		}

		// =========>
		// private String getBrokerInput() throws Exception {
		// System.out.println(
		// "Please create a new order. Format: \n[sell || buy] [market id] [instrument]
		// [quantity] [price]");
		// BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		// return br.readLine();
		// }

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

		// =========>
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

	// Sell or buy generating functions aswell as menue print
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
