package core.decoders;

import java.nio.charset.Charset;
import java.util.List;

import core.messages.FixMessage;
import core.messages.MessageAcceptConnection;
import core.messages.MessageSellOrBuy;
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
			response.setId(in.readInt());
			response.setChecksum(in.readCharSequence(in.readInt(), charset).toString());
			out.add(response);
		} else if (request.getMessageType().equals("MESSAGE_BUY") || 
				request.getMessageType().equals("MESSAGE_SELL")) {
			MessageSellOrBuy response = new MessageSellOrBuy();
			response.setMessageType(request.getMessageType());
			response.setMessageAction(in.readCharSequence(in.readInt(), charset).toString());
			response.setId(in.readInt());

			// TODO complete this block

		}
		
		// TODO Auto-generated method stub

	}
}
