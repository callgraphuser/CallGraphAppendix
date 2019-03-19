package anonymAndInner;

public class AnonymAndInnerTest {
	/**
	 * @call anonym anonymAndInner.AnonymAndInnerTest.innerClass() -> anonymAndInner.Outer()
	 * @call anonym anonymAndInner.AnonymAndInnerTest.innerClass() -> anonymAndInner.Outer.inner()
	 */
	void innerClass() {
		helper.Debug.debug("anonymAndInner.AnonymAndInnerTest.innerClass");

		Outer outer = new Outer();
		outer.inner();
	}

	/**
	 * @call anonym anonymAndInner.AnonymAndInnerTest.anonim() -> anonymAndInner.AnonymAndInnerTest.anonim().new Child1() {...}.foo() 
	 * @call anonym anonymAndInner.AnonymAndInnerTest.anonim() -> helper.Child1.goo()
	 * @call simpleCall anonymAndInner.AnonymAndInnerTest.anonim() -> helper.Child1.foo()
	 */
	void anonim() {
		helper.Debug.debug("anonymAndInner.AnonymAndInnerTest.anonim");

		helper.Child1 c1 = new helper.Child1() {
			public void foo() {
				helper.Debug.debug("Child1_anonim.foo()");
			}
		};
		c1.foo();
		c1.goo();

		generics.Generic1<helper.Child1> g2 = new generics.Generic1<helper.Child1>() {
			/**
			 * @call anonym  anonymAndInner.AnonymAndInnerTest.anonim().new Generic1() {...}.generic(Child1) -> anonymAndInner.AnonymAndInnerTest.anonim().new Child1() {...}.foo() 
			 */
			public void generic(helper.Child1 c) {
				helper.Debug.debug("Generic<Child1>_anonim.generic(Child1)");
				c.foo();
			}
		};
		g2.generic(c1);
	}

	/**
	 * @call anonym anonymAndInner.AnonymAndInnerTest.nested() -> anonymAndInner.Outer.Nested.nested()
	 * @call anonym anonymAndInner.AnonymAndInnerTest.nested() -> anonymAndInner.Outer.Nested.staticNested()
	 */
	void nested() {
		helper.Debug.debug("anonymAndInner.AnonymAndInnerTest.nested");

		new Outer.Nested().nested();
		Outer.Nested.staticNested();
	}

	public void test() {
		innerClass();
		anonim();
		nested();
	}
	
	public static void main(String[] args) {
		new AnonymAndInnerTest().test();

	}

}
