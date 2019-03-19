package initialization;

import helper.Debug;

public class InitializationTest {

	/**
	 * @call init initialization.InitializationTest.test() -> initialization.Init.Init(Interface)
	 * @call init initialization.InitializationTest.test() -> helper.Child1.Child1()
	 * @call init initialization.InitializationTest.test() -> initialization.Constructor.Constructor()
	 * @call init initialization.InitializationTest.test() -> initialization.StaticInit
	 */
    public void test() {
    	Debug.debug("\nInitialization tests");
    	StaticInit.staticInit();
    	new Init(new helper.Child1());
    	new Constructor();
    }
    
    public static void main(String[] args) {
		new InitializationTest().test();
	}

}
