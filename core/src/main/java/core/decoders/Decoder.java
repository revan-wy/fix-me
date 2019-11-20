package core.decoders;

import java.nio.charset.Charset;
import java.util.List;

import core.Client;
import core.messages.BuyOrSellOrder;
import core.messages.ConnectionRequest;
import core.messages.FixMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

public class Decoder extends ReplayingDecoder<Object> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		final Charset charset = Charset.forName("UTF-8");
		FixMessage message = new FixMessage();
		message.setMessageType(in.readCharSequence(in.readInt(), charset).toString());
		if (Client.messageIsConnectionRequest(message)) {
			ConnectionRequest request = new ConnectionRequest();
			request.setMessageType(message.getMessageType());
			request.setId(in.readInt());
			request.setChecksum(in.readCharSequence(in.readInt(), charset).toString());
			out.add(request);
		} else if (Client.messageIsBuyOrSell(message)) {
			BuyOrSellOrder order = new BuyOrSellOrder();
			order.setMessageType(message.getMessageType());
			order.setMessageAction(in.readCharSequence(in.readInt(), charset).toString());
			order.setId(in.readInt());
			order.setInstrument(in.readCharSequence(in.readInt(), charset).toString());
			order.setMarketId(in.readInt());
			order.setQuantity(in.readInt());
			order.setPrice(in.readInt());
			order.setChecksum(order.createMyChecksum());
			out.add(order);
		}
	}
}

// TODO alphabetise and format
// TODO format