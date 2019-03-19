package anonymAndInner;

public class Outer {

	public void inner() {
		helper.Debug.debug("anonymAndInner.Outer.foo()");
		new Inner().inner();
	}

	public class Inner {
		public void inner() {
			helper.Debug.debug("anonymAndInner.Outer.Inner.foo()");
		}
	}

	public static class Nested {
		public static void staticNested() {
			helper.Debug.debug("anonymAndInner.Outer.Nested.staticNested()");
		}

		public void nested() {
			helper.Debug.debug("anonymAndInner.Outer.Nested.nested()");
		}
	}

}
