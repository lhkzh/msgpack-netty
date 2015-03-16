package u14.reflect;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * JavaBean转Map工具类
 * @author zhangheng
 */
final public class JxClass {
	
	public static final String CLASS_NAME = "#$";
	public static final String CLASS_STATIC = "#@";
	
	public static boolean cacheJavaClass = true;
	public static boolean supportCyclicReference = false;
	public static boolean supportCustomJavaBeanMethod = false;

	public static Map<String,Object> toMap(Object pojo) throws Exception{
		return toMap(pojo, supportCustomJavaBeanMethod, new IdentityHashMap<Object, Object>(2), supportCyclicReference);
	}
	public static Map<String,Object> toMap(Object pojo, boolean supportCusstormBeanMethod) throws Exception{
		return toMap(pojo, supportCusstormBeanMethod, new IdentityHashMap<Object, Object>(2), supportCyclicReference);
	}
	public static Map<String,Object> toMap(Object pojo, boolean supportCusstormBeanMethod, boolean supportReference) throws Exception{
		if(pojo==null){
			return null;
		}
		return toMap(pojo, supportCusstormBeanMethod, new IdentityHashMap<Object, Object>(2), supportReference);
	}
	/**
	 * 将对象Bean转为IMsgObject
	 * @param pojo							Bean实例
	 * @param supportCusstormBeanMethod		支持传统getter/setter
	 * @param dict							循环引用字典
	 * @param supportReference				是否支持转换后的对象循环引用
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> toMap(Object pojo, boolean supportCusstormBeanMethod, IdentityHashMap<Object, Object> dict, boolean supportReference) throws Exception{
		if(pojo==null){
			return null;
		}
		if(Class.class.isInstance(pojo)){
			Map<String,Object> fieldMap = new HashMap<String, Object>(2);
			fieldMap.put(CLASS_NAME, ((Class<?>)pojo).getCanonicalName());
			fieldMap.put(CLASS_STATIC, true);
			return fieldMap;
		}
		Class<?> pojoClazz = pojo.getClass();
		if (pojoClazz.getCanonicalName() == null) {
			throw new IllegalArgumentException(
					"Anonymous classes cannot be serialized!");
		}
		if(dict!=null && dict.containsKey(pojo)){
			if(supportReference){
				return (Map<String,Object>)dict.get(pojo);
			}
			return null;
		}
		
		String fieldName = null;
		Object fieldValue = null;
		
		JxAcc[] accList = JxAcc.getAccList(pojoClazz, supportCusstormBeanMethod);
		
		Map<String,Object> fieldMap = new HashMap<String, Object>(accList.length+1);
		if(dict!=null)dict.put(pojo, fieldMap);
		
		for (JxAcc jfield : accList) {
			fieldName = jfield.name;
//			System.out.println(fieldName);
			if(fieldMap.containsKey(fieldName)){
				continue;
			}
			fieldValue = jfield.get(pojo);
			if(fieldValue!=null){
				Object simpleValue = toSimple(fieldValue, supportCusstormBeanMethod, dict, supportReference);
				if(simpleValue!=null){
					fieldMap.put(fieldName, simpleValue);
				}
			}
//			else{
//				fieldMap.put(fieldName, fieldValue);
//			}
		}
		fieldMap.put(CLASS_NAME, pojoClazz.getCanonicalName());
		return fieldMap;
	}
	@SuppressWarnings("rawtypes")
	private static Object toSimple(Object pojo, boolean useCusstormBean,IdentityHashMap<Object, Object> dict, boolean supportReference) throws Exception{
		Class<?> fieldClass = pojo.getClass();
		if(isPrimitive(fieldClass)){
			return pojo;
		}
		else if(fieldClass.isArray()){
			if(isPrimitiveArray(fieldClass)){
				return pojo;
			}
			toSimpleCollectionByArray(pojo, useCusstormBean, dict, supportReference);
		}
		else if(Collection.class.isInstance(pojo)){
			return toSimpleCollection((Collection)pojo, useCusstormBean, dict, supportReference);
		}
		else if(Map.class.isInstance(pojo)){
			return toSimpleMap((Map)pojo, useCusstormBean, dict, supportReference);
		}
		else if(fieldClass.getCanonicalName().startsWith("java")){
			return pojo;
		}
		return toMap(pojo, useCusstormBean, dict, supportReference);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Map toSimpleMap(Map map, boolean useCusstormBean,IdentityHashMap<Object, Object> dict, boolean supportReference) throws Exception{
		if(dict!=null && dict.containsKey(map)){
			if(supportReference){
				return (Map)dict.get(map);
			}
			return null;
		}
		if(dict!=null)dict.put(map, map);
		if(map.isEmpty())return map;
		Iterator<Map.Entry<?, ?>> it = map.entrySet().iterator();
		HashMap<Object, Object> ret = new HashMap<Object, Object>();
		while(it.hasNext()){
			Map.Entry<?, ?> entry = it.next();
			ret.put(toSimple(entry.getKey(), useCusstormBean, dict, supportReference), 
					toSimple(entry.getValue(), useCusstormBean, dict, supportReference));
		}
		return ret;
	}
	@SuppressWarnings("rawtypes")
	private static Collection toSimpleCollectionByArray(Object arr, boolean useCusstormBean,IdentityHashMap<Object, Object> dict, boolean supportReference) throws Exception{
		if(dict!=null && dict.containsKey(arr)){
			if(supportReference){
				return (Collection)dict.get(arr);
			}
			return null;
		}
		if(dict!=null)dict.put(arr, arr);
		int size = Array.getLength(arr);
		ArrayList<Object> ret = new ArrayList<Object>(size);
		for(int i=0;i<size;i++){
			ret.add(toSimple(Array.get(arr, i), useCusstormBean, dict, supportReference));
		}
		return ret;
	}
	@SuppressWarnings("rawtypes")
	private static Collection toSimpleCollection(Collection list, boolean useCusstormBean,IdentityHashMap<Object, Object> dict, boolean supportReference) throws Exception{
		if(dict!=null && dict.containsKey(list)){
			if(supportReference){
				return (Collection)dict.get(list);
			}
			return null;
		}
		if(dict!=null)dict.put(list, list);
		if(list.isEmpty())return list;
		ArrayList<Object> ret = new ArrayList<Object>(list.size());
		Iterator<?> it = list.iterator();
		while(it.hasNext()){
			ret.add(toSimple(it.next(), useCusstormBean, dict, supportReference));
		}
		return ret;
	}
	
	public static <T> T fromObject(Object from, Class<T> clazz) throws Exception{
		return TypeUtils.castObject(from, clazz, supportCustomJavaBeanMethod);
	}
	
	/**
	 * 在map里找JxClass序列化的javabean类字段
	 * @param map
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Class<?> findJavaBeanClass(Map map){
		if(map.containsKey(CLASS_NAME)){
			try {
				return Class.forName(String.valueOf(map.get(CLASS_NAME)));
			} catch (ClassNotFoundException e) {
				//e.printStackTrace();
			}
		}
		return null;
	}
	/**
	 * 尝试转为java_bean，如果失败，返回map
	 * @param map
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object tryToJavaBean(Map map){
		if(map.containsKey(CLASS_NAME)){
			try {
				Class clazz = Class.forName(String.valueOf(map.get(CLASS_NAME)));
				map.remove(CLASS_NAME);
				if(map.containsKey(CLASS_STATIC) && map.size()==2){
					Object st = map.get(CLASS_STATIC);
					if(
							(st instanceof Boolean && ((Boolean)st)==true)||
							(st instanceof Number && ((Number)st).intValue()!=0)
					)
						return clazz;
				}
				return fromMap(map, clazz);
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
		return map;
	}
	@SuppressWarnings("rawtypes")
	public static <T> T fromMap(Map fieldMap, Class<T> clazz) throws Exception
	{
		return fromMap(fieldMap, clazz, supportCustomJavaBeanMethod);
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T fromMap(Map fieldMap, Class<T> clazz, boolean useCustomJavaBeanMethod) throws Exception
	{
//		T pojo = clazz.newInstance();
		Constructor<?> constructor = getDefaultConstructor(clazz);
		if(constructor==null)return null;
		constructor.setAccessible(true);
		T pojo = null;
		if(constructor.getParameterTypes().length==1){
			pojo = (T) constructor.newInstance(fieldMap);
		}else{
			pojo = (T) constructor.newInstance();
		}
		Set<Object> fieldNames = fieldMap.keySet();
		Map<String, JxAcc> accMap = JxAcc.getAccMap(clazz, useCustomJavaBeanMethod);
		
		Object fieldValue = null;
		JxAcc acc = null;
		for(Object fieldNameValue : fieldNames){
			String fieldName = fieldNameValue.toString();
			fieldValue = fieldMap.get(fieldName);
			acc = accMap.get(fieldName);
			if(acc!=null){
				try{
					setObjectField(pojo, fieldName, fieldValue, acc);
				}catch(NoSuchMethodException noerr){
					continue;
				}
			}
		}
		return pojo;
	}
	
    private static Constructor<?> getDefaultConstructor(Class<?> clazz) {
        if (Modifier.isAbstract(clazz.getModifiers())) {
            return null;
        }
        Constructor<?> defaultConstructor = null;
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.getParameterTypes().length == 0) {
                defaultConstructor = constructor;
                break;
            }
        }
        if (defaultConstructor == null) {
            if (clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers())) {
                for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                    if (constructor.getParameterTypes().length == 1
                        && constructor.getParameterTypes()[0].equals(clazz.getDeclaringClass())) {
                        defaultConstructor = constructor;
                        break;
                    }
                }
            }
        }
        return defaultConstructor;
    }
	
    private static Constructor<?> getCollectionConstructor(Class<?> clazz, Object fieldValue) {
        if (Modifier.isAbstract(clazz.getModifiers())) {
            return null;
        }
        Constructor<?> method = null;
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.getParameterTypes().length==1) {
            	Class<?> type = constructor.getParameterTypes()[0];
            	if(type.isAssignableFrom(fieldValue.getClass())){
            		method = constructor;
                    break;
            	}
            }
        }
        return method;
    }
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void setObjectField(Object pojo, String fieldName, Object fieldValue, JxAcc aac)
			throws Exception {
		Field field = aac.field;
		if(field!=null){
			Class<?> fieldType = field.getType();
			if (fieldType.isArray()) {
				if(fieldValue instanceof Collection){
					Collection collection = (Collection) fieldValue;
					fieldValue = collection.toArray();
					int arraySize = collection.size();
		
					Object typedArray = Array.newInstance(field.getType()
							.getComponentType(), arraySize);
					System.arraycopy(fieldValue, 0, typedArray, 0, arraySize);
					fieldValue = typedArray;
				}
				if(JxArray.isPrimitiveArray(field.getType())){
					fieldValue = JxArray.toPrimitiveArray(fieldValue);
				}
			} else if (fieldValue instanceof Collection && Collection.class.isAssignableFrom(fieldType)) {
				if(fieldType.isInterface()==false){
					Constructor<?> method = getCollectionConstructor(fieldType, fieldValue);
					fieldValue = method.newInstance(fieldValue);
				}else{
					Collection collection = (Collection)fieldValue;
					if(fieldType.isInstance(fieldValue)){
						fieldValue = fieldType.cast(fieldValue);
					}else if(Queue.class.isAssignableFrom(fieldType)){
						fieldValue = new ConcurrentLinkedQueue(collection);
					}else if(Set.class.isAssignableFrom(fieldType)){
						fieldValue = new HashSet(collection);
					}else if(List.class.isAssignableFrom(fieldType)){
						fieldValue = new ArrayList(collection);
					}else if(Deque.class.isAssignableFrom(fieldType)){
						fieldValue = new ArrayDeque(collection);
					}else if(Stack.class.isAssignableFrom(fieldType)){
						Stack st = new Stack();
						st.addAll(collection);
						fieldValue = st;
					}
				}
			}else if(fieldType==java.sql.Date.class){
				if(fieldValue!=null){
					fieldValue = new java.sql.Date(((java.util.Date)fieldValue).getTime());
				}
			}else if(field.getType().getCanonicalName()!=null && field.getType().getCanonicalName().startsWith("java")==false){
				
			}
		}
		if (aac.method==null){
			field.set(pojo, fieldValue);
		}else{
			try{
				writeValueFromSetter(aac, pojo, fieldValue);
			}catch (NoSuchMethodException e) {
//				System.out.println(aac.name+":"+fieldValue);
//				e.printStackTrace();
			}
		}
	}	
	private static void writeValueFromSetter(JxAcc aac, Object pojo,
			Object fieldValue) throws Exception {
		String setterName = "set" + capitalize(aac.name);
		Method method = null;
		if(aac.field!=null){
			method = pojo.getClass().getMethod(setterName,
					new Class[] { aac.field.getType() });
		}else{
			method = pojo.getClass().getMethod(setterName,
					new Class[] { aac.method.getReturnType() });
		}
		method.setAccessible(true);
		method.invoke(pojo, new Object[] { fieldValue });
	}
	
	private static String capitalize(String str) {
//		int strLen;
//		if ((str == null) || ((strLen = str.length()) == 0)) {
//			return str;
//		}
//		return strLen + Character.toTitleCase(str.charAt(0)) + str.substring(1);
		if(str==null || str.isEmpty()){
			return str;
		}
		return Character.toTitleCase(str.charAt(0)) + str.substring(1);
	}
	

	public static boolean isPrimitiveArray(Class<?> clazz){
		return clazz.isArray() && isPrimitive(clazz.getComponentType());
	}
	public static boolean isPrimitive(Class<?> clazz){
		return clazz.isPrimitive() || 
				clazz.equals(Boolean.class) ||
				clazz.equals(Byte.class) ||
				clazz.equals(Short.class) ||
				clazz.equals(Integer.class) ||
				clazz.equals(Long.class) ||
				clazz.equals(Float.class) ||
				clazz.equals(Double.class) ||
				clazz.equals(Character.class) ||
				clazz.equals(String.class)
				;
	}
	public static boolean isPrimitiveOrArray(Class<?> clazz){
		return isPrimitive(clazz) || isPrimitiveArray(clazz);
	}
}
