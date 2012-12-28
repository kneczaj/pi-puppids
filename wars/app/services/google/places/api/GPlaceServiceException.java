package services.google.places.api;

public class GPlaceServiceException extends Exception {

	private static final long serialVersionUID = -3677677653275554698L;

	public GPlaceServiceException(String msg) {
		super(msg);
	}
	
	public GPlaceServiceException(String msg, Throwable t) {
		super(msg, t);
	}
	
}
