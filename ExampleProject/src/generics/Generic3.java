package generics;

public class Generic3 extends Generic1<helper.Child1> {
	/**
	 * @call generics generics.Generic3(T,K).foo() -> generics.Generic1(T,K).generic()
	 */
	public void foo() {
		helper.Debug.debug("generics.Generic3.foo()");
		generic(new helper.Child1());
	}
}
