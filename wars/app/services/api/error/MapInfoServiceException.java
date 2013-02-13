package services.api.error;

/**
 * Wraps Exceptions thrown by the MapInfoService
 * 
 * @author markus
 */
public class MapInfoServiceException extends Exception {

	private static final long serialVersionUID = 6450686696560407146L;
	
	public MapInfoServiceException(String msg) {
		super(msg);
	}

}
