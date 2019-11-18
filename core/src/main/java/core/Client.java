package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import core.decoders.Decoder;
import core.encoders.AcceptConnectionEncoder;
import core.encoders.BuyOrSellEncoder;
import core.exceptions.ChecksumIsInvalid;
import core.exceptions.EmptyInput;
import core.exceptions.ErrorInput;
import core.messages.BuyOrSellOrder;
import core.messages.ConnectionRequest;
import core.messages.FixMessage;
import core.messages.Message;
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

	public enum Type {
		BROKER, MARKET
	}

	public Client(Client.Type clientType) {
		this.clientType = clientType;
		this.port = 5000;
		if (clientType == Client.Type.MARKET)
			port = 5001;
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

	@Override
	public void run() {
		workerGroup = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(workerGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new Decoder(), new AcceptConnectionEncoder(), new BuyOrSellEncoder(),
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

	class ClientHandler extends ChannelInboundHandlerAdapter {
		@Override
		public void channelActive(ChannelHandlerContext context) throws Exception {
			System.out.println("Connection request sent to router.");
			ConnectionRequest message = new ConnectionRequest(Message.Type.CONNECTION_REQUEST.toString());
			context.writeAndFlush(message);
		}

		@Override
		public void channelRead(ChannelHandlerContext context, Object msg) {
			FixMessage message = (FixMessage) msg;
			if (message.getMessageType().equals(Message.Type.CONNECTION_REQUEST.toString())) {
				announceNewConnection(msg);
			} else if (messageIsBuyOrSell(message)) {
				BuyOrSellOrder request = (BuyOrSellOrder) msg;
				try {
					if (!request.createMyChecksum().equals(request.getChecksum()))
						throw new ChecksumIsInvalid();
				} catch (Exception e) {
					System.out.println(e.getMessage());
					return;
				}
				if (messageHasBeenActioned(request))
					return;
				if (message.getMessageType().equals(Message.Type.SELL.toString()))
				marketSellOrderHandler(context, request);
				else
				marketBuyOrderHandler(context, request);
			}
		}

		private boolean messageIsBuyOrSell(FixMessage message) {
			return message.getMessageType().equals(Message.Type.BUY.toString())
					|| message.getMessageType().equals(Message.Type.SELL.toString());
		}

		private void announceNewConnection(Object message) {
			ConnectionRequest request = (ConnectionRequest) message;
			clientID = request.getId();
			System.out.println("Client connected to router with ID: " + clientID);
		}

		private boolean messageHasBeenActioned(BuyOrSellOrder message) {
			if (message.getMessageAction().equals(Message.Action.EXECUTED.toString())
					|| message.getMessageAction().equals(Message.Action.REJECTED.toString())) {
				System.out.println("Response from Market " + message.getMarketId() + ": " + message.getMessageAction());
				return true;
			}
			return false;
		}

		private void marketSellOrderHandler(ChannelHandlerContext context, BuyOrSellOrder message) {
			Random random = new Random();
			if (random.nextBoolean()) {
				System.out.println("Sell order successfully executed.");
				message.setMessageAction(Message.Action.EXECUTED.toString());
			} else {
				System.out.println("Sell order rejected.");
				message.setMessageAction(Message.Action.REJECTED.toString());
			}
			message.updateChecksum();
			context.writeAndFlush(message);
		}

		private void marketBuyOrderHandler(ChannelHandlerContext context, BuyOrSellOrder message) {
			Random random = new Random();
			if (random.nextBoolean()) {
				System.out.println("Buy order rejected.");
				message.setMessageAction(Message.Action.REJECTED.toString());
			} else {
				System.out.println("Buy order successfully executed.");
				message.setMessageAction(Message.Action.EXECUTED.toString());
			}
			message.updateChecksum();
			context.writeAndFlush(message);
		}

		private void channelWrite(ChannelHandlerContext ctx) {
			try {
				String input = getTextFromUser();
				if (input.length() == 0)
					throw new EmptyInput();
				else if (input.toLowerCase().equals("exit"))
					shutdown();
				else if (clientType == Client.Type.BROKER)
					handleBrokerWrite(ctx, input);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				channelWrite(ctx);
			}
		}

		// TODO

		private void handleBrokerWrite(ChannelHandlerContext ctx, String s) throws Exception {
			String[] split = s.split("\\s+");
			if (split.length != 5)
				throw new ErrorInput();
			BuyOrSellOrder out;
			int marketID = checkID(split[1]);
			String instrument = split[2];
			int quantity = Integer.valueOf(split[3]);
			int price = Integer.valueOf(split[4]);
			if (split[0].toLowerCase().equals("sell")) {
				out = new BuyOrSellOrder(Message.Type.SELL.toString(), "-", marketID, clientID, instrument, quantity,
						price);
			} else if (split[0].toLowerCase().equals("buy")) {
				out = new BuyOrSellOrder(Message.Type.BUY.toString(), "-", marketID, clientID, instrument, quantity,
						price);
			} else
				throw new ErrorInput();
			out.updateChecksum();
			ctx.writeAndFlush(out);
			System.out.println("Sending request to router..");
		}

		// TODO

		private int checkID(String id) throws Exception {
			int iID = Integer.valueOf(id);
			if (id.length() != 6)
				throw new ErrorInput();
			return iID;
		}

		// TODO

		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) {
			if (clientType == Client.Type.BROKER)
				channelWrite(ctx);
		}

		// TODO

		private String getTextFromUser() throws Exception {
			System.out.println(
					"Enter request message of type: [sell || buy] [market id] [instrument] [quantity] [price]");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			return br.readLine();
		}
	}
}

// TODO alphabetise
// TODO format