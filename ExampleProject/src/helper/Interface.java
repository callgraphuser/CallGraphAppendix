package helper;

public interface Interface {
	  void foo();
	  void goo();
	  default void hoo() {
	    Debug.debug("Interface.hoo()");
	  }
}
