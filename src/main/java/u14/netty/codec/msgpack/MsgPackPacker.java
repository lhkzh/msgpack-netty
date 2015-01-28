package u14.netty.codec.msgpack;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;

import u14.reflect.JxClass;

/**
 * msgpack-序列化类
 * @see https://bitbucket.org/sirbrialliance/msgpack-java-lite/overview
 * @see https://github.com/msgpack/msgpack-java
 * @author zhangheng
 */
class MsgPackPacker {
	/**
	 * 序列化java对象为msgpack数据流
	 * @param item			java对象
	 * @param out			输出到目标流
	 * @throws IllegalArgumentException
	 */
	public static byte[] pack(Object item, MsgPackFormat format)throws IllegalArgumentException{
		ByteBuf out = Unpooled.buffer();
		try{
			pack(item, out, format, new IdentityHashMap<Object, Object>(2));
			byte[] bytes = new byte[out.writerIndex()];
			out.readBytes(bytes);
			return bytes;
		}finally{
			out.release();
		}
	}
	/**
	 * 序列化java对象为msgpack数据流
	 * @param item			java对象
	 * @param out			输出到目标流
	 * @throws IllegalArgumentException
	 */
	public static void pack(Object item, ByteBuf out, MsgPackFormat format)throws IllegalArgumentException{
		pack(item, out, format, new IdentityHashMap<Object, Object>(2));
	}
	/**
	 * 序列化java对象为msgpack数据流
	 * @param item			java对象
	 * @param out			输出到目标流
	 * @param cyclicMap		防止循环引用字典
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void pack(Object item, ByteBuf out, MsgPackFormat format, IdentityHashMap<Object, Object> cyclicMap)throws IllegalArgumentException{
		if (item == null) {
			out.writeByte(Code.NIL);
		} else if (item instanceof Boolean) {
			out.writeByte(((Boolean)item).booleanValue() ? Code.TRUE : Code.FALSE);
		} else if (item instanceof Number) {
			if (item instanceof Float) {
				out.writeByte(Code.FLOAT32);
				out.writeFloat((Float)item);
			} else if (item instanceof Double) {
				out.writeByte(Code.FLOAT64);
				out.writeDouble((Double)item);
			} else {
				long value = ((Number)item).longValue();
				packInteger(value, out);
			}
		}
		else if(item instanceof CharSequence){
			packString(((CharSequence) item).toString(), out);
		}
		else if (item instanceof byte[] || item instanceof ByteBuffer){
			byte[] data;
			if (item instanceof byte[])
				data = (byte[])item;
			else {
				ByteBuffer bb = ((ByteBuffer)item);
				if (bb.hasArray())
					data = bb.array();
				else {
					data = new byte[bb.remaining()];
					bb.get(data);
				}
			}
			packBinary(data, out);
		} else if (item instanceof Map) {
			if(cyclicMap!=null && cyclicMap.containsKey(item))return;
			cyclicMap.put(item, item);
			
			Map<Object, Object> map = (Map<Object, Object>)item;
			packMapHeader(map.size(), out);
			for (Map.Entry<Object, Object> kvp : map.entrySet()) {
				pack(kvp.getKey(), out, format, cyclicMap);
				pack(kvp.getValue(), out, format, cyclicMap);
			}
		} else if (item instanceof Collection) {
			if(cyclicMap!=null && cyclicMap.containsKey(item))return;
			cyclicMap.put(item, item);
			
			Collection<Object> list = (Collection<Object>)item;
			packArrayHeader(list.size(), out);
			for (Object element : list) {
				pack(element, out, format, cyclicMap);
			}
		} else if (item.getClass().isArray()) {
			if(cyclicMap!=null && cyclicMap.containsKey(item))return;
			cyclicMap.put(item, item);
			
			int size = Array.getLength(item);
			packArrayHeader(size, out);
			for(int i=0;i<size;i++){
				pack(Array.get(item, i), out, format, cyclicMap);
			}
		}
//		else if(Enum.class.isInstance(item)){
		else if(item instanceof Enum){
			if(format.isEnumToString()){
				packString(item.toString(), out);
			}else{
				packInteger(((Enum<?>)item).ordinal(), out);
			}
		}
		else if(item instanceof Date){
			packInteger(((Date)item).getTime(), out);
		}
		else {
//			throw new IllegalArgumentException("Cannot msgpack object of type " + item.getClass().getCanonicalName());
			if(format.isJavaBeanSupport()){
				if(cyclicMap!=null && cyclicMap.containsKey(item))return;
				cyclicMap.put(item, item);
				try{
					pack(JxClass.toMap(item), out, format, cyclicMap);
				}catch(Exception err){
					throw new IllegalArgumentException(String.format("Cannot msgpack object of type:(%s)", item.getClass().getCanonicalName()));
				}
			}else{
				throw new IllegalArgumentException(String.format("Cannot msgpack object of type:(%s)", item.getClass().getCanonicalName()));
			}
		}
	}
	
	public static void packInteger(long v, ByteBuf out){
        if (v < -(1L << 5)) {
            if (v < -(1L << 15)) {
                if (v < -(1L << 31)) {
    	        	out.writeByte(Code.INT64);
    	        	out.writeLong(v);
                } else {
                    out.writeByte(Code.INT32);
    	        	out.writeInt((int) v);
                }
            } else {
                //if (v < -(1 << 7)) {
                if(v<-128){
            		out.writeByte(Code.INT16);
    	        	out.writeShort((short) v);
                } else {
                    out.writeByte(Code.INT8);
    	        	out.writeByte((byte) v);
                }
            }
        } else if (v < (1 << 7)) {
            // fixnum
            out.writeByte((byte) v);
        } else {
            if (v < (1L << 16)) {
                if (v < (1 << 8)) {
                    out.writeByte(Code.UINT8);
    	        	out.writeByte((byte) v);
                } else {
                    out.writeByte(Code.UINT16);
    	        	out.writeShort((short) v);
                }
            } else {
                if (v < (1L << 32)) {
                    out.writeByte(Code.UINT32);
    	        	out.writeInt((int) v);
                } else {
                    out.writeByte(Code.UINT64);
    	        	out.writeLong(v);
                }
            }
        }
	}
	public static void packMapHeader(int size, ByteBuf out){
        if(size < (1 << 4)) {
        	out.writeByte((byte) (Code.FIXMAP_PREFIX | size));
        } else if(size < (1 << 16)) {
            out.writeByte(Code.MAP16);
        	out.writeShort((short) size);
        } else {
            out.writeByte(Code.MAP32);
        	out.writeInt(size);
        }
	}
	public static void packArrayHeader(int size, ByteBuf out){
        if(size < (1 << 4)) {
        	out.writeByte((byte) (Code.FIXARRAY_PREFIX | size));
        } else if(size < (1 << 16)) {
            out.writeByte(Code.ARRAY16);
            out.writeShort((short)size);
        } else {
            out.writeByte(Code.ARRAY32);
            out.writeInt(size);
        }
	}
	public static void packString(String str, ByteBuf out){
		byte[] bin = str.getBytes(CharsetUtil.UTF_8);
		int len = bin.length;
        if(len < (1 << 5)) {
        	out.writeByte((byte) (Code.FIXSTR_PREFIX | len));
        } else if(len < (1 << 8)) {
        	out.writeByte(Code.STR8);
        	out.writeByte((byte) len);
        } else if(len < (1 << 16)) {
        	out.writeByte(Code.STR16);
        	out.writeShort((short) len);
        } else {
        	out.writeByte(Code.STR32);
        	out.writeInt(len);
        }
        out.writeBytes(bin);
	}
	public static void packBinary(byte[] data, ByteBuf out){
		int len = data.length;
        if(len < (1 << 8)) {
            out.writeByte(Code.BIN8);
        	out.writeByte((byte) len);
        } else if(len < (1 << 16)) {
            out.writeByte(Code.BIN16);
        	out.writeShort((short) len);
        } else {
            out.writeByte(Code.BIN32);
        	out.writeInt(len);
        }
		out.writeBytes(data);
	}	
}
