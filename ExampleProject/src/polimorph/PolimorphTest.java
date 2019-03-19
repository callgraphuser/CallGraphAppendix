package polimorph;

public class PolimorphTest {
	void overload() {
		helper.Debug.debug("polimorf.PolimorfTest.overload");

		Overload o = new Overload();
		o.overload1();
		o.overload2();
		o.overload3();
		o.overload4();
	}

	void polimorphic() {
		helper.Debug.debug("polimorph.polimorphic");
		Polimorph p = new Polimorph();
		p.interfaceImplementation();
		p.interfaceImplementation2();
		p.abstractClassImplementation();
		p.overloadedInterface(new helper.Child3());
	}

	public void test() {
		helper.Debug.debug("\nTests for overriding and overloding");
		overload();
		polimorphic();
	}
	public static void main(String[] args) {
		new PolimorphTest().test();

	}

}
