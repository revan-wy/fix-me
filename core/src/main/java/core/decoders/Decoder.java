package core.decoders;

import java.nio.charset.Charset;
import java.util.List;

import core.messages.FixMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

public class Decoder extends ReplayingDecoder<Object> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		FixMessage message = new FixMessage();
		Charset charset = Charset.forName("UTF-8");
		message.setMessageType(in.readCharSequence(in.readInt(), charset).toString());
		
		// TODO Auto-generated method stub

	}

	// TODO implement decoder class

}
