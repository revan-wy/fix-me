package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Scanner;

import core.decoders.Decoder;
import core.encoders.ConnectionRequestEncoder;
import core.encoders.OrderEncoder;
import core.exceptions.BrokerInputError;
import core.exceptions.ChecksumIsInvalid;
import core.exceptions.InputStringEmpty;
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
		private void announceNewConnection(Object message) {
			ConnectionRequest request = (ConnectionRequest) message;
			clientID = request.getSenderId();
			System.out.println("Client connected to router with ID: " + clientID);
		}
//========>
		// private void brokerWriteHandler(ChannelHandlerContext context, String string) throws Exception {
		// 	String[] split = string.split("\\s+");
		// 	if (split.length != 5)
		// 		throw new BrokerInputError();
		// 	Order message;
		// 	if (split[0].toLowerCase().equals("sell")) {
		// 		message = new Order(Message.Type.SELL.toString(), "", verifyId(split[1]), clientID, split[2],
		// 				Integer.valueOf(split[3]), Integer.valueOf(split[4]));
		// 	} else if (split[0].toLowerCase().equals("buy")) {
		// 		message = new Order(Message.Type.BUY.toString(), "", verifyId(split[1]), clientID, split[2],
		// 				Integer.valueOf(split[3]), Integer.valueOf(split[4]));
		// 	} else {
		// 		throw new BrokerInputError();
		// 	}
		// 	message.updateChecksum();;
		// 	context.writeAndFlush(message);
		// 	System.out.println("Sending " + message.getType() + " order to router.");
		// }
//=======>
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
				announceNewConnection(message);
			} else if (messageIsBuyOrSell(fixMessage)) {
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
				channelWrite(context);
		}

		private void channelWrite(ChannelHandlerContext context) throws IOException {
			Order message = null;
			Scanner scan = new Scanner(System.in);
			String command;
			int marketId = 0;
			int validation = 0;
			while (validation == 0){
				printMenue();
            	command = scan.nextLine();
				switch (command) {
				case "1":
					System.out.println("Please input a market ID:");
					try{marketId = scan.nextInt();}catch(Exception ex){
						System.out.println("Invalid Market ID");
						System.out.println("Press Any Key To Continue...");
						System.in.read();
						continue;}
						//Order(Message.Type.SELL.toString(), "", verifyId(split[1]), clientID, split[2],
					message = new Order(Message.Type.SELL.toString(), "", (marketId), 1212, randInstrument(), randQuantity(), randPrice());
					System.out.println("Buy command signaled -> ["+marketId+"]");
					System.out.println(message.toString());
					System.out.println("Press Any Key To Continue...");
					try{System.in.read();}catch(Exception ex){ex.printStackTrace();}
					break;
				case "2":
					System.out.println("Please input a market ID:");
					try{marketId = scan.nextInt();}catch(Exception ex){
						System.out.println("Invalid Market ID");
						System.out.println("Press Any Key To Continue...");
						System.in.read();
						continue;}
					message = new Order("Sell Message", "SELL", (marketId), 1212, randInstrument(), randQuantity(), randPrice());
					System.out.println("Sell command signaled to market -> ["+marketId+"]");
					System.out.println(message.toString());
					System.out.println("Press Any Key To Continue...");
					try{System.in.read();}catch(Exception ex){ex.printStackTrace();}
					break;
				case "3":
					validation = 1;
					System.out.println("Exit command signaled");
					System.out.println("Press Any Key To Continue...");
					scan.nextLine();
					shutdown();
					break;
				}
				//Write and Flush
				message.updateChecksum();;
				context.writeAndFlush(message);
            	System.out.println("Write and Flush goes here");
            }
            System.out.println("Broker Loop Exited");
        	scan.close();
			// }
			// try {
			// 	String input = getBrokerInput();
			// 	if (input.length() == 0)
			// 		throw new InputStringEmpty();
			// 	else if (input.toLowerCase().equals("exit"))
			// 		shutdown();
			// 	else if (clientType == Client.Type.BROKER)
			// 		brokerWriteHandler(context, input);
			// } catch (Exception e) {
			// 	System.out.println(e.getMessage());
			// 	channelWrite(context);
			// }
			// write and flush Here
			// message.updateChecksum();;
			// context.writeAndFlush(message);
		}
//=========>
		private String getBrokerInput() throws Exception {
			System.out.println(
					"Please create a new order. Format: \n[sell || buy] [market id] [instrument] [quantity] [price]");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			return br.readLine();
		}
//=========>
		private boolean messageHasBeenActioned(Order message) {
			if (message.getResponse().equals(Message.Response.EXECUTED.toString())
					|| message.getResponse().equals(Message.Response.REJECTED.toString())) {
				System.out
						.println("Response to " + message.getType() + " order : " + message.getResponse());
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
			message.updateChecksum();;
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
			message.updateChecksum();;
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

	public static boolean messageIsBuyOrSell(FixMessage message) {
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
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println("    ______ _                  __  __        ____            _    ");
        System.out.println("   |  ____(_)                |  \\/  |      |  _ \\          | |");
        System.out.println("   | |__   ___  ___  ______  | \\  / | ___  | |_) |_ __ ___ | | _____ _ __ ");
        System.out.println("   |  __| | \\ \\/ /  |______| | |\\/| |/ _ \\ |  _ <| '__/ _ \\| |/ / _ \\ '__|");
        System.out.println("   | |    | |>  <            | |  | |  __/ | |_) | | | (_) |   <  __/ |   ");
        System.out.println("   |_|    |_/_/\\_\\           |_|  |_|\\___| |____/|_|  \\___/|_|\\_\\___|_|");
        System.out.println("   ________________________________________________________________________");
        System.out.println("  | Welcome to the Fix-Me Broker.                                          |");
        System.out.println("  | The following commands are available, please use numbers 1, 2 or 3.    |");
        System.out.println("  |------------------------------------------------------------------------|");
        System.out.println("  |  1. | BUY a comodity from the market.                                  |");
        System.out.println("  |  2. | SELL a comodity to the market.                                   |");
        System.out.println("  |  3. | EXIT the Fix-Me Broker.                                          |");
        System.out.println("  |________________________________________________________________________|");
    }

    private static int randPrice(){
        Random rand = new Random();
        return(rand.nextInt(100));
    }

    private static String randInstrument(){
        String[] arr = {"GOLD", "OIl", "AVOCADOS", "DIAMONDS", "COFFEE"};
        Random rand = new Random();
        return(arr[rand.nextInt(4)]);
    }
    private static int randQuantity(){
        Random rand = new Random();
        return(rand.nextInt(100));
    }
}
