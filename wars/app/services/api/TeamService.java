package services.api;

import models.City;
import models.Faction;
import models.Invitation;
import models.Player;
import models.Team;
import services.api.error.TeamServiceException;

/**
 * Manage Teams
 * 
 * @author markus
 */
public interface TeamService {

	/**
	 * Create a team
	 * 
	 * @param player
	 * @return
	 */
	public Team createTeam(Faction faction, City city, String name);

	/**
	 * Create a team
	 * 
	 * @param city
	 * @param name
	 * @return
	 */
	public Team createTeam(City city, String name);

	/**
	 * Invite a player to join an other team.
	 * 
	 * @param sender
	 *            player who invites to their team
	 * @param receiver
	 *            player invited
	 */
	public Invitation invite(Player sender, Player receiver);

	/**
	 * A Player may accept an invite to a team
	 * 
	 * @param player
	 * @param team
	 *            the team he was invited to
	 * @return the refreshed player entity
	 */
	public Player acceptInvite(Invitation invitation);

	/**
	 * Invite strangers (people that are not already registered at ARWars) via
	 * emailAdress to a team. Sends an email to the adress with an invitation
	 * link.
	 * 
	 * @param sender
	 * @param emailAdress
	 */
	public Invitation iniviteStranger(Player sender, String emailAddress);

	public void sendInvitation(Invitation invitation);

	/**
	 * Allows a player to join a faction.
	 * 
	 * @param player
	 * @param factionId
	 * @throws TeamServiceException
	 *             if the Player already belongs to an other Faction or the factionId
	 *             is not valid
	 * @throws NullPointerException
	 *             if Player, factionId or Player's team is null
	 */
	public Player joinFaction(Player player, String factionId)
			throws TeamServiceException;

	/**
	 * Allows a player to join a city.
	 * 
	 * @param player
	 * @param cityId
	 * @throws TeamServiceException
	 *             if the Player already belongs to an other City or the cityId
	 *             is not valid
	 * @throws NullPointerException
	 *             if Player, cityId or Player's team is null
	 */
	public Player joinCity(Player player, String cityId) throws TeamServiceException;

}
