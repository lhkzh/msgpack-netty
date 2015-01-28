package u14.netty.codec.msgpack;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;

/**
 * MsgPack decoder for netty
 * @author zhangheng
 */
public class MsgPackDecoder extends ByteToMessageDecoder {

	private int maxMsgSize;
	private MsgPackFormat format;
	
	public MsgPackDecoder(int maxMsgSize) {
		this(maxMsgSize, MsgPackFormat.DEFAULT);
	}
	public MsgPackDecoder(int maxMsgSize, MsgPackFormat format) {
		this.maxMsgSize = maxMsgSize;
		this.format = format;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		int pos = in.readerIndex();
		try{
			out.add(MsgPackUnPacker.unpack(in, format));
		}catch(IndexOutOfBoundsException err){
			in.readerIndex(pos);
			if(in.readableBytes()>maxMsgSize){
				throw new TooLongFrameException(String.format("Too Big FrameData=(%d),limit=(%d)", in.readableBytes(), maxMsgSize));
			}
		}
	}

}
