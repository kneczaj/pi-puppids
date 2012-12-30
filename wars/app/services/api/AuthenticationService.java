package services.api;

import models.Player;

/**
 * AuthenticationService
 * @author kamil
 *
 */
public interface AuthenticationService extends Service {

	/**
	 * Returns currently logged player from Http.Context
	 * @param ctx
	 * @return Player
	 */
	public Player getPlayer();
}