package services.api;

import java.util.List;

import models.City;
import models.Faction;
import models.Invitation;
import models.Player;
import models.Team;
import models.notifications.ShoppingListMessage;
import services.api.error.TeamServiceException;

/**
 * Manage Teams
 * 
 * @author markus
 */
public interface TeamService extends Service {

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
	 * Join a player to a team.
	 * 
	 * @param player
	 *            player who joins
	 * @param team
	 *            team player joins to
	 */
	public Player joinTeam(Player player, Team team);

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
	 * @param token
	 * @param loggedPlayer
	 * @returns 
	 * 		logged player instance after accepting the invitaiton  
	 * @throws TeamServiceException
	 * 		with problems encountered in the exception message
	 */
	public ShoppingListMessage acceptInvitation(Invitation invitation, Player loggedPlayer) 
			throws TeamServiceException;
	
	/**
	 * Finds invitation with given token in the db
	 * 
	 * @param token
	 * @return
	 * @throws TeamServiceException
	 * 		if invitation was not found
	 */
	public Invitation getInvitation(String token) throws TeamServiceException;

	/**
	 * Invite strangers (people that are not already registered at ARWars) via
	 * emailAdress to a team. Sends an email to the adress with an invitation
	 * link.
	 * 
	 * @param sender
	 * @param emailAdress
	 */
	public Invitation iniviteStranger(Player sender, String emailAddress);
	
	/**
	 * makes invitation from given data
	 * 
	 * @param invitedUserOrEmail
	 * 		a string of a user name of an email to invite
	 * @param loggedPlayer
	 * @return Invitation
	 * 		the invitation - already stored in the db
	 * 
	 * @throws TeamServiceException
	 * 		with problems encountered in the exception message 
	 * 
	 */
	public Invitation prepareInvitation(String invitedUserOrEmail, Player loggedPlayer) 
			throws TeamServiceException;

	/**
	 * Sends invitation email
	 * 
	 * @param invitation
	 */
	public void sendInvitation(Invitation invitation);

	/**
	 * Creates an initial team for a new player
	 * 
	 * @return
	 */
	public Team createInitialTeam(Player player);
	
	/**
	 * returns invitation from db sent to the given player
	 * 
	 * @param player
	 * @return
	 */
	public List<Invitation> getPlayerInvitations(Player player);
	
	/**
	 * Rejects all invitations sent to given player
	 * 
	 * @param player
	 */
//	public void rejectAllInvitations(Player player);
	
	/**
	 * Removes a player from a team
	 * 
	 * @param team
	 * @param player
	 * @throws exception when given player doesn't belong to given team
	 */
	public void removePlayer(Team team, Player player) throws TeamServiceException;
	
	/**
	 * Changes team data
	 * 
	 * @param team
	 * @param teamname
	 * @throws exception when data doesn't pass the validation
	 */
	public void changeTeamData(Team team, String teamname) throws TeamServiceException;
	
	public boolean checkFaction(Invitation invitation, Player player);
	
	public boolean checkCity(Invitation invitation, Player player);
	
	public void sendInvitationAcceptanceNotifications(Player loggedPlayer, Invitation invitation);

}
