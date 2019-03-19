package generics;

public class GenericsTest {
	/**
	 * @call generics generics.GenericsTest.genericMethod(T) -> helper.Abstract.foo()
	 */
	public <T extends helper.Interface> void genericMethod(T t) {
		helper.Debug.debug("GenericsTest.genericMethod");
		t.foo();    
	}

	public void test() {
		helper.Debug.debug("\nGeneric class/method tests");

		Generic1<helper.Child1> g1 = new Generic1<helper.Child1>();
		g1.generic(new helper.Child1());

		Generic1<helper.Child2> g2 = new Generic1<helper.Child2>();
		g2.generic(new helper.Child2());

		Generic2<helper.Child2, Generic1<helper.Child2>> g3 = new Generic2<helper.Child2, Generic1<helper.Child2>>();
		g3.generic(new helper.Child2(), g2);

		new Generic3().foo();


		genericMethod(new helper.Child2());
	}
	public static void main(String args[]) {
		new GenericsTest().test();
	}
}
