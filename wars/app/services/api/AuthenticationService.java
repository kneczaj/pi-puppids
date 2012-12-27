package services.api;

import models.Player;
import play.mvc.Http;
import securesocial.core.java.SocialUser;

/**
 * AuthenticationService
 * @author kamil
 *
 */
public interface AuthenticationService extends Service {

	/**
	 * Returns currently logged user from Http.Context
	 * @param ctx
	 * @return SocialUser
	 */
	public SocialUser getUser(Http.Context ctx);

	/**
	 * Returns currently logged player from Http.Context
	 * @param ctx
	 * @return Player
	 */
	public Player getPlayer(Http.Context ctx);
	
}
