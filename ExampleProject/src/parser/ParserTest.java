package parser;

public class ParserTest {

	/**
	 * @call parser parser.ParserTest.test() -> parser.ForParser.foo()
	 * @call parser parser.ParserTest.test() -> parser.ForParser.goo()
	 * @call parser parser.ParserTest.test() -> parser.ForParser.validMethod(Child2, Generic1<Child2>, Class<?>[])
	 * @call parser parser.ParserTest.test() -> parser.ForParser.validGenericMethod(Child2, Generic1<Child2>, Class<?>[])
	 */
	public void test() {
	    helper.Debug.debug("\nParser tests");
	    ForParser f = new ForParser();
	    \u0066.\u0066\u006F\u006F();
	    f.goo();
	    
	    generics.Generic1<helper.Child2> g2 = new generics.Generic1<helper.Child2>();
	    new ForParser().validMethod(new helper.Child2(), g2, Integer.class).generic(new helper.Child2(), g2);
	    new ForParser().validGenericMethod(new helper.Child2(), g2, Integer.class).generic(new helper.Child2(), g2);
	}
	
	public static void main(String[] args) {
		new ParserTest().test();
	}

}
