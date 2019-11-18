package core.decoders;

import java.nio.charset.Charset;
import java.util.List;

import core.messages.BuyOrSellOrder;
import core.messages.ConnectionRequest;
import core.messages.FixMessage;
import core.messages.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

public class Decoder extends ReplayingDecoder<Object> {
	private final Charset charset = Charset.forName("UTF-8");

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		FixMessage msg = new FixMessage();
		msg.setMessageType(in.readCharSequence(in.readInt(), charset).toString());
		if (msg.getMessageType().equals(Message.Type.CONNECTION_REQUEST.toString())) {
			ConnectionRequest ret = new ConnectionRequest();
			ret.setMessageType(msg.getMessageType());
			ret.setId(in.readInt());
			ret.setChecksum(in.readCharSequence(in.readInt(), charset).toString());
			out.add(ret);
		} else if (	msg.getMessageType().equals(Message.Type.BUY.toString()) ||
					msg.getMessageType().equals(Message.Type.SELL.toString())) {
			BuyOrSellOrder ret = new BuyOrSellOrder();
			ret.setMessageType(msg.getMessageType());
			ret.setMessageAction(in.readCharSequence(in.readInt(), charset).toString());
			ret.setId(in.readInt());
			ret.setInstrument(in.readCharSequence(in.readInt(), charset).toString());
			ret.setMarketId(in.readInt());
			ret.setQuantity(in.readInt());
			ret.setPrice(in.readInt());
			ret.updateChecksum();
			out.add(ret);
		}
	}
}

// TODO format