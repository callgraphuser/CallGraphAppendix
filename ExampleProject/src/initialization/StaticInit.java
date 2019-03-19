package initialization;

public class StaticInit {
	static helper.Interface i1, i2;

	static {
		helper.Debug.debug("initialization.StaticInit.{}");
		i1 = new IntermediateInitialization().instantiateChild1();
	}
	static {
		i2 = new helper.Child2();
	}

	static void staticInit() {
		helper.Debug.debug("initialization.StaticInit.staticInit()");
	}
}
