package initialization;

public class Constructor {
	private void privateMethod () {
		
	} 
	
	protected void callPrivate() {
	  privateMethod();	
	}
	
	Constructor() {
	    helper.Debug.debug("initialization.Constructor.Constructor()");
	}
}
