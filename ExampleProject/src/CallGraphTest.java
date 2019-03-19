
public class CallGraphTest {

	public static void main(String[] args) {
		new parser.ParserTest().test();
		new anonymAndInner.AnonymAndInnerTest().test();
		new initialization.InitializationTest().test();
		new java8.Java8Test().test();
		new generics.GenericsTest().test();
		new polimorph.PolimorphTest().test();
		new dynamic.DynamicTest().test();
	}

}
