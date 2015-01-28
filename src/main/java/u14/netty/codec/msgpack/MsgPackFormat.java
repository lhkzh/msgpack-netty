package u14.netty.codec.msgpack;

/**
 * msgpack-一些配置
 * @author zhangheng
 */
public class MsgPackFormat {

	public static final MsgPackFormat DEFAULT = new MsgPackFormat();
	
	public static final MsgPackFormat UNLIMITED = new MsgPackFormat().setMaxContainerSize(0).setMaxRawSize(0);
	
	/**
	 * 限制解码时产生的容器的最大值. <br/>
	 * 小于等于零表示不限制.超过限制，将解码错误。<br/>
	 * 限制的作用是防止恶意攻击<br/>
	 * @default 512
	 */
	private int maxContainerSize = 512;
	/**
	 * 限制解码时产生的最大byte数组.<br/>
	 * 小于等于零表示不限制.超过限制，将解码错误。<br/>
	 * 限制的作用是防止恶意攻击<br/>
	 * @default 65536
	 */
	private int maxRawSize = 65536;
	/**
	 * 枚举类型在序列化选项<br/>
	 * 如果为true，直接调用toString。否则写入ordinal方法写入int值
	 * 
	 * @default true
	 * */
	private boolean enumToString = true;
	/**
	 * 是否支持序列化JavaBean
	 * @default true
	 */
	private boolean javaBeanSupport = true;
	
	public MsgPackFormat() {
		
	}
	
	public int getMaxContainerSize(){
		return maxContainerSize;
	}
	public MsgPackFormat setMaxContainerSize(int value){
		maxContainerSize = value;
		return this;
	}
	
	public int getMaxRawSize(){
		return maxRawSize;
	}
	public MsgPackFormat setMaxRawSize(int value){
		maxRawSize = value;
		return this;
	}

	public boolean isEnumToString(){
		return enumToString;
	}
	public MsgPackFormat setEnumToString(boolean value){
		enumToString = value;
		return this;
	}
	
	public boolean isJavaBeanSupport(){
		return javaBeanSupport;
	}
	public MsgPackFormat setJavaBeanSupport(boolean support){
		javaBeanSupport = support;
		return this;
	}

	public MsgPackFormat copy(){
		MsgPackFormat format = new MsgPackFormat();
		format.enumToString = enumToString;
		format.javaBeanSupport = javaBeanSupport;
		format.maxContainerSize = maxContainerSize;
		format.maxRawSize = maxRawSize;
		return format;
	}
}
