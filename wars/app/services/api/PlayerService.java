package services.api;

import models.Player;
import securesocial.core.java.SocialUser;
import services.api.error.PlayerServiceException;

/**
 * PlayerService: register new users, join a faction, join a city
 * 
 * @author markus
 */
public interface PlayerService {
	
	/**
	 * Register a new Player in the system
	 * 
	 * @param authenticationProvider
	 * @param email
	 * @param firstname
	 * @param lastname
	 * @param passwordHash
	 * @param username
	 * @return the registered player
	 * @throws PlayerServiceException if the username is already taken
	 */
	public Player register(String authenticationProvider, String email,
			String firstname, String lastname, String passwordHash,
			String username) throws PlayerServiceException;
	
	/**
	 * Register a new Player in the system with help of a SocialUser object.
	 * 
	 * @param socialUser
	 * @return the registered player
	 * @throws PlayerServiceException if the username is already taken
	 */
	public Player register(SocialUser socialUser) throws PlayerServiceException;
	
	/**
	 * Allows a player to join a faction.
	 * 
	 * @param player
	 * @param factionId
	 * @throws PlayerServiceException
	 *             if the Player already belongs to an other Faction or the factionId
	 *             is not valid
	 * @throws NullPointerException
	 *             if Player, factionId or Player's team is null
	 */
	public Player joinFaction(Player player, String factionId)
			throws PlayerServiceException;

	/**
	 * Allows a player to join a city.
	 * 
	 * @param player
	 * @param cityId
	 * @throws PlayerServiceException
	 *             if the Player already belongs to an other City or the cityId
	 *             is not valid
	 * @throws NullPointerException
	 *             if Player, cityId or Player's team is null
	 */
	public Player joinCity(Player player, String cityId) throws PlayerServiceException;

}
