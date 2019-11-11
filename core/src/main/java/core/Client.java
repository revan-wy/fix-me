package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import core.decoders.Decoder;
import core.encoders.NewConnectionEncoder;
import core.encoders.SellOrBuyEncoder;
import core.exceptions.ChecksumIsNotEqual;
import core.messages.FixMessage;
import core.messages.MessageAcceptConnection;
import core.messages.MessageSellOrBuy;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client implements Runnable {

	private Object clientName;
	private NioEventLoopGroup workerGroup;
	public int uniqueId;

	public Client(String clientName) {
		this.clientName = clientName;
	}

	public static void handleInput(Client client) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input;
		while (true) {
			input = "";
			try {
				input = br.readLine();
			} catch (IOException e) {
				System.err.print(e.getMessage());
			}
			if (!input.equals("") && input.toUpperCase().equals("EXIT")) {
				client.shutdown();
				break;
			}
		}
	}

	@Override
	public void run() {
		String host = "localhost";
		int port = 5000;
		if (clientName.equals("market")) {
			port = 5001;
		}
		workerGroup = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(workerGroup)
					.channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(
								new Decoder(),
								new NewConnectionEncoder(),
								new SellOrBuyEncoder(),
								new ClientHandler()
							);
						}
					}).option(ChannelOption.SO_KEEPALIVE, true);
					ChannelFuture f = b.connect(host, port).sync();
					f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			shutdown();
		}
	}

	private void shutdown() {
		workerGroup.shutdownGracefully();
	}

	public class ClientHandler extends ChannelInboundHandlerAdapter {
		@Override
		public void channelActive(ChannelHandlerContext context) throws Exception {
			System.out.println(clientName + " is waiting for a router connection.");
			MessageAcceptConnection message = new MessageAcceptConnection("MESSAGE_ACCEPT_CONNECTION", 0, 0);
			context.writeAndFlush(message);
		}

		@Override
		public void channelRead(ChannelHandlerContext context, Object request) {
			FixMessage message = (FixMessage)request;
			if (message.getMessageType().equals("message_accept_connection")) {
				MessageAcceptConnection response = (MessageAcceptConnection)request;
				uniqueId = response.getId();
				System.out.println("Connection established with id " + uniqueId);
			} else if ((message.getMessageType().equals("MESSAGE_BUY")) || 
					(message.getMessageType().equals("MESSAGE_SELL"))) {
				MessageSellOrBuy response = (MessageSellOrBuy)request;
				try {
					if (!response.createMyChecksum().equals(response.getChecksum())) {
						throw new ChecksumIsNotEqual();
					}
				} catch (ChecksumIsNotEqual e) {
					System.out.println(e.getMessage());
					return;
				}
				if (checkForBrokerAnswerFromMarket(response)) {
					return;
				}
				if (message.getMessageType().equals("MESSAGE_SELL")) {
					marketForSellRequestLogic(context, response);
				} else {
					marketForBuyRequestLogic(context, response);
				}
			}
		}
		
		public boolean checkForBrokerAnswerFromMarket(MessageSellOrBuy response) {

			// TODO

			return false;
		}
		
		public void marketForSellRequestLogic(ChannelHandlerContext context, MessageSellOrBuy response) {

			// TODO

		}
		
		public void marketForBuyRequestLogic(ChannelHandlerContext context, MessageSellOrBuy response) {

			// TODO

		}

		// TODO
	
	}	

	// TODO

}
