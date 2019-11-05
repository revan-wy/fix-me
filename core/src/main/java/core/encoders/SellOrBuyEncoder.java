package core.encoders;

import core.messages.MessageSellOrBuy;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class SellOrBuyEncoder extends MessageToByteEncoder<MessageSellOrBuy> {

	@Override
	protected void encode(ChannelHandlerContext ctx, MessageSellOrBuy msg, ByteBuf out) throws Exception {
		// TODO Auto-generated method stub

	}

	// TODO complete this implementation

}
