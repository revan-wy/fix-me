package core.decoders;

import java.nio.charset.Charset;
import java.util.List;

import core.Client;
import core.messages.ConnectionRequest;
import core.messages.FixMessage;
import core.messages.Order;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

public class Decoder extends ReplayingDecoder<Object> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		final Charset charset = Charset.forName("UTF-8");
		FixMessage message = new FixMessage();
		message.setType(in.readCharSequence(in.readInt(), charset).toString());
		if (Client.messageIsConnectionRequest(message)) {
			out.add(connectionRequestHandler(in, charset, message));
		} else if (Client.messageIsBuyOrSell(message)) {
			out.add(orderHandler(in, charset, message));
		}
	}

	private ConnectionRequest connectionRequestHandler(ByteBuf in, final Charset charset, FixMessage message) {
		ConnectionRequest request = new ConnectionRequest();
		request.setType(message.getType());
		request.setSenderId(in.readInt());
		request.setChecksum(in.readCharSequence(in.readInt(), charset).toString());
		return request;
	}

	private Order orderHandler(ByteBuf in, final Charset charset, FixMessage message) {
		Order order = new Order();
		order.setType(message.getType());
		order.setMessageAction(in.readCharSequence(in.readInt(), charset).toString());
		order.setSenderId(in.readInt());
		order.setInstrument(in.readCharSequence(in.readInt(), charset).toString());
		order.setMarketId(in.readInt());
		order.setQuantity(in.readInt());
		order.setPrice(in.readInt());
		order.setChecksum(in.readCharSequence(in.readInt(), charset).toString());
		return order;
	}

}
