package parser;

public class ForParser {
	String s1 = "void notMethod6() {}";
	String s2 = \u0022 void notMethod7() {} \u0022;

	\u0076\u006F\u0069\u0064 \u0066\u006F\u006F() { // void foo()
		helper.Debug.debug("\u0076\u006F\u0069\u0064 ForParser.\u0066\u006F\u006F()");
	}

	\u0076\u006F\u0069\u0064 goo() {
		helper.Debug.debug("\u0076\u006F\u0069\u0064 ForParser.goo()");
	}


	generics.Generic2<helper.Child2, generics.Generic1<helper.Child2>> validMethod(helper.Child2 c, generics.Generic1<helper.Child2> g, Class<?>... objects) {
		helper.Debug.debug("Generic2<Child2, Generic1<Child2>> ForParser.validMethod(Child2, Generic1<Child2>, Class<?>... )");

		generics.Generic2<helper.Child2, generics.Generic1<helper.Child2>> local = new generics.Generic2<helper.Child2, generics.Generic1<helper.Child2>>();
		local.generic(c,  g);
		return local;
	}

	<T, K extends helper.Child2> generics.Generic2<helper.Child2, generics.Generic1<helper.Child2>> validGenericMethod(K c, generics.Generic1<K> g, Class<?>... objects) {
		helper.Debug.debug("<T, K extends Child2> Generic2<Child2, Generic1<Child2>> ForParser.validGenericMethod(K, Generic1<K>, Class<?>...)");

		generics.Generic2<helper.Child2, generics.Generic1<helper.Child2>> local = new generics.Generic2<helper.Child2, generics.Generic1<helper.Child2>>();
		local.generic(c,  new generics.Generic1<helper.Child2>());
		return local;
	}
}
