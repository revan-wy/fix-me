package core.decoders;

import java.nio.charset.Charset;
import java.util.List;

import core.messages.FixMessage;
import core.messages.MessageAcceptConnection;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

public class Decoder extends ReplayingDecoder<Object> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		FixMessage request = new FixMessage();
		Charset charset = Charset.forName("UTF-8");
		request.setMessageType(in.readCharSequence(in.readInt(), charset).toString());
		if (request.getMessageType().equals("MESSAGE_ACCEPT_CONNECTION")) {
			MessageAcceptConnection response = new MessageAcceptConnection();
			response.setMessageType(request.getMessageType());

			// TODO complete this block

		}
		
		// TODO Auto-generated method stub

	}
}
