package router;

import java.util.HashMap;

import core.Client;
import core.decoders.Decoder;
import core.encoders.ConnectionRequestEncoder;
import core.encoders.OrderEncoder;
import core.exceptions.ChecksumIsInvalid;
import core.exceptions.ClientNotRegistered;
import core.messages.ConnectionRequest;
import core.messages.FixMessage;
import core.messages.Message;
import core.messages.Order;
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
	private static HashMap<Integer, ChannelHandlerContext> routingTable = new HashMap<>();
	static final int MARKET_PORT = 5001;
	static final int BROKER_PORT = 5000;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private int port;

	Router(int port) {
		this.port = port;
	}

	private String brokerOrMarketString() {
		return this.port == MARKET_PORT ? "market" : "broker";
	}

	private boolean brokerOrMarketBool() {
		return this.port != MARKET_PORT;
	}

	@Override
	public void run() {
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new Decoder(), new ConnectionRequestEncoder(), new OrderEncoder(),
									new ProcessingHandler());
						}
					}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			shutDown();
		}
	}

	void shutDown() {
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
	}

	class ProcessingHandler extends ChannelInboundHandlerAdapter {
		@Override
		public void channelRead(ChannelHandlerContext context, Object message) {// TODO
			FixMessage fixMessage = (FixMessage) message;
			if (Client.messageIsConnectionRequest(fixMessage))
				acceptNewConnection(context, message);
			else if (Client.messageIsBuyOrSell(fixMessage)) {
				Order order = (Order) message;
				try {
					checkForErrors(order);
					if (messageIsFromMarket(order)) {
						getFromTableById(order.getSenderId()).writeAndFlush(order);
					} else {
						System.out.println("Sending request to market with ID " + order.getMarketId());
						getFromTableById(order.getMarketId()).channel().writeAndFlush(order);
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
					order.setResponse(Message.Response.REJECTED.toString());
					order.updateChecksum();
					context.writeAndFlush(order);
				}
			}
		}
	}

	private void acceptNewConnection(ChannelHandlerContext context, Object message) {// TODO
		ConnectionRequest request = (ConnectionRequest) message;
		String newID = context.channel().remoteAddress().toString().substring(11);
		newID = newID.concat(brokerOrMarketBool() ? "0" : "1");
		request.setSenderId(Integer.valueOf(newID));
		request.updateChecksum();
		context.writeAndFlush(request);
		routingTable.put(request.getSenderId(), context);
		System.out.println("Accepted a connection from " + brokerOrMarketString() + ": " + newID);
	}

	public void checkForErrors(Order order) throws Exception {
		isChecksumValid(order);
		checkIfInTable(order.getMarketId());
	}

	private boolean messageIsFromMarket(Order order) throws Exception {
		if (order.getResponse().equals(Message.Response.EXECUTED.toString())
				|| order.getResponse().equals(Message.Response.REJECTED.toString())) {
			isChecksumValid(order);
			return true;
		}
		return false;
	}

	private void isChecksumValid(Order order) throws Exception {
		if (!order.createMyChecksum().equals(order.getChecksum()))
			throw new ChecksumIsInvalid();
	}

	private void checkIfInTable(int id) throws Exception {
		if (!routingTable.containsKey(id))
			throw new ClientNotRegistered();
	}

	private ChannelHandlerContext getFromTableById(int id) throws Exception {
		checkIfInTable(id);
		return routingTable.get(id);
	}
}

