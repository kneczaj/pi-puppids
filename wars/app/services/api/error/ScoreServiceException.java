package services.api.error;

/**
 * Wraps errors in the ScoreService
 * @author michi
 *
 */
public class ScoreServiceException extends Exception {

	private static final long serialVersionUID = -9043251256613354070L;

	public ScoreServiceException(String message) {
		super(message);
	}
}
