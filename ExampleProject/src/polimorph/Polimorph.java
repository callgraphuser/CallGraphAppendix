package polimorph;

public class Polimorph {
	/**
	 * @call polimorph polimorph.Polimorph.overloadedInterface() -> helper.Abstract.foo()
	 * @call polimorph polimorph.Polimorph.overloadedInterface() -> helper.Child2.goo()
	 * @call polimorph polimorph.Polimorph.overloadedInterface() -> helper.Child3.hoo()
	 * @call simpleCall polimorph.Polimorph.overloadedInterface() -> helper.Child2.hoo()
	 * @call simpleCall polimorph.Polimorph.overloadedInterface() -> helper.Interface.hoo()
	 * @call simpleCall polimorph.Polimorph.overloadedInterface() -> helper.Interface.goo()
	 */
	public void overloadedInterface(helper.Interface interf) {
		helper.Debug.debug("Helper.interfaceTest(Interface)");
		interf.foo();
		interf.goo();
		interf.hoo();
	}
	
	/**
	 * @call polimorph polimorph.Polimorph.overloadedInterface() -> helper.Abstract.foo()
	 * @call polimorph polimorph.Polimorph.overloadedInterface() -> helper.Child2.goo()
	 * @call polimorph polimorph.Polimorph.overloadedInterface() -> helper.Child3.hoo()
	 * @call simpleCall polimorph.Polimorph.overloadedInterface() -> helper.Child2.hoo()
	 * @call simpleCall polimorph.Polimorph.overloadedInterface() -> helper.Interface.hoo()
	 * @call simpleCall polimorph.Polimorph.overloadedInterface() -> helper.Interface.goo()
	 */
	public void overloadedInterface() {
		helper.Debug.debug("Helper.interfaceTest(Interface)");
		helper.Child3 c = new helper.Child3();
		c.foo();
		c.goo();
		c.hoo();
	}
	
	/**
	 * @call polimorph polimorph.Polimorph.overloadedInterface() -> helper.Abstract.foo()
	 * @call polimorph polimorph.Polimorph.overloadedInterface() -> helper.Child2.goo()
	 * @call polimorph polimorph.Polimorph.overloadedInterface() -> helper.Child2.hoo()
	 * @call simpleCall polimorph.Polimorph.overloadedInterface() -> helper.Interface.hoo()
	 * @call simpleCall polimorph.Polimorph.overloadedInterface() -> helper.Interface.goo()
	 */
	public void overloadedInterface2() {
		helper.Debug.debug("Helper.interfaceTest(Interface)");
		helper.Child2 c = new helper.Child2();
		c.foo();
		c.goo();
		c.hoo();
	}	
	
	/**
	 * @call polimorph polimorph.Polimorph.interfaceImplementation() -> helper.Interface.hoo()
	 * @call polimorph polimorph.Polimorph.interfaceImplementation() -> helper.Child1.goo()
	 * @call polimorph polimorph.Polimorph.interfaceImplementation() -> helper.Child1.foo()
	 * @call simpleCall polimorph.Polimorph.interfaceImplementation() -> helper.Interface.foo()
	 * @call simpleCall polimorph.Polimorph.interfaceImplementation() ->  helper.Interface.goo()
	 * @call simpleCall polimorph.Polimorph.interfaceImplementation() ->  helper.Abstract.foo()
	 */
	public void interfaceImplementation() {
	    helper.Interface i1 = new helper.Child1();
	    i1.foo();
	    i1.goo();
	    i1.hoo();
	}
	
	/**
	 * @call polimorph polimorph.Polimorph.interfaceImplementation2() -> helper.Abstract.foo()
	 * @call polimorph polimorph.Polimorph.interfaceImplementation2() -> helper.Child2.goo()
	 * @call polimorph polimorph.Polimorph.interfaceImplementation2() -> helper.Child2.hoo()
	 * @call simpleCall polimorph.Polimorph.interfaceImplementation2() -> helper.Interface.foo()
	 * @call simpleCall polimorph.Polimorph.interfaceImplementation2() ->  helper.Interface.goo()
	 * @call simpleCall polimorph.Polimorph.interfaceImplementation2() ->  helper.Interface.hoo()
	 */
	public void interfaceImplementation2() {
		helper.Interface i2 = new helper.Child2();
	    i2.foo();
	    i2.hoo();
	}
	
	/**
	 * @call simpleCall polimorph.Polimorph.abstractClassImplementation() -> helper.Abstract.foo()
	 * @call polimorph polimorph.Polimorph.abstractClassImplementation() -> helper.Child1.foo()
	 */
	public void abstractClassImplementation() {
		helper.Abstract a1 = new helper.Child1();
	    a1.foo();
	    new helper.Child1().foo();
	}
}
