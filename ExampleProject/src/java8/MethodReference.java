package java8;

import java.util.ArrayList;
import java.util.List;

public class MethodReference {
	List<String> list = new ArrayList<>();

	public static String print(String str) {
		helper.Debug.debug("java8.MethodReference.print()");
		return str;
	}

	public static void privatePrint() {
		helper.Debug.debug("java8.MethodReference.privatePrint()");
	}

	public MethodReference() {
		list.add("first");
		list.add("second");
	}

	/**
	 * @call methodReference java8.MethodReference.doSomething -> Iterable<String>.forEach(Consumer<? super String
	 * @call simpleCall Iterable<String>.forEach(Consumer<? super String>) -> java8.MethodReference.print
	 */
	public void doSomething() {
		list.forEach(MethodReference::print);
	} 
}
