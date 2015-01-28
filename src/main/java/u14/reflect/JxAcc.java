package u14.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * javaBean类 属性、getter、setter节点
 * @author zhangheng
 */
final public class JxAcc {

	public final String name;
	public final boolean declared;
	public final Field field;
	public final Method method;

	public JxAcc(Field f, boolean declared) {
		this.field = f;
		this.method = null;
		this.name = f.getName();
		this.declared = declared;
		f.setAccessible(true);
	}

	public JxAcc(String name, Method method, Field f) {
		this.field = f;
		this.method = method;
		this.name = name;
		if (f != null) {
			f.setAccessible(true);
			this.declared = true;
		} else {
			this.declared = false;
		}
		method.setAccessible(true);
	}

	public Object get(Object pojo) throws Exception {
		if (method != null) {
			return method.invoke(pojo, new Object[0]);
		}
		return field.get(pojo);
	}

	private static Map<Class<?>, JxAcc[]> cacheAccList = new HashMap<Class<?>, JxAcc[]>();
	private static Map<Class<?>, Map<String, JxAcc>> cacheAccMap = new HashMap<Class<?>, Map<String, JxAcc>>();

	public static void clearCache() {
		cacheAccList.clear();
		cacheAccMap.clear();
	}
	/**
	 * 获取acc以acc_name为key的map方式
	 * @param clazz
	 * @param includeBeanMethod
	 * @return
	 */
	public static Map<String, JxAcc> getAccMap(Class<?> clazz, boolean includeBeanMethod) {
		if (JxClass.cacheJavaClass && cacheAccMap.containsKey(clazz)) {
			return cacheAccMap.get(clazz);
		}
		JxAcc[] list = getAccList(clazz, includeBeanMethod);
		Map<String, JxAcc> map = new HashMap<String, JxAcc>(list.length);
		for (JxAcc f : list) {
			if (map.containsKey(f.name) == false || f.declared == false)
				map.put(f.name, f);
		}
		if (JxClass.cacheJavaClass) {
			cacheAccMap.put(clazz, map);
		}
		return map;
	}
	/**
	 * 获取acc列表
	 * @param clazz
	 * @param includeBeanMethod
	 * @return
	 */
	public static JxAcc[] getAccList(Class<?> clazz, boolean includeBeanMethod) {
		if (JxClass.cacheJavaClass && cacheAccList.containsKey(clazz)) {
			return cacheAccList.get(clazz);
		}
		List<JxAcc> list = new ArrayList<JxAcc>();
		if (includeBeanMethod) {
			deepGetAllMethod(list, clazz);
		}
		deepGetAllField(list, clazz);
		JxAcc[] arr = list.toArray(new JxAcc[list.size()]);
		if (JxClass.cacheJavaClass) {
			cacheAccList.put(clazz, arr);
		}
		return arr;
	}
	
	private static void deepGetAllMethod(List<JxAcc> list, Class<?> clazz){
		for (Method method : clazz.getMethods()) {
			String methodName = method.getName();
			int mod = method.getModifiers();
			if (Modifier.isStatic(mod) || Modifier.isAbstract(mod)
					|| Modifier.isTransient(mod) 
					|| Modifier.isPrivate(mod)
					|| Modifier.isProtected(mod)) {
				continue;
			}

			if (method.getReturnType().equals(Void.TYPE)
					|| method.getParameterTypes().length != 0
					|| method.getReturnType() == ClassLoader.class) {
				continue;
			}
			if (method.isAnnotationPresent(JxOmitMethod.class)) {
				continue;
			}
			// System.out.println("method:"+methodName);
			if (methodName.startsWith("get")
					&& methodName.length() > 3
					&& !(methodName.length() == 8 && methodName
							.equals("getClass"))
					&& Character.isUpperCase(methodName.charAt(3))) {
				// System.out.println("v:"+methodName);
				// String propertyName =
				// Introspector.decapitalize(methodName.substring(3));
				String propertyName = Character.toLowerCase(methodName
						.charAt(3)) + methodName.substring(4);
				// try {
				// Field filed = clazz.getDeclaredField(propertyName);
				// list.add(new JxAcc(propertyName, method, filed));
				// } catch (NoSuchFieldException e) {
				// list.add(new JxAcc(propertyName, method, null));
				// }
				list.add(new JxAcc(propertyName, method, null));
			} else if (methodName.startsWith("is")
					&& methodName.length() > 2
					&& Character.isUpperCase(methodName.charAt(2))) {
				// String propertyName =
				// Introspector.decapitalize(methodName.substring(2));
				String propertyName = Character.toLowerCase(methodName
						.charAt(2)) + methodName.substring(3);
				list.add(new JxAcc(propertyName, method, null));
			}
		}
		if(clazz.getSuperclass()!=null && clazz.getSuperclass()!=java.lang.Object.class){
			deepGetAllMethod(list, clazz.getSuperclass());
		}
	}
	
	private static void deepGetAllField(List<JxAcc> list, Class<?> clazz){
		Field[] fs = clazz.getDeclaredFields();
		for (Field f : fs) {
//			 System.out.println("field:"+f.getName());
			int mod = f.getModifiers();
			if (
					Modifier.isStatic(mod)
					|| Modifier.isAbstract(mod)
					|| Modifier.isTransient(mod)
					//|| Modifier.isPrivate(mod)
					//|| Modifier.isProtected(mod)
				) {
				continue;
			}
			if (f.isAnnotationPresent(JxOmitField.class)) {
				continue;
			}
			if(Modifier.isPublic(mod)==false){
				f.setAccessible(true);
			}
			list.add(new JxAcc(f, false));
		}
//		System.out.println(clazz+" @ "+clazz.getSuperclass());
		if(clazz.getSuperclass()!=null && clazz.getSuperclass()!=java.lang.Object.class){
			deepGetAllField(list, clazz.getSuperclass());
		}
	}
}
