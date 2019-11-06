package core.encoders;

import core.messages.MessageSellOrBuy;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class SellOrBuyEncoder extends MessageToByteEncoder<MessageSellOrBuy> {

	@Override
	protected void encode(ChannelHandlerContext ctx, MessageSellOrBuy message, ByteBuf out) throws Exception {
		out.writeInt(message.getTypeLength());
		out.writeCharSequence(message.getMessageType, charset);
		if (message.getMessageType().equals("MESSAGE_BUY") ||
				message.getMessageType().equals("MESSAGE_SELL")) {
			out.writeInt(message.getActionLength());
			out.writeCharSequence(message.getMessageAction(), charset);
			out.writeInt(message.getId());
			out.writeInt(message.getInstrumentLength());
			out.writeCharSequence(message.getInstrument(), charset);
			out.writeInt(message.getMarketId());
			out.writeInt(message.getPrince());
			out.writeInt(message.getQuantity());
			out.writeInt(message.getChecksumLength());
			out.writeCharSequence(message.getChecksum(), charset)
		}
	}
}
