package core.encoders;

import java.nio.charset.Charset;

import core.messages.ConnectionRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ConnectionRequestEncoder extends MessageToByteEncoder<ConnectionRequest> {

	@Override
	protected void encode(ChannelHandlerContext context, ConnectionRequest message, ByteBuf out) throws Exception {
		final Charset charset = Charset.forName("UTF-8");
		out.writeInt(message.getTypeLength());
		out.writeCharSequence(message.getType(), charset);
		out.writeInt(message.getSenderId());
		out.writeInt(message.getChecksumLength());
		out.writeCharSequence(message.getChecksum(), charset);
	}
}
