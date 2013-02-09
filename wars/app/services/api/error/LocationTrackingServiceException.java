package services.api.error;

/**
 * Wraps exceptions thrown by the LocationTrackingService
 * 
 * @author markus
 */
public class LocationTrackingServiceException extends Exception {

	private static final long serialVersionUID = 4976759391139283522L;
	
	public LocationTrackingServiceException(String msg) {
		super(msg);
	}

}
