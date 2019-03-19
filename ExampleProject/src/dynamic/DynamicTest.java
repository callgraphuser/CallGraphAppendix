package dynamic;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DynamicTest {
	/**
	 * @call simpleCall dynamic.DynamicTest.reflection() -> java.lang.reflect.Method.invoke(Object, Object[])
	 * @call reflection java.lang.reflect.Method.invoke(Object, Object[]) -> java8.MethodReference.privatePrint()
	 */
	void reflection() {
		try {
			helper.Debug.debug("dynamic.reflection");
			Method method = Class.forName("java8.MethodReference").getMethod("privatePrint");
			method.setAccessible(true);

			method.invoke(null);

		} catch (IllegalAccessException | IllegalArgumentException 
				| ClassNotFoundException | NoSuchMethodException
				| InvocationTargetException e) 
		{
			System.err.println("Some error occured during the reflection test");
		}
	} 


	/** 
	 * @call simpleCall dynamic.DynamicTest.methodHandle() -> java.lang.invoke.MethodHandle.MethodHandle.invokeWithArguments(Object[]) 
	 * @call methodHandle java.lang.invoke.MethodHandle.MethodHandle.invokeWithArguments(Object[]) -> java8.MethodReference.print()
	 */
	void methodHandle() {
		try {
			helper.Debug.debug("dynamic.methodHandle");
			MethodType type = MethodType.methodType(String.class, String.class);
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			MethodHandle mh;

			mh = lookup.findStatic(java8.MethodReference.class, "print", type);

			mh.invokeWithArguments("Hello");
		} catch (Throwable e) {
			System.err.println("Some error occured during the method handle " );
			e.printStackTrace();
		}
	}
	public void test() {
		methodHandle();
		reflection();
	}
	public static void main(String[] args) {
		new DynamicTest().test();

	}

}
