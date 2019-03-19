package java8;

public class Java8Test {
	/**
	 * @call lambda java8.Lambda.compare(int, int) -> java8.Java8Test.lambda()_LambdaExpression
	 * @call simpleCall java8.Java8Test.lambda() -> java8.Lambda.compare(int, int)
	 */
	private void lambda() {
		helper.Debug.debug("Java8Test.lambda");
		Lambda l = (a1, a2) -> {helper.Debug.debug("Java8Test.lambda(int, int)"); return a1 > a2;};
		l.compare(2, 5);
	}

	void methodReference() {
		helper.Debug.debug("Java8Test.methodReference");
		MethodReference mf = new MethodReference();
		mf.doSomething();
	}

	/**
	 * @call methodReference java8.FunctionalInterface.doSomething(String) -> java8.MethodReference.print(String)
	 * @call simpleCall java8.Java8Test.functionalInterface() -> java8.FunctionalInterface.doSomething(String)
	 */
	void functionalInterface() {
		helper.Debug.debug("Java8Test.functionalInterface");
		FunctionalInterface fi = MethodReference::print;
		fi.doSomething("Hello");
	}
	
	/**
	 * @call methodReference java8.FunctionalInterface.doSomething(String) -> java8.MethodReference.print(String)
	 * @call simpleCall java8.Java8Test.functionalInterface2() -> java8.FunctionalInterface.doSomething(String)
	 */
	void functionalInterface2() {
		helper.Debug.debug("Java8Test.functionalInterface2");
		FunctionalInterface[] fi = {MethodReference::print, s->{new helper.Child1();System.out.println(new String(s).toLowerCase()); return "H";}};
		for (int i=0; i<fi.length;i++)
			fi[i].doSomething("Hello2");
	}

	public void test() {
		helper.Debug.debug("\nJava8 tests");
		lambda();
		methodReference();
		functionalInterface();
		functionalInterface2();
	}

	public static void main(String[] args) {
		new Java8Test().test();
	}
}
