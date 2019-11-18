package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import core.decoders.Decoder;
import core.encoders.AcceptConnectionEncoder;
import core.encoders.SellOrBuyEncoder;
import core.exceptions.ChecksumIsNotEqual;
import core.exceptions.EmptyInput;
import core.exceptions.ErrorInput;
import core.messages.ConnectionRequest;
import core.messages.FIXMessage;
import core.messages.Message;
import core.messages.MessageSellOrBuy;
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

	public enum Type {
		BROKER, MARKET
	}

	private Client.Type clientType;
	private EventLoopGroup workerGroup;
	private int clientID;

	public Client(Client.Type clientName) {
		this.clientType = clientName;
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
		String host = "localhost";
		int port = 5000;
		if (clientType == Client.Type.MARKET)
			port = 5001;
		workerGroup = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(workerGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new Decoder(), new AcceptConnectionEncoder(), new SellOrBuyEncoder(),
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
			ConnectionRequest message = new ConnectionRequest(Message.Type.CONNECTION_REQUEST.toString(), 0, 0);
			context.writeAndFlush(message);
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) {
			FIXMessage message = (FIXMessage) msg;
			if (message.getMessageType().equals(Message.Type.CONNECTION_REQUEST.toString())) {
				ConnectionRequest ret = (ConnectionRequest) msg;
				clientID = ret.getId();
				System.out.println("Connection with router established. ID: " + clientID);
			} else if (message.getMessageType().equals(Message.Type.BUY.toString())
					|| message.getMessageType().equals(Message.Type.SELL.toString())) {
				MessageSellOrBuy ret = (MessageSellOrBuy) msg;
				try {
					if (!ret.createMyChecksum().equals(ret.getChecksum()))
						throw new ChecksumIsNotEqual();
				} catch (Exception e) {
					System.out.println(e.getMessage());
					return;
				}
				if (checkForBrokerAnswerFromMarket(ret))
					return;
				if (message.getMessageType().equals(Message.Type.SELL.toString()))
					marketForSellRequestLogic(ctx, ret);
				else
					marketForBuyRequestLogic(ctx, ret);
			}
		}

		private boolean checkForBrokerAnswerFromMarket(MessageSellOrBuy ret) {
			if (ret.getMessageAction().equals(Message.Action.EXECUTE.toString())
					|| ret.getMessageAction().equals(Message.Action.REJECT.toString())) {
				System.out.println("Answer for your request: " + ret.getMessageAction());
				return true;
			}
			return false;
		}

		private void marketForSellRequestLogic(ChannelHandlerContext ctx, MessageSellOrBuy ret) {
			Random random = new Random();
			if (random.nextBoolean()) {
				System.out.println("EXECUTE. Thank you for this instrument!");
				ret.setMessageAction(Message.Action.EXECUTE.toString());
			} else {
				System.out.println("REJECT. Cause: we don't want this instrument.");
				ret.setMessageAction(Message.Action.REJECT.toString());
			}
			ret.setNewChecksum();
			ctx.writeAndFlush(ret);
		}

		private void marketForBuyRequestLogic(ChannelHandlerContext ctx, MessageSellOrBuy ret) {
			Random random = new Random();
			int randomInt = random.nextInt(100);
			if (randomInt >= 0 && randomInt < 20) {
				System.out.println("REJECT. Cause: no such instrument on market!");
				ret.setMessageAction(Message.Action.REJECT.toString());
			} else if (randomInt >= 20 && randomInt < 40) {
				System.out.println("REJECT. Cause: not enough amount of such instrument on market!");
				ret.setMessageAction(Message.Action.REJECT.toString());
			} else {
				System.out.println("EXECUTE. Thank you for buying!");
				ret.setMessageAction(Message.Action.EXECUTE.toString());
			}
			ret.setNewChecksum();
			ctx.writeAndFlush(ret);
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

		private void handleBrokerWrite(ChannelHandlerContext ctx, String s) throws Exception {
			String[] split = s.split("\\s+");
			if (split.length != 5)
				throw new ErrorInput();
			MessageSellOrBuy out;
			int marketID = checkID(split[1]);
			String instrument = split[2];
			int quantity = Integer.valueOf(split[3]);
			int price = Integer.valueOf(split[4]);
			if (split[0].toLowerCase().equals("sell")) {
				out = new MessageSellOrBuy(Message.Type.SELL.toString(), "-", marketID, clientID, instrument, quantity,
						price);
			} else if (split[0].toLowerCase().equals("buy")) {
				out = new MessageSellOrBuy(Message.Type.BUY.toString(), "-", marketID, clientID, instrument, quantity,
						price);
			} else
				throw new ErrorInput();
			out.setNewChecksum();
			ctx.writeAndFlush(out);
			System.out.println("Sending request to router..");
		}

		private int checkID(String id) throws Exception {
			int iID = Integer.valueOf(id);
			if (id.length() != 6)
				throw new ErrorInput();
			return iID;
		}

		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) {
			if (clientType == Client.Type.BROKER)
				channelWrite(ctx);
		}

		private String getTextFromUser() throws Exception {
			System.out.println(
					"Enter request message of type: [sell || buy] [market id] [instrument] [quantity] [price]");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			return br.readLine();
		}
	}
}

// TODO format