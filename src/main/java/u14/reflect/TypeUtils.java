package u14.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

import u14.reflect.JxClass;


/**
 * 类型转换
 * @author wenshao<szujobs@hotmail.com>
 * @author zhangheng
 */
public class TypeUtils {

    public static final String castToString(Object value) {
        if (value == null) {
            return null;
        }

        return value.toString();
    }

    public static final Byte castToByte(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).byteValue();
        }
        if (value instanceof CharSequence) {
        	String str = ((CharSequence) value).toString();
            if (str.length() == 0) {
                return null;
            }
            return Byte.parseByte(str);
        }
        
        throw new ClassCastException("can not cast to byte, value : " + value);
    }

    public static final Character castToChar(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Character) {
            return (Character) value;
        }
        if (value instanceof CharSequence) {
        	String str = ((CharSequence) value).toString();
            if (str.length() == 0) {
                return null;
            }
            if (str.length() != 1) {
                throw new ClassCastException("can not cast to byte, value : " + value);
            }
            return str.charAt(0);
        }
        throw new ClassCastException("can not cast to byte, value : " + value);
    }

    public static Character castToCharacter(Object value){
    	if (value == null) {
            return null;
        }
    	if(value instanceof Character){
    		return (Character)value;
    	}
    	if(value instanceof Number){
    		return (char)((Number)value).shortValue();
    	}
    	if(value instanceof CharSequence) {
    		CharSequence cs = (CharSequence)value;
    		if(cs.length()>0)
    			return cs.charAt(0);
    		return null;
    	}
    	throw new ClassCastException("can not cast to character, value : " + value);
    }
    
    public static final Short castToShort(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).shortValue();
        }

        if (value instanceof CharSequence) {
        	String str = ((CharSequence) value).toString();
            if (str.length() == 0) {
                return null;
            }
            return Short.valueOf(str);
        }

        throw new ClassCastException("can not cast to short, value : " + value);
    }

    public static final BigDecimal castToBigDecimal(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }

        if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        }

        String strVal = value.toString();
        if (strVal.length() == 0) {
            return null;
        }

        return new BigDecimal(strVal);
    }

    public static final BigInteger castToBigInteger(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof BigInteger) {
            return (BigInteger) value;
        }

