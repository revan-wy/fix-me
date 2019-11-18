package router;

import java.util.HashMap;

import core.decoders.Decoder;
import core.encoders.AcceptConnectionEncoder;
import core.encoders.SellOrBuyEncoder;
import core.exceptions.ChecksumIsNotEqual;
import core.exceptions.ClientNotInRoutingTable;
import core.messages.ConnectionRequest;
import core.messages.FIXMessage;
import core.messages.MessageSellOrBuy;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Router implements Runnable {
	private int port;
	private EventLoopGroup workerGroup;
	private EventLoopGroup bossGroup;
	private static HashMap<Integer, ChannelHandlerContext> routingTable = new HashMap<>();

	public Router(int port) {
		this.port = port;
	}

	public void acceptNewConnection(ChannelHandlerContext context, Object request) {
		ConnectionRequest response = (ConnectionRequest) request;
		String newId = context.channel().remoteAddress().toString().substring(11);
		newId = newId.concat(brokerOrMarketBool() ? "2" : "3");
		response.setId(Integer.valueOf(newId));
		response.setNewChecksum();
		context.writeAndFlush(response);
		routingTable.put(response.getId(), context);
		System.out.println("New connection made with " + stringBrokerOrMarket() + newId);
	}

	private boolean brokerOrMarketBool() {
		return (this.port != 5001);
	}

	public void checkForErrors(MessageSellOrBuy response) throws Exception {
		if (!response.createMyChecksum().equals(response.getChecksum())) {
			throw new ChecksumIsNotEqual();
		}
		if (!checkIfInTable(response.getMarketId())) {
			throw new ClientNotInRoutingTable();
		}
	}

	private boolean checkIfInTable(int id) {
		return routingTable.containsKey(id);
	}

	public ChannelHandlerContext getFromTableById(int id) {
		return routingTable.get(id);
	}

	public boolean messageIsFromMarket(MessageSellOrBuy response) throws Exception {
		if ((response.getMessageAction().equals("MESSAGE_EXECUTE"))
				|| (response.getMessageAction().equals("MESSAGE_REJECT"))) {
			if (!response.createMyChecksum().equals(response.getChecksum())) {
				throw new ChecksumIsNotEqual();
			}
			getFromTableById(response.getId()).writeAndFlush(response);
			return true;
		}
		return false;
	}

	public class ProcessingHandler extends ChannelInboundHandlerAdapter {
		@Override
		public void channelRead(ChannelHandlerContext context, Object request) {
			FIXMessage message = (FIXMessage)request;
			if (message.getMessageType().equals("MESSAGE_ACCEPT_CONNECTION")) {
				acceptNewConnection(context, request);
			} else if ((message.getMessageType().equals("MESSAGE_BUY")) ||
					(message.getMessageType().equals("MESSAGE_SELL"))) {
				MessageSellOrBuy response = (MessageSellOrBuy)request;
				try {
					checkForErrors(response);
					if (messageIsFromMarket(response)) {
						return;
					};
					System.out.print("Sending request to market " + response.getMarketId());
					getFromTableById(response.getMarketId()).channel().writeAndFlush(response);
				} catch(Exception e) {
					System.out.println(e.getMessage());
					response.setMessageAction("MESSAGE_REJECT");
					response.setNewChecksum();
					context.writeAndFlush(response);
				}
			}
		}
	}

	@Override
	public void run() {
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch)
							throws Exception {
								ch.pipeline().addLast(
									new Decoder(),
									new AcceptConnectionEncoder(),
									new SellOrBuyEncoder(),
									new ProcessingHandler()
							);
						}
					}).option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true);
			ChannelFuture future = b.bind(this.port).sync();
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			shutdown();
		}
	}
	
	private String stringBrokerOrMarket() {
		return this.port == 5001 ? "market" : "broker";
	}

	public void shutdown() {
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
	}
}

// TODO format