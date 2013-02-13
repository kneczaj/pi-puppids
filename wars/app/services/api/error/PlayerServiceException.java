package services.api.error;

/**
 * Wraps exceptions of the PlayerService
 * 
 * @author markus
 */
public class PlayerServiceException extends Exception {

	private static final long serialVersionUID = 6452971609052151668L;

	public PlayerServiceException(String message) {
		super(message);
	}
	
}
