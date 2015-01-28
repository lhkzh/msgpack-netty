package u14.reflect;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * 日期格式化工具类，主要定义了几种常用时间戳格式
 * @author zhangheng
 */
public class TypeDateFormat extends ThreadLocal<DateFormat> {

	private String pattern;
	public TypeDateFormat(String s){
		this.pattern = s;
	}
	protected DateFormat initialValue() {
		return new SimpleDateFormat(pattern);
	}
	
	/**
	 * yyyy-MM-dd HH:mm:ss.SSS
	 */
	public static TypeDateFormat fmt_23_0 = new TypeDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	/**
	 * yyyy/MM/dd HH:mm:ss.SSS
	 */
	public static TypeDateFormat fmt_23_1 = new TypeDateFormat("yyyy/MM/dd HH:mm:ss.SSS");	
	
	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static TypeDateFormat fmt_19_0 = new TypeDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * yyyy/MM/dd HH:mm:ss
	 */
	public static TypeDateFormat fmt_19_1 = new TypeDateFormat("yyyy/MM/dd HH:mm:ss");
	
	/**
	 * yyyy-MM-dd
	 */
	public static TypeDateFormat fmt_10_0 = new TypeDateFormat("yyyy-MM-dd");
	/**
	 * yyyy/MM/dd
	 */
	public static TypeDateFormat fmt_10_1 = new TypeDateFormat("yyyy/MM/dd");
	
	/**
	 * yy-MM-dd
	 */
	public static TypeDateFormat fmt_8_0 = new TypeDateFormat("yy-MM-dd");
	/**
	 * yy/MM/dd
	 */
	public static TypeDateFormat fmt_8_1 = new TypeDateFormat("yy/MM/dd");
	/**
	 * HH:mm:ss
	 */
	public static TypeDateFormat fmt_8_3 = new TypeDateFormat("HH:mm:ss");
	
	public Date parse(String source) throws ParseException{
		return get().parse(source);
	}
	public String format(Date date){
		return get().format(date);
	}
	
	/**
	 * 利用既定几种时间戳样式尝试进行解析，解析失败或者未定义格式将报错_ClassCastException
	 * @param strVal
	 * @return
	 */
	public static Date parseToDate(String strVal) throws ClassCastException{
		int size = strVal.length();
		if(size<8){
			return null;
		}
		TypeDateFormat format = null;
		switch(size){
			case 19://yyyy-MM-dd HH:mm:ss
				if(strVal.charAt(4)=='/'){
					format = fmt_19_1;
				}else{
					format = fmt_19_0;
				}
			break;
			case 10://yyyy-MM-dd
				if(strVal.charAt(4)=='/'){
					format = fmt_10_1;
				}else{
					format = fmt_10_0;
				}
			break;
			case 23://yyyy-MM-dd HH:mm:ss.SSS
				if(strVal.charAt(4)=='/'){
					format = fmt_23_1;
				}else{
					format = fmt_23_0;
				}
			break;
			case 8://HH:mm:ss or yy-MM-dd
				if(strVal.charAt(3)==':'){
					format = fmt_8_3;
				}else{
					if(strVal.charAt(3)=='/'){
						format = fmt_8_1;
					}else{
						format = fmt_8_0;
					}
				}
			break;
		}
		if(format!=null){
			try {
                return format.parse(strVal);
            } catch (ParseException e) {
                throw new ClassCastException("can not cast to Date, value : " + strVal);
            }
		}
		return null;
	}
}
