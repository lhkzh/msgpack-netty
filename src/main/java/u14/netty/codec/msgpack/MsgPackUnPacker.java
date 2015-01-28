package u14.netty.codec.msgpack;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import io.netty.util.CharsetUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;






import u14.reflect.JxClass;

/**
 * msgpack-反序列化类
 * @see https://bitbucket.org/sirbrialliance/msgpack-java-lite/overview
 * @see https://github.com/msgpack/msgpack-java
 * @author zhangheng
 */
public class MsgPackUnPacker {

	/**
	 * 反序列化msgpack数据
	 * @param in				输入流
	 * @param format			反序列化配置
	 * @return 					反序列化后的对象
	 * @throws IndexOutOfBoundsException 如果输入流中字节流不充足
	 * @throws DecoderException 		   不支持的类型或者非法的数据
	 */
	public static Object unpack(byte[] in, MsgPackFormat format) throws DecoderException,IndexOutOfBoundsException {
		ByteBuf buf = Unpooled.wrappedBuffer(in);
		try{
			return unpack(in, format);
		}finally{
			buf.release();
		}
	}
	/**
	 * 反序列化msgpack数据
	 * @param in				输入流
	 * @param format			反序列化配置
	 * @return 					反序列化后的对象
	 * @throws IndexOutOfBoundsException 如果ByteBuf字节流不充足
	 * @throws DecoderException 		   不支持的类型或者非法的数据
	 */
	public static Object unpack(ByteBuf in, MsgPackFormat format) throws DecoderException,IndexOutOfBoundsException {
		byte byteValue = in.readByte();
        if(Code.isFixInt(byteValue)) {
            return byteValue;
        }
		else if(Code.isFixedArray(byteValue)){
			return unpackList(byteValue & 0x0f, in, format);
		}
		else if(Code.isFixedMap(byteValue)){
			return unpackMap(byteValue & 0x0f, in, format);
		}
		else if (Code.isFixStr(byteValue)){
			return unpackString(byteValue & 0x1f, in, format);
		} 
		else if (Code.isFixedRaw(byteValue)){
			return unpackBinary(byteValue & 0x1F, in, format);
		} 
		switch (byteValue) {
			case Code.NIL:
				return null;
			case Code.FALSE:
				return false;
			case Code.TRUE:
				return true;
			case Code.FLOAT32:
				return in.readFloat();
			case Code.FLOAT64:
				return in.readDouble();
				
            case Code.INT8: // signed int 8
                return in.readByte();
            case Code.INT16:
                return in.readShort();
            case Code.INT32:
                return in.readInt();
            case Code.INT64: // signed int 64
                return in.readLong();
            case Code.UINT8: // unsigned int 8
                byte u8 = in.readByte();
                if(u8 < 0) {
                    return (short)(u8 & 0xFF);
                }
                else {
                    return u8;
                }
            case Code.UINT16: // unsigned int 16
                short u16 = in.readShort();
                if(u16 < 0) {
                    return u16 & 0xFFFF;
                }
                else {
                    return u16;
                }
            case Code.UINT32: // unsigned int 32
                int u32 = in.readInt();
                if(u32 < 0) {
                    return (long) (u32 & 0x7fffffff) + 0x80000000L;
                } else {
                   return u32;
                }
            case Code.UINT64: // unsigned int 64
                long u64 = in.readLong();
                if(u64 < 0L) {
                    return BigInteger.valueOf(u64 + Long.MAX_VALUE + 1L).setBit(63);
                } else {
                    return u64;
                }
            
			case Code.ARRAY16:
				return unpackList(in.readShort() & 0xffff, in, format);
			case Code.ARRAY32:
				return unpackList(in.readInt(), in, format);
				
			case Code.MAP16:
				return unpackMap(in.readShort() & 0xffff, in, format);
			case Code.MAP32:
				return unpackMap(in.readInt(), in, format);
				
			case Code.STR8:
				return unpackString(in.readByte() & 0xff, in, format);
			case Code.STR16:
				return unpackString(in.readShort() & 0xffff, in, format);
			case Code.STR32:
				return unpackString(in.readInt(), in, format);
				
			case Code.BIN8:
				return unpackBinary(in.readByte() & 0xff, in, format);
			case Code.BIN16:
				return unpackBinary(in.readShort() & 0xffff, in, format);
			case Code.BIN32:
				return unpackBinary(in.readInt(), in, format);
		}
		throw new DecoderException(String.format("Input contains invalid type value:(%d)",byteValue));
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static List unpackList(int size, ByteBuf in, MsgPackFormat format) throws DecoderException {
		if (size<0){
			throw new DecoderException(String.format("Array to unpack too small!(%d)",size));
		}
		if(format.getMaxContainerSize()>0 && size>format.getMaxContainerSize()){
			throw new DecoderException(String.format("Array to unpack too large! size=(%d),limit=(%d)",size,format.getMaxContainerSize()));
		}
		List ret = new ArrayList(size);
		for (int i = 0; i < size; ++i) {
			ret.add(unpack(in, format));
		}
		return ret;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static Object unpackMap(int size, ByteBuf in, MsgPackFormat format) throws DecoderException {
		if (size < 0){
			throw new DecoderException(String.format("Map to unpack too small!(%d)",size));
		}
		if(format.getMaxContainerSize()>0 && size>format.getMaxContainerSize()){
			throw new DecoderException(String.format("Map to unpack too large! size=(%d),limit=(%d)",size,format.getMaxContainerSize()));
		}
		Map map = new HashMap(size);
		for (int i = 0; i < size; ++i) {
			Object key = unpack(in, format);
			Object value = unpack(in, format);
			map.put(key, value);
		}
		if(format.isJavaBeanSupport())
			return JxClass.tryToJavaBean(map);
		return map;
	}

	protected static String unpackString(int size, ByteBuf in, MsgPackFormat format) throws DecoderException {
		if (size < 0){
			throw new DecoderException(String.format("String to unpack too small!(%d)",size));
		}
		if(format.getMaxRawSize()>0 && size>format.getMaxRawSize()){
			throw new DecoderException(String.format("String to unpack too large! size=(%d),limit=(%d)",size,format.getMaxRawSize()));
		}
		if(size==0)return "";
		byte[] data = new byte[size];
		in.readBytes(data);
		return new String(data, CharsetUtil.UTF_8);
	}
	
	protected static byte[] unpackBinary(int size, ByteBuf in, MsgPackFormat format) throws DecoderException{
		if (size < 0){
			throw new DecoderException(String.format("Binary to unpack too small!(%d)",size));
		}
		if(format.getMaxRawSize()>0 && size>format.getMaxRawSize()){
			throw new DecoderException(String.format("Binary to unpack too large! size=(%d),limit=(%d)",size,format.getMaxRawSize()));
		}
		byte[] data = new byte[size];
		in.readBytes(data);
		return data;
	}
	
}
