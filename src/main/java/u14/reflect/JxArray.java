package u14.reflect;


final class JxArray {

	/**
	 * boolean[],byte[],int[],short[],long[],float[],double[]
	 * @param cls
	 * @return
	 */
	public static boolean isPrimitiveArray(Class<?> cls){
		return cls!=null && cls.isArray() && cls.getComponentType().isPrimitive();
	}
	public static Object toPrimitiveArray(Object arr) {
		if(arr==null){
			return arr;
		}
		Class<?> arrClass = arr.getClass();
		if(isPrimitiveArray(arrClass)){
			return arr;
		}
		if(Boolean[].class==arrClass){
			return toPrimitiveArray((Boolean[])arr);
		}
		if(Byte[].class==arrClass){
			return toPrimitiveArray((Byte[])arr);
		}
		if(Short[].class==arrClass){
			return toPrimitiveArray((Short[])arr);
		}
		if(Integer[].class==arrClass){
			return toPrimitiveArray((Integer[])arr);
		}
		if(Long[].class==arrClass){
			return toPrimitiveArray((Long[])arr);
		}
		if(Float[].class==arrClass){
			return toPrimitiveArray((Float[])arr);
		}
		if(Double[].class==arrClass){
			return toPrimitiveArray((Double[])arr);
		}
		if(Character[].class==arrClass){
			return toPrimitiveArray((Character[])arr);
		}
		return null;
	}
	public static Object toObjectArray(Object arr) {
		if(arr==null){
			return arr;
		}
		Class<?> arrClass = arr.getClass();
		if(arrClass.isArray() && arrClass.getComponentType().isPrimitive()==false){
			return arr;
		}
		if(boolean[].class==arrClass){
			return toObjectArray((boolean[])arr);
		}
		if(byte[].class==arrClass){
			return toObjectArray((byte[])arr);
		}
		if(short[].class==arrClass){
			return toObjectArray((short[])arr);
		}
		if(int[].class==arrClass){
			return toObjectArray((int[])arr);
		}
		if(long[].class==arrClass){
			return toObjectArray((long[])arr);
		}
		if(float[].class==arrClass){
			return toObjectArray((float[])arr);
		}
		if(double[].class==arrClass){
			return toObjectArray((double[])arr);
		}
		if(char[].class==arrClass){
			return toObjectArray((char[])arr);
		}
		return null;
	}
	public static boolean[] toPrimitiveArray(Boolean[] arr)
	{
		if(arr==null){
			return null;
		}
		boolean[] newArr = new boolean[arr.length];
		for(int i=0;i<arr.length;i++){
			newArr[i] = arr[i];
		}
		return newArr;
	}
	public static Boolean[] toObjectArray(boolean[] arr)
	{
		if(arr==null){
			return null;
		}
		Boolean[] newArr = new Boolean[arr.length];
		for(int i=0;i<arr.length;i++){
			newArr[i] = arr[i];
		}
		return newArr;
	}
	public static byte[] toPrimitiveArray(Byte[] arr)
	{
		if(arr==null){
			return null;
		}
		byte[] newArr = new byte[arr.length];
		for(int i=0;i<arr.length;i++){
			newArr[i] = arr[i];
		}
		return newArr;
	}	
	public static Byte[] toObjectArray(byte[] arr)
	{
		if(arr==null){
			return null;
		}
		Byte[] newArr = new Byte[arr.length];
		for(int i=0;i<arr.length;i++){
			newArr[i] = arr[i];
		}
		return newArr;
	}
	public static int[] toPrimitiveArray(Integer[] arr)
	{
		if(arr==null){
			return null;
		}
		int[] newArr = new int[arr.length];
		for(int i=0;i<arr.length;i++){
			newArr[i] = arr[i];
		}
		return newArr;
	}	
	public static Integer[] toObjectArray(int[] arr)
	{
		if(arr==null){
			return null;
		}
		Integer[] newArr = new Integer[arr.length];
		for(int i=0;i<arr.length;i++){
			newArr[i] = arr[i];
		}
		return newArr;
	}
	public static long[] toPrimitiveArray(Long[] arr)
	{
		if(arr==null){
			return null;
		}
		long[] newArr = new long[arr.length];
		for(int i=0;i<arr.length;i++){
			newArr[i] = arr[i];
		}
		return newArr;
	}	
	public static Long[] toObjectArray(long[] arr)
	{
		if(arr==null){
			return null;
		}
		Long[] newArr = new Long[arr.length];
		for(int i=0;i<arr.length;i++){
			newArr[i] = arr[i];
		}
		return newArr;
	}
	public static float[] toPrimitiveArray(Float[] arr)
	{
		if(arr==null){
			return null;
		}
		float[] newArr = new float[arr.length];
		for(int i=0;i<arr.length;i++){
			newArr[i] = arr[i];
		}
		return newArr;
	}
	public static Float[] toObjectArray(float[] arr)
	{
		if(arr==null){
			return null;
		}
		Float[] newArr = new Float[arr.length];
		for(int i=0;i<arr.length;i++){
			newArr[i] = arr[i];
		}
		return newArr;
	}
	public static double[] toPrimitiveArray(Double[] arr)
	{
		if(arr==null){
			return null;
		}
		double[] newArr = new double[arr.length];
		for(int i=0;i<arr.length;i++){
			newArr[i] = arr[i];
		}
		return newArr;
	}
	public static Double[] toObjectArray(double[] arr)
	{
		if(arr==null){
			return null;
		}
		Double[] newArr = new Double[arr.length];
		for(int i=0;i<arr.length;i++){
			newArr[i] = arr[i];
		}
		return newArr;
	}
	public static char[] toPrimitiveArray(Character[] arr)
	{
		if(arr==null){
			return null;
		}
		char[] newArr = new char[arr.length];
		for(int i=0;i<arr.length;i++){
			newArr[i] = arr[i];
		}
		return newArr;
	}
	public static Character[] toObjectArray(char[] arr)
	{
		if(arr==null){
			return null;
		}
		Character[] newArr = new Character[arr.length];
		for(int i=0;i<arr.length;i++){
			newArr[i] = arr[i];
		}
		return newArr;
	}
}
