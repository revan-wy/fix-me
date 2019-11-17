package core.decoders;

import core.messages.FixMessage;
import core.messages.MessageAcceptConnection;
import core.messages.MessageSellOrBuy;
import core.messages.MessageTypes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class Decoder extends ReplayingDecoder<Object> {
	private final Charset charset = Charset.forName("UTF-8");

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		FixMessage msg = new FixMessage();
		msg.setMessageType(in.readCharSequence(in.readInt(), charset).toString());
		if (msg.getMessageType().equals(MessageTypes.MESSAGE_ACCEPT_CONNECTION.toString())) {
			MessageAcceptConnection ret = new MessageAcceptConnection();
			ret.setMessageType(msg.getMessageType());
			ret.setId(in.readInt());
			ret.setChecksum(in.readCharSequence(in.readInt(), charset).toString());
			out.add(ret);
		} else if (	msg.getMessageType().equals(MessageTypes.MESSAGE_BUY.toString()) ||
					msg.getMessageType().equals(MessageTypes.MESSAGE_SELL.toString())) {
			MessageSellOrBuy ret = new MessageSellOrBuy();
			ret.setMessageType(msg.getMessageType());
			ret.setMessageAction(in.readCharSequence(in.readInt(), charset).toString());
			ret.setId(in.readInt());
			ret.setInstrument(in.readCharSequence(in.readInt(), charset).toString());
			ret.setMarketId(in.readInt());
			ret.setQuantity(in.readInt());
			ret.setPrice(in.readInt());
			ret.setNewCheckSum();
			out.add(ret);
		}
	}
}
