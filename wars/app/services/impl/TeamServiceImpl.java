package services.impl;

import java.util.List;
import java.util.UUID;

import models.City;
import models.Faction;
import models.Invitation;
import models.Player;
import models.Team;

import services.api.TeamService;
import services.api.error.TeamServiceException;

import com.google.code.morphia.query.Query;
import com.google.inject.Inject;
import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;

import daos.CityDAO;
import daos.FactionDAO;
import daos.InvitationDAO;
import daos.PlayerDAO;
import daos.TeamDAO;

/**
 * Implementation of a TeamService
 * 
 * @author kamil
 */
public class TeamServiceImpl implements TeamService {
	
	@Inject
	private static TeamDAO teamDAO;
	
	@Inject
	private static PlayerDAO playerDAO;
	
	@Inject
	private static CityDAO cityDAO;
	
	@Inject
	private static InvitationDAO invitationDAO;
	
	@Inject
	private static FactionDAO factionDAO;
	
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
	public Invitation prepareInvitation(String invitedUserOrEmail, Player loggedPlayer) 
			throws TeamServiceException {
		
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
			
		} else if (invitedPlayer.getTeam().getId().equals(loggedPlayer.getTeam().getId()))
			
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
	public Player acceptInvitation(Invitation invitation, Player loggedPlayer) 
			throws TeamServiceException {
		
		Player recipient = invitation.getRecipient();
		if (recipient == null)
			recipient = playerDAO.findOne("email", invitation.getEmail());
		
		// invited person does not exist in db - create a new account
		if (recipient == null) 
			throw new TeamServiceException("playerNotRegistered");
		
		// invited player is not currently logged in
		if ((loggedPlayer == null) || (!loggedPlayer.getUsername().equals(recipient.getUsername()) ) )
			throw new TeamServiceException("playerNotLogged");
		
		// current Faction or City doesn't match
		if (!validateInvitation(invitation, loggedPlayer))
			throw new TeamServiceException("validationFailed");
		
		Player player = joinTeam(recipient, invitation.getTeam());
		
		invitationDAO.delete(invitation);
		
		return player;
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
		if (oldTeam != null) {
			
			oldTeam.removePlayer(player);
			if (oldTeam.getPlayers().isEmpty())
				teamDAO.delete(oldTeam);
			else {
				oldTeam.refindTeamMaster();
				teamDAO.save(oldTeam);
			}
		}
		
		player.setTeam(team);
		playerDAO.save(player);
		
		team.addPlayer(player);
		teamDAO.save(team);
		
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
		
		MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
		
		mail.setSubject("ARWars - invitation to team " + invitation.getTeam().getName());
		mail.addRecipient(invitation.getEmail());
		mail.addFrom("arwars.game@gmail.com");
		
		String name;
		if (recipient == null)
			name = "New Player";
		else
			name = recipient.getFirstname() + " " + recipient.getName();
		
		mail.send(
			"Dear "+ name + "<br><br>" +
			"Player " + sender.getUsername() + " has invited you to their team " + invitation.getTeam().getName() + ". " +
			"Click <a href=\"" + link + "\">here</a> to confirm." + "<br><br>" +
			"Enjoy!"
		);
		
		invitation.setSent();
		invitationDAO.save(invitation);
	}

	@Override
	public Team createInitialTeam(Player player){
		Team team = new Team(player.getUsername());
		teamDAO.save(team);
		
		return team;
	}
	
	public List<Invitation> getPlayerInvitations(Player player) {
		
		Query<Invitation> q = invitationDAO.createQuery();
		q.or(
		        q.criteria("email").equal(player.getEmail()),
		        q.criteria("recipient").equal(player)
		);
		
		return invitationDAO.find(q).asList();
	}
	
//	public void rejectAllInvitations(Player player) {
//		
//		Query<Invitation> q = invitationDAO.createQuery();
//		q.or(
//		        q.criteria("email").equal(player.getEmail()),
//		        q.criteria("recipient").equal(player)
//		);
//		
//		invitationDAO.deleteByQuery(q);
//	}
	
	public Boolean validateInvitation(Invitation invitation, Player player)  {
		
		Team pTeam = player.getTeam();
		if (pTeam == null)
			return true;
		Team iTeam = invitation.getTeam();
		if ((pTeam.getFaction() == iTeam.getFaction() || pTeam.getFaction() == null) 
				&& (pTeam.getCity() == iTeam.getCity() || pTeam.getCity() == null))
			return true;
		
		return false;
	}
}
