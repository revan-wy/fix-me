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
	private int serverType;

	Router(int serverType) {
		this.serverType = serverType;
	}

	@Override
	public void run() {
		createServer(serverType);
	}

	private String brokerOrMarketString() {
		return serverType == MARKET_PORT ? "market" : "broker";
	}

	private boolean brokerOrMarketBool() {
		return serverType != MARKET_PORT;
	}

	private void createServer(int port) {
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
		public void channelRead(ChannelHandlerContext ctx, Object msg) {
			FixMessage message = (FixMessage) msg;
			if (Client.messageIsConnectionRequest(message))
				acceptNewConnection(ctx, msg);
			else if (Client.messageIsBuyOrSell(message)) {
				Order ret = (Order) msg;
				try {
					checkForErrors(ret);
					if (messageIsFromMarket(ret))
						return;
					System.out.println("Sending request to market with ID " + ret.getMarketId());
					getFromTableById(ret.getMarketId()).channel().writeAndFlush(ret);
				} catch (Exception e) {
					System.out.println(e.getMessage());
					ret.setResponse(Message.Response.REJECTED.toString());
					ret.updateChecksum();
					ctx.writeAndFlush(ret);
				}
			}
		}
	}

	private void acceptNewConnection(ChannelHandlerContext ctx, Object msg) {
		ConnectionRequest ret = (ConnectionRequest) msg;
		String newID = ctx.channel().remoteAddress().toString().substring(11);
		newID = newID.concat(brokerOrMarketBool() ? "2" : "3");
		ret.setSenderId(Integer.valueOf(newID));
		ret.updateChecksum();
		ctx.writeAndFlush(ret);
		routingTable.put(ret.getSenderId(), ctx);
		System.out.println("Accepted a connection from " + brokerOrMarketString() + ": " + newID);
	}

	public void checkForErrors(Order response) throws Exception {
		if (!response.createMyChecksum().equals(response.getChecksum())) {
			throw new ChecksumIsInvalid();
		}
		if (!checkIfInTable(response.getMarketId())) {
			throw new ClientNotRegistered();
		}
	}

	private boolean messageIsFromMarket(Order order) throws Exception {
		if (order.getResponse().equals(Message.Response.EXECUTED.toString())// TODO extract this
				|| order.getResponse().equals(Message.Response.REJECTED.toString())) {
			if (!order.createMyChecksum().equals(order.getChecksum())) // TODO extract this
				throw new ChecksumIsInvalid();
			getFromTableById(order.getSenderId()).writeAndFlush(order); // TODO change to table checking
			return true;
		}
		return false;
	}

	private boolean checkIfInTable(int id) {
		return routingTable.containsKey(id);
	}

	private ChannelHandlerContext getFromTableById(int id) {
		return routingTable.get(id);
	}
}

// TODO format