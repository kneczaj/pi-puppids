package services.api.error;

/**
 * Wraps the Exceptions thrown by the ResourceService
 * 
 * @author markus
 */
public class ResourceServiceException extends Exception {

	private static final long serialVersionUID = 7128171101767269688L;

	public ResourceServiceException(String message) {
		super(message);
	}
	
}
