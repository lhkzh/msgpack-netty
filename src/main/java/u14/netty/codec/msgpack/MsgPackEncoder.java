package u14.netty.codec.msgpack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * MsgPack encoder for netty
 * @author zhangheng
 */
public class MsgPackEncoder extends MessageToByteEncoder<Object> {

	private MsgPackFormat format;
	
	public MsgPackEncoder() {
		this(MsgPackFormat.DEFAULT);
	}
	public MsgPackEncoder(MsgPackFormat format) {
		this.format = format;
	}
	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out)
			throws Exception {
		int pos = out.writerIndex();
		try{
			MsgPackPacker.pack(msg, out, format);
		}catch(IllegalArgumentException err){
			out.writerIndex(pos);
			throw err;
		}
	}

}
