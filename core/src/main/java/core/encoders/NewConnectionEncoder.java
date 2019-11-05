package core.encoders;

import core.messages.MessageAcceptConnection;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NewConnectionEncoder extends MessageToByteEncoder<MessageAcceptConnection> {

	@Override
	protected void encode(ChannelHandlerContext ctx, MessageAcceptConnection msg, ByteBuf out) throws Exception {
		// TODO Auto-generated method stub

	}

	// TODO complete implementation of this encoder

}
