package initialization;

public class Init {
	  helper.Interface i1, i2 = new IntermediateInitialization().instantiateChild1(), i3, i4;

	  {
	    helper.Debug.debug("initialization.Init.{}");
	    i1 = new helper.Child1();
	  }
	  {
	    i3 = new helper.Child2();
	  }
	  
	  public Init(helper.Interface i) {
	    helper.Debug.debug("initialization.Init.Init(Interface)");
	    i4 = i; 
	  }
}
