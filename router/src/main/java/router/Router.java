package router;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class Router implements Runnable {
	int	port;
	
	Router(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
	}

	
}