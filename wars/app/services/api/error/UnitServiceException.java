package services.api.error;

/**
 * Wraps the Exceptions thrown by the UnitService
 * 
 * @author michi
 */
public class UnitServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6929255688114093547L;

	public UnitServiceException(String message) {
		super(message);
	}
	
}
