package core.encoders;

import java.nio.charset.Charset;

import core.messages.Order;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class OrderEncoder extends MessageToByteEncoder<Order> {

	@Override
	protected void encode(ChannelHandlerContext context, Order message, ByteBuf out) throws Exception {
		final Charset charset = Charset.forName("UTF-8");
		out.writeInt(message.getTypeLength());
		out.writeCharSequence(message.getType(), charset);
		out.writeInt(message.getResponseLength());
		out.writeCharSequence(message.getResponse(), charset);
		out.writeInt(message.getSenderId());
		out.writeInt(message.getInstrumentLength());
		out.writeCharSequence(message.getInstrument(), charset);
		out.writeInt(message.getMarketId());
		out.writeInt(message.getPrice());
		out.writeInt(message.getQuantity());
		out.writeInt(message.getChecksumLength());
		out.writeCharSequence(message.getChecksum(), charset);
	}
}
