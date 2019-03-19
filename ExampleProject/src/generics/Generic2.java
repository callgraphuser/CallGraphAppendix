package generics;

public class Generic2<T extends helper.Interface, K extends Generic1<T>> {
	/**
	 * @call generics generics.Generic2(T,K).generic() -> helper.Abstract.foo()
	 */
	public void generic(T t, K k) {
		helper.Debug.debug("generics.Generic2.generic(T, K)");
		t.foo();
		k.generic(t);
	}
}
