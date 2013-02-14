package services.impl;

import java.util.List;
import java.util.UUID;

import models.City;
import models.Faction;
import models.Invitation;
import models.Place;
import models.Player;
import models.Team;
import models.notifications.ShoppingListMessage;
import services.api.TeamService;
import services.api.WebSocketCommunicationService;
import services.api.error.TeamServiceException;

import com.google.code.morphia.query.Query;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;

import daos.CityDAO;
import daos.FactionDAO;
import daos.InvitationDAO;
import daos.PlaceDAO;
import daos.PlayerDAO;
import daos.TeamDAO;

/**
 * Implementation of a TeamService
 * 
 * @author kamil
 */
public class TeamServiceImpl implements TeamService {

	@Inject
	private TeamDAO teamDAO;

	@Inject
	private PlayerDAO playerDAO;

	@Inject
	private CityDAO cityDAO;
	
	@Inject
	private PlaceDAO placeDAO;

	@Inject
	private InvitationDAO invitationDAO;

	@Inject
	private FactionDAO factionDAO;
	
	@Inject
	private WebSocketCommunicationService webSocketCommunicationService;

	@Override
	public Team createTeam(City city, String name) {
		return createTeam(null, city, name);
	}

	@Override
	public Team createTeam(Faction faction, City city, String name) {
		Team team = new Team(name);
		team.setCity(city);
		team.setFaction(faction);

		teamDAO.save(team);

		return team;
	}

	@Override
	public Invitation prepareInvitation(String invitedUserOrEmail,
			Player loggedPlayer) throws TeamServiceException {

		if (invitedUserOrEmail.equals(loggedPlayer.getEmail())
				|| invitedUserOrEmail.equals(loggedPlayer.getUsername()))
			throw new TeamServiceException("userIsYou");

		Player invitedPlayer;
		Boolean isEmail = invitedUserOrEmail.contains("@");
		if (isEmail)
			invitedPlayer = playerDAO.findOne("email", invitedUserOrEmail);
		else
			invitedPlayer = playerDAO.findOne("username", invitedUserOrEmail);

		Invitation invitation;
		if (invitedPlayer == null) {

			if (!isEmail)
				throw new TeamServiceException("givenUserDoesntExist");
			invitation = new Invitation(loggedPlayer, invitedUserOrEmail);

		} else if (invitedPlayer.getTeam().getId()
				.equals(loggedPlayer.getTeam().getId()))

			throw new TeamServiceException("alreadyInTheTeam");

		else {
			invitation = new Invitation(loggedPlayer, invitedPlayer);
		}

		invitationDAO.save(invitation);
		return invitation;
	}

	@Override
	public Invitation getInvitation(String token) throws TeamServiceException {
		Invitation invitation = invitationDAO.findOne("token", token);
		if (invitation == null)
			throw new TeamServiceException("invitationNotFound");
		return invitation;
	}

	@Override
	public ShoppingListMessage acceptInvitation(Invitation invitation, Player loggedPlayer)
			throws TeamServiceException {

		Player recipient = invitation.getRecipient();
		if (recipient == null)
			recipient = playerDAO.findOne("email", invitation.getEmail());

		// invited person does not exist in db - create a new account
		if (recipient == null)
			throw new TeamServiceException("playerNotRegistered");

		// invited player is not currently logged in
		if ((loggedPlayer == null)
				|| (!loggedPlayer.getUsername().equals(recipient.getUsername())))
			throw new TeamServiceException("playerNotLogged");

		ShoppingListMessage factionCityChangeListMessage = new ShoppingListMessage();
		boolean cityNeedsChange = !checkCity(invitation, loggedPlayer);
		boolean factionNeedsChange = !checkFaction(invitation, loggedPlayer);
		factionCityChangeListMessage.addFactionCityChange(factionNeedsChange, cityNeedsChange, invitation.getToken());
		
		// current Faction or City doesn't match
		if (!factionCityChangeListMessage.isEmpty())
			return factionCityChangeListMessage;

		joinTeam(recipient, invitation.getTeam());

		invitationDAO.delete(invitation);

		return factionCityChangeListMessage;
	}
	
	public void sendInvitationAcceptanceNotifications(Player loggedPlayer, Invitation invitation) {
		webSocketCommunicationService.sendSimpleNotification("Team changed", 
				"You have successfully joined \"" + invitation.getTeam().getName() + "\" team", "info", loggedPlayer);
		
		List<Player> teammates = invitation.getTeam().getPlayers();
		teammates.remove(loggedPlayer);
		
		webSocketCommunicationService.sendSimpleNotification("New member", 
				"User " + loggedPlayer.getUsername() + " joined your team", "info", teammates);
	}

