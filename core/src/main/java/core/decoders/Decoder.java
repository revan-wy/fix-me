package core.decoders;

import java.util.List;

import core.messages.FixMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

public class Decoder extends ReplayingDecoder<Object> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		FixMessage message = new FixMessage();
		
		// TODO Auto-generated method stub

	}

	// TODO implement decoder class

}
