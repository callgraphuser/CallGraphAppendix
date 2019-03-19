package generics;

public class Generic1<T extends helper.Interface> {
	/**
	 * @call generics generics.Generic1(T,K).generic() -> helper.Abstract.foo()
	 * @call generics generics.Generic1(T,K).generic() -> helper.Child1.foo()
	 */
	public void generic(T t) {
		helper.Debug.debug("genericsGeneric1.generic(T)");
		t.foo();
	}
}
