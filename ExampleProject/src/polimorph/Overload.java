package polimorph;

public class Overload {
	void foo() {
		helper.Debug.debug("polimorph.Overload.foo()");
	}
	void foo(int i) {
		helper.Debug.debug("polimorph.Overload.foo(int)");
	}
	<T> void foo(T t) {
		helper.Debug.debug("polimorph.Overload.foo(T)");
	}
	void foo(int i, Object... objects ) {
		helper.Debug.debug("polimorph.Overload.foo(int, Object...)");
	}

	/**
	 * @call overload polimorph.Overload.overload1() -> polimorph.Overload.foo()
	 */
	void overload1() {
		helper.Debug.debug("polimorph.Overload.overload1");
		foo();
	}

	/**
	 * @call overload polimorph.Overload.overload2() -> polimorph.Overload.foo(int)
	 */
	void overload2() {
		helper.Debug.debug("polimorph.Overload.overload2");
		foo(3);
	}

	/**
	 * @call overload polimorph.Overload.overload3() -> polimorph.Overload.foo(Object)
	 */
	void overload3() {
		helper.Debug.debug("polimorph.Overload.overload3");
		foo(this);
	}

	/**
	 * @call overload polimorph.Overload.overload4() -> polimorph.Overload.foo(int, Object[])
	 */
	void overload4() {
		helper.Debug.debug("polimorph.Overload.overload4");
		foo(3, this);
	}
}
