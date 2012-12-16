package services.api;

import java.util.List;

import models.Player;
import models.Team;

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
	public Team createTeam(Team team);

	/**
	 * List the members of a team
	 * 
	 * @param team
	 * @return
	 */
	public List<Player> getMembers(Team team);

	/**
	 * Invite a player to join an other team.
	 * 
	 * @param player
	 * @param team
	 */
	public void invite(Player player, Team team);
	
	/**
	 * A Player may accept an invite to a team
	 * 
	 * @param player 
	 * @param team the team he was invited to
	 * @return the refreshed player entity
	 */
	public Player acceptInvite(Player player, Team team);

//	/**
//	 * Invite strangers (people that are not already registered at ARWars) via
//	 * emailAdress to a team. Sends an email to the adress with an invitation
//	 * link.
//	 * 
//	 * @param emailAdress
//	 * @param team
//	 */
//	public void iniviteStranger(String emailAdress, Team team);

}
