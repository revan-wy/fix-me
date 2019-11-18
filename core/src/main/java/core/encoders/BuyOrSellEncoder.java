package core.encoders;

import core.messages.Message;
import core.messages.BuyOrSellOrder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

public class BuyOrSellEncoder extends MessageToByteEncoder<BuyOrSellOrder> {// TODO
	
	@Override
	protected void encode(ChannelHandlerContext ctx, BuyOrSellOrder msg, ByteBuf out) throws Exception {// TODO
		final Charset charset=Charset.forName("UTF-8");
		out.writeInt(msg.getTypeLength());
		out.writeCharSequence(msg.getMessageType(), charset);
		if (msg.getMessageType().equals(Message.Type.BUY.toString()) ||// TODO
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

// TODO format