//        if (value instanceof Float || value instanceof Double) {
//            return BigInteger.valueOf(((Number) value).longValue());
//        }
        if(value instanceof Number){
        	return BigInteger.valueOf(((Number)value).longValue());
        }

        String strVal = value.toString();
        if (strVal.length() == 0) {
            return null;
        }

        return new BigInteger(strVal);
    }

    public static final Float castToFloat(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }

        if (value instanceof CharSequence) {
        	String str = ((CharSequence) value).toString();
            if (str.length() == 0) {
                return null;
            }

            return Float.valueOf(str);
        }

        throw new ClassCastException("can not cast to float, value : " + value);
    }

    public static final Double castToDouble(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof CharSequence) {
        	String str = ((CharSequence) value).toString();
            if (str.length() == 0) {
                return null;
            }
            return Double.valueOf(str);
        }

        throw new ClassCastException("can not cast to double, value : " + value);
    }

    public static final Date castToDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Calendar) {
            return ((Calendar) value).getTime();
        }
        if (value instanceof Date) {
            return (Date) value;
        }
        long longValue = 0;
        if (value instanceof Number) {
            longValue = ((Number) value).longValue();
        }
        if (value instanceof CharSequence) {
        	String str = ((CharSequence) value).toString();
            if (str.length() == 0) {
                return null;
            }
            if(isTimeNumber(str)){
            	longValue = Long.parseLong(str);
            }else{
            	Date date = TypeDateFormat.parseToDate(str);
                if(date!=null)return date;
            }
        }
        if (longValue <= 0) {
            throw new ClassCastException("can not cast to Date, value : " + value);
        }
        return new Date(longValue);
    }
	public static final java.sql.Date castToSqlDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Calendar) {
            return new java.sql.Date(((Calendar) value).getTimeInMillis());
        }
        if (value instanceof java.sql.Date) {
            return (java.sql.Date) value;
        }
        if (value instanceof java.util.Date) {
            return new java.sql.Date(((java.util.Date) value).getTime());
        }
        long longValue = 0;
        if (value instanceof Number) {
            longValue = ((Number) value).longValue();
        }
        if (value instanceof CharSequence) {
        	String str = ((CharSequence) value).toString();
            if (str.length() == 0) {
                return null;
            }
            if(isTimeNumber(str)){
            	longValue = Long.parseLong(str);
            }else{
            	Date date = TypeDateFormat.parseToDate(str);
                if(date!=null)return new java.sql.Date(date.getTime());
            }
        }
        if (longValue <= 0) {
            throw new ClassCastException("can not cast to Date, value : " + value);
        }
        return new java.sql.Date(longValue);
    }

    public static final java.sql.Timestamp castToTimestamp(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Calendar) {
            return new java.sql.Timestamp(((Calendar) value).getTimeInMillis());
        }
        if (value instanceof java.sql.Timestamp) {
            return (java.sql.Timestamp) value;
        }
        if (value instanceof java.util.Date) {
            return new java.sql.Timestamp(((java.util.Date) value).getTime());
        }
        long longValue = 0;
        if (value instanceof Number) {
            longValue = ((Number) value).longValue();
        }
        if (value instanceof CharSequence) {
            String str = ((CharSequence) value).toString();
            if (str.length() == 0) {
                return null;
            }
            if(isTimeNumber(str)){
            	longValue = Long.parseLong(str);
            }else{
            	Date date = TypeDateFormat.parseToDate(str);
                if(date!=null)longValue = date.getTime();
            }
        }
        if (longValue <= 0) {
            throw new ClassCastException("can not cast to Date, value : " + value);
        }
        return new java.sql.Timestamp(longValue);
    }
    private static Pattern LongDateNumberPattern = Pattern.compile("^[0-9]+$");
    private static boolean isTimeNumber(CharSequence sq){
    	if(sq.length()>0&&sq.length()<20){
    		return LongDateNumberPattern.matcher(sq).find();
    	}
    	return false;
    }
    public static final Long castToLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof CharSequence) {
        	String str = ((CharSequence) value).toString();
            if (str.length() == 0) {
                return null;
            }
            return Long.valueOf(str);
        }
        throw new ClassCastException("can not cast to long, value : " + value);
    }

    public static final Integer castToInt(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof CharSequence) {
        	String str = ((CharSequence) value).toString();
            if (str.length() == 0) {
                return null;
            }
            return Integer.valueOf(str);
        }
        throw new ClassCastException("can not cast to int, value : " + value);
    }

    public static final Boolean castToBoolean(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue() == 1;
        }
        if (value instanceof CharSequence) {
        	String str = ((CharSequence) value).toString();
            if (str.length() == 0) {
                return null;
            }
            if ("true".equals(str)) {
                return Boolean.TRUE;
            }
            if ("false".equals(str)) {
                return Boolean.FALSE;
            }
            if ("NO".equals(str)) {
                return Boolean.FALSE;
            }
            if ("0".equals(str)) {
                return Boolean.FALSE;
            }
            return true;
        }
        throw new ClassCastException("can not cast to bool, value : " + value);
    }
    
    public static final byte[] castToBytes(Object value) {
        if (value instanceof byte[]) {
            return (byte[]) value;
        }
        if(value instanceof ByteBuffer){
        	return ((ByteBuffer)value).array();
        }
        /*if(value instanceof MsgRawWrap){
        	return ((MsgRawWrap)value).getByteArray();
        }*/
        if(value instanceof Byte[]){
        	Byte[] barr = (Byte[])value;
        	byte[] arr = new byte[barr.length];
        	for(int i=0;i<barr.length;i++){arr[i]=barr[i];}
        	return arr;
        }
        throw new ClassCastException("can not cast to byte[], value : " + value);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final <T> T castObject(Object obj, Class<T> clazz, boolean useCustomJavaBeanMethod) {
        if (obj == null) {
            return null;
        }
        if (clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }
        if (clazz == obj.getClass()) {
            return (T) obj;
        }
        if (obj instanceof Map) {
            if (clazz == Map.class) {
                return (T) obj;
            }
            try {
				return (T) JxClass.fromMap((Map)obj, clazz, useCustomJavaBeanMethod);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
        }
        if (clazz.isArray()) {
        	if(obj.getClass().isArray()){
        		int listLength = Array.getLength(obj);
        		Collection list = new ArrayList(listLength);
        		for(int i=0;i<listLength;i++){
        			Object temp = Array.get(obj, i);
        			list.add(temp);
        		}
            }
            if (obj instanceof Collection) {
                Collection collection = (Collection) obj;
                int index = 0;
                Object array = Array.newInstance(clazz.getComponentType(), collection.size());
                for (Object item : collection) {
//                    Object value = cast(item, clazz.getComponentType());
                	Object value = castObject(item, clazz.getComponentType(), useCustomJavaBeanMethod);
                    Array.set(array, index, value);
                    index++;
                }
                return (T) array;
            }
        }

        if (clazz.isAssignableFrom(obj.getClass())) {
            return (T) obj;
        }
        if (clazz == boolean.class || clazz == Boolean.class) {
            return (T) castToBoolean(obj);
        }
        if (clazz == byte.class || clazz == Byte.class) {
            return (T) castToByte(obj);
        }
		if (clazz == char.class || clazz == Character.class) {
			return (T) castToCharacter(obj);
		}
        if (clazz == short.class || clazz == Short.class) {
            return (T) castToShort(obj);
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T) castToInt(obj);
        }
        if (clazz == long.class || clazz == Long.class) {
            return (T) castToLong(obj);
        }
        if (clazz == float.class || clazz == Float.class) {
            return (T) castToFloat(obj);
        }
        if (clazz == double.class || clazz == Double.class) {
            return (T) castToDouble(obj);
        }
        if (clazz == String.class) {
            return (T) castToString(obj);
        }
        if (clazz == BigDecimal.class) {
            return (T) castToBigDecimal(obj);
        }
        if (clazz == BigInteger.class) {
            return (T) castToBigInteger(obj);
        }
        if (clazz == Date.class) {
            return (T) castToDate(obj);
        }
        if (clazz == java.sql.Date.class) {
            return (T) castToSqlDate(obj);
        }
        if (clazz == java.sql.Timestamp.class) {
            return (T) castToTimestamp(obj);
        }
        if(clazz==Number.class){
        	return (T) Number.class.cast(obj);
        }
        if (clazz.isEnum()) {
            return (T) castToEnum(obj, clazz);
        }
        if (Calendar.class.isAssignableFrom(clazz)) {
            Date date = castToDate(obj);
            Calendar calendar;
            if (clazz == Calendar.class) {
                calendar = Calendar.getInstance();
            } else {
                try {
                    calendar = (Calendar) clazz.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("can not cast to : " + clazz.getName(), e);
                }
            }
            calendar.setTime(date);
            return (T) calendar;
        }

        if (obj instanceof String) {
            String strVal = (String) obj;
            if (strVal.length() == 0) {
                return null;
            }
        }
        throw new RuntimeException("can not cast to : " + clazz.getName());
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static final <T> T castToEnum(Object obj, Class<T> clazz) {
        try {
            if (obj instanceof String) {
                String name = (String) obj;
                if (name.length() == 0) {
                    return null;
                }
                return (T) Enum.valueOf((Class<? extends Enum>) clazz, name);
            }
            if (obj instanceof Number) {
                int ordinal = ((Number) obj).intValue();
                Method method = clazz.getMethod("values");
                Object[] values = (Object[]) method.invoke(null);
                for (Object value : values) {
                    Enum e = (Enum) value;
                    if (e.ordinal() == ordinal) {
                        return (T) e;
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("can not cast to : " + clazz.getName(), ex);
        }
        throw new RuntimeException("can not cast to : " + clazz.getName());
    }
    /*@SuppressWarnings("unchecked")
    private static final <T> T cast(Object obj, Type type) {
        if (obj == null) {
            return null;
        }
        if (type instanceof ParameterizedType) {
            return (T) cast(obj, (ParameterizedType) type);
        }
        if (obj instanceof String) {
            String strVal = (String) obj;
            if (strVal.length() == 0) {
                return null;
            }
        }
        if (type instanceof TypeVariable) {
            return (T) obj;
        }
//        if (type instanceof Class) {
//            return (T) cast(obj, ((Class<T>) type).getComponentType());
//        }
        throw new MsgException("can not cast to : " + type);
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private static final <T> T cast(Object obj, ParameterizedType type, boolean useCustomJavaBeanMethod) {
        Type rawTye = type.getRawType();
        if (rawTye == List.class || rawTye == ArrayList.class) {
            Type itemType = type.getActualTypeArguments()[0];
            if (obj instanceof Iterable) {
                List list = new ArrayList();
                for (Iterator it = ((Iterable) obj).iterator(); it.hasNext();) {
                    Object item = it.next();
//                    list.add(cast(item, itemType));
                    item = castObject(it.next(), itemType.getClass(), useCustomJavaBeanMethod);
                	list.add(item);
                }
                return (T) list;
            }
        }
        if (rawTye == Map.class || rawTye == HashMap.class) {
            Type keyType = type.getActualTypeArguments()[0];
            Type valueType = type.getActualTypeArguments()[1];
            if (obj instanceof Map) {
                Map map = new HashMap();
                for (Map.Entry entry : ((Map<?, ?>) obj).entrySet()) {
//                    Object key = cast(entry.getKey(), keyType);
//                    Object value = cast(entry.getValue(), valueType);
                	Object key = castObject(entry.getKey(), keyType.getClass(), useCustomJavaBeanMethod);
                	Object value = castObject(entry.getValue(), valueType.getClass(), useCustomJavaBeanMethod);
                    map.put(key, value);
                }
                return (T) map;
            }
        }
        if (obj instanceof String) {
            String strVal = (String) obj;
            if (strVal.length() == 0) {
                return null;
            }
        }
        if (type.getActualTypeArguments().length == 1) {
            Type argType = type.getActualTypeArguments()[0];
            if (argType instanceof WildcardType) {
//                return (T) cast(obj, rawTye);
            	return (T)castObject(obj, rawTye.getClass(), useCustomJavaBeanMethod);
            }
        }
        throw new MsgException("can not cast to : " + type);
    }
    */
}
