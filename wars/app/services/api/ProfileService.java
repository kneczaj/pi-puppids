package services.api;

import securesocial.core.java.SocialUser;

import models.Player;

import play.mvc.Http;


/**
 * ProfileService
 * @author kamil
 *
 */
public interface ProfileService extends Service {

	// some problems with service registration
	
	/**
	 * Returns currently logged user from Http.Context
	 * @param ctx
	 * @return SocialUser
	 */
	public SocialUser loggedUser(Http.Context ctx);

	/**
	 * Returns currently logged player from Http.Context
	 * @param ctx
	 * @return Player
	 */
	public Player loggedPlayer(Http.Context ctx);
	
}
