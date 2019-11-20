package router;

import java.util.HashMap;

import core.Client;
import core.decoders.Decoder;
import core.encoders.AcceptConnectionEncoder;
import core.encoders.BuyOrSellEncoder;
import core.exceptions.ChecksumIsInvalid;
import core.exceptions.ClientNotInRoutingTable;
import core.messages.BuyOrSellOrder;
import core.messages.ConnectionRequest;
import core.messages.FixMessage;
import core.messages.Message;
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
							ch.pipeline().addLast(new Decoder(), new AcceptConnectionEncoder(), new BuyOrSellEncoder(),
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
				BuyOrSellOrder ret = (BuyOrSellOrder) msg;
				try {
					checkForErrors(ret);
					if (checkIfMessageIsRejectedOrExecuted(ret)) // TODO rename this method, Ryan!!!!!!
						return;
					System.out.println("Sending request to market with ID " + ret.getMarketId());
					getFromTableById(ret.getMarketId()).channel().writeAndFlush(ret);
				} catch (Exception e) {
					System.out.println(e.getMessage());
					ret.setMessageAction(Message.Action.REJECTED.toString());
					ret.setChecksum(ret.createMyChecksum());
					ctx.writeAndFlush(ret);
				}
			}
		}
	}

	private void acceptNewConnection(ChannelHandlerContext ctx, Object msg) {
		ConnectionRequest ret = (ConnectionRequest) msg;
		String newID = ctx.channel().remoteAddress().toString().substring(11);
		newID = newID.concat(brokerOrMarketBool() ? "2" : "3");
		ret.setId(Integer.valueOf(newID));
		ret.setNewChecksum();
		ctx.writeAndFlush(ret);
		routingTable.put(ret.getId(), ctx);
		System.out.println("Accepted a connection from " + brokerOrMarketString() + ": " + newID);
	}

	public void checkForErrors(BuyOrSellOrder response) throws Exception {
		if (!response.createMyChecksum().equals(response.getChecksum())) {
			throw new ChecksumIsInvalid();
		}
		if (!checkIfInTable(response.getMarketId())) {
			throw new ClientNotInRoutingTable();
		}
	}

	private boolean checkIfMessageIsRejectedOrExecuted(BuyOrSellOrder ret) throws Exception {
		if (ret.getMessageAction().equals(Message.Action.EXECUTED.toString())
				|| ret.getMessageAction().equals(Message.Action.REJECTED.toString())) {
			if (!ret.createMyChecksum().equals(ret.getChecksum()))
				throw new ChecksumIsInvalid();
			getFromTableById(ret.getId()).writeAndFlush(ret);
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