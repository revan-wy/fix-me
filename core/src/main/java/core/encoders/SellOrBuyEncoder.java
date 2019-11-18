package core.encoders;

import core.messages.Message;
import core.messages.MessageSellOrBuy;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

public class SellOrBuyEncoder extends MessageToByteEncoder<MessageSellOrBuy> {
	private final Charset charset=Charset.forName("UTF-8");

	@Override
	protected void encode(ChannelHandlerContext ctx, MessageSellOrBuy msg, ByteBuf out) throws Exception {
		out.writeInt(msg.getTypeLength());
		out.writeCharSequence(msg.getMessageType(), charset);
		if (msg.getMessageType().equals(Message.Type.BUY.toString()) ||
				msg.getMessageType().equals(Message.Type.SELL.toString())) {
			out.writeInt(msg.getActionLength());
			out.writeCharSequence(msg.getMessageAction(), charset);
			out.writeInt(msg.getId());
			out.writeInt(msg.getInstrumentLength());
			out.writeCharSequence(msg.getInstrument(), charset); // on msob
			out.writeInt(msg.getMarketId()); // on fm
			out.writeInt(msg.getPrice()); // on msob
			out.writeInt(msg.getQuantity()); // on msob
			out.writeInt(msg.getChecksumLength()); // fm
			out.writeCharSequence(msg.getChecksum(), charset); // on fm
		}
	}
}

// TODO