	@Override
	public Invitation invite(Player sender, Player receiver) {

		Invitation invitation = new Invitation(sender, receiver);
		invitationDAO.save(invitation);
		return invitation;
	}

	@Override
	public Player joinTeam(Player player, Team team) {

		Team oldTeam = player.getTeam();
		
		player.setTeam(team);
		playerDAO.save(player);

		team.addPlayer(player);
		teamDAO.save(team);
		
		if (oldTeam != null) {

			oldTeam.removePlayer(player);
			
			// delete player from conquered places
			for (Place place: player.getConquered()) {
				place.removePlayerFromConquerors(player);
				placeDAO.save(place);
			}
			
			// delete conquered places from the player
			List<Place> emptyPlaceList = Lists.newLinkedList();
			player.setConquered(emptyPlaceList);
			
			if (oldTeam.getPlayers().isEmpty()) {
				Query<Invitation> query = invitationDAO.createQuery().field("team").equal(oldTeam);
				invitationDAO.deleteByQuery(query);
				
				// Deleting the team breaks the system
				//teamDAO.delete(oldTeam);
			} else {
				oldTeam.refindTeamMaster();
				teamDAO.save(oldTeam);
			}
		}

		return player;
	}

	@Override
	public Invitation iniviteStranger(Player sender, String emailAddress) {
		Invitation invitation = new Invitation(sender, emailAddress);
		invitationDAO.save(invitation);
		return invitation;
	}

	@Override
	public void sendInvitation(Invitation invitation) {

		String token = UUID.randomUUID().toString();
		invitation.setToken(token);

		String link = "http://localhost:9000/acceptInvitation/" + token;
		Player recipient = invitation.getRecipient();
		Player sender = invitation.getSender();

		MailerAPI mail = play.Play.application().plugin(MailerPlugin.class)
				.email();

		mail.setSubject("ARWars - invitation to team "
				+ invitation.getTeam().getName());
		mail.addRecipient(invitation.getEmail());
		mail.addFrom("arwars.game@gmail.com");

		String name;
		if (recipient == null)
			name = "New Player";
		else
			name = recipient.getFirstname() + " " + recipient.getName();

		mail.send("Dear " + name + "<br><br>" + "Player "
				+ sender.getUsername() + " has invited you to their team "
				+ invitation.getTeam().getName() + ". " + "Click <a href=\""
				+ link + "\">here</a> to confirm." 
				+ "<br><br>" 
				+ "Enjoy!");

		invitation.setSent();
		invitationDAO.save(invitation);
	}

	@Override
	public Team createInitialTeam(Player player) {
		Team team = new Team(player.getUsername() + "'s team");
		teamDAO.save(team);

		return team;
	}

	public List<Invitation> getPlayerInvitations(Player player) {

		Query<Invitation> q = invitationDAO.createQuery();
		q.or(q.criteria("email").equal(player.getEmail()),
				q.criteria("recipient").equal(player));

		return invitationDAO.find(q).asList();
	}

	// public void rejectAllInvitations(Player player) {
	//
	// Query<Invitation> q = invitationDAO.createQuery();
	// q.or(
	// q.criteria("email").equal(player.getEmail()),
	// q.criteria("recipient").equal(player)
	// );
	//
	// invitationDAO.deleteByQuery(q);
	// }
	
	public boolean checkFaction(Invitation invitation, Player player) {
		Team pTeam = player.getTeam();
		if (pTeam == null)
			return true;
		
		if (pTeam.getFaction() == null)
			return true;
		
		return pTeam.getFaction().equals(invitation.getTeam().getFaction());
	}
	
	public boolean checkCity(Invitation invitation, Player player) {
		Team pTeam = player.getTeam();
		if (pTeam == null)
			return true;
		
		if (pTeam.getCity() == null)
			return true;
		
		return pTeam.getCity().equals(invitation.getTeam().getCity());
	}
	
	public void removePlayer(Team team, Player player) throws TeamServiceException {
		
		if (!team.isMember(player))
			throw new TeamServiceException(
					"Cannot delete - this player doesn't belong to this team");
		
		Team newTeam = createInitialTeam(player);
		newTeam.setCity(team.getCity());
		newTeam.setFaction(team.getFaction());
		
		joinTeam(player, newTeam);
		webSocketCommunicationService.sendSimpleNotification(
				"Team changed", "You were deleted from team " + team.getName(), "info", player);
	}
	
	public void changeTeamData(Team team, String teamname) throws TeamServiceException {
		
		if (teamname.length() == 0) {
			throw new TeamServiceException("Team name cannot be empty");
		}
		
		team.setName(teamname);
		teamDAO.save(team);
	}
}
