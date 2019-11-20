package core.encoders;

import core.messages.ConnectionRequest;
import core.messages.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

public class AcceptConnectionEncoder extends MessageToByteEncoder<ConnectionRequest> {
	private final Charset charset=Charset.forName("UTF-8");

	@Override
	protected void encode(ChannelHandlerContext ctx, ConnectionRequest msg, ByteBuf out) throws Exception {
		out.writeInt(msg.getTypeLength());
		out.writeCharSequence(msg.getMessageType(), charset);
		if (msg.getMessageType().equals(Message.Type.CONNECTION_REQUEST.toString())) { //TODO
			out.writeInt(msg.getId());
			out.writeInt(msg.getChecksumLength());
			out.writeCharSequence(msg.getChecksum(), charset);
		}
	}
}

// TODO format