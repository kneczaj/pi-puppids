package services.impl;

import java.util.Date;
import java.util.UUID;

import models.City;
import models.Faction;
import models.Invitation;
import models.Player;
import models.Team;

import org.bson.types.ObjectId;

import services.api.TeamService;
import services.api.error.TeamServiceException;

import com.google.inject.Inject;
import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;

import daos.CityDAO;
import daos.FactionDAO;
import daos.InvitationDAO;
import daos.PlayerDAO;
import daos.TeamDAO;
//import play.Logger;

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
	private InvitationDAO invitationDAO;
	
	@Inject
	private FactionDAO factionDAO;
	
	@Override
	public Team createTeam(City city, String name) {
		return createTeam(null, city, name);
	}
	
	@Override
	public Team createTeam(Faction faction, City city, String name) {
		Team team = new Team();
		team.setCity(city);
		team.setCreatedAt(new Date());
		team.setFaction(faction);
		team.setName(name);
		
		teamDAO.save(team);
		
		return team;
	}
	
	@Override
	public Invitation invite(Player sender, Player receiver) {
		
		Invitation invitation = new Invitation(sender, receiver);
		invitationDAO.save(invitation);
		return invitation;
	}

	@Override
	public Player acceptInvite(Invitation invitation) {
		
		Player player = invitation.getRecipient();
		player.setTeam(invitation.getTeam());
		playerDAO.save(player);
		
		invitationDAO.delete(invitation);
		
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
	public Player joinCity(Player player, String cityId) throws TeamServiceException {
		if (player == null || player.getTeam() == null || cityId == null) {
			throw new NullPointerException("Player and city and a player's team must not be null");
		}
		
		City newCity = cityDAO.get(new ObjectId(cityId));
		if (newCity == null) {
			throw new TeamServiceException("The Faction with this factionId could not be found.");
		}
		
		City oldCity = player.getTeam().getCity();
		if (oldCity != null) {
			// Player belongs already to this faction => is okay
			if (oldCity.getId().toString().equals(newCity.getId().toString())) {
				return player;
			}
			else {
				throw new TeamServiceException("Player already belongs to an other city.");
			}
		}
		
		teamDAO.updateCityOfTeam(player.getTeam(), newCity);
		Player p = playerDAO.get(player.getId());
		
		return p;
	}

	@Override
	public Player joinFaction(Player player, String factionId) throws TeamServiceException {
		if (player == null || player.getTeam() == null || factionId == null) {
			throw new NullPointerException("Player and factionId and a player's team must not be null");
		}
		
		Faction newFaction = factionDAO.get(new ObjectId(factionId));
		if (newFaction == null) {
			throw new TeamServiceException("The Faction with this factionId could not be found.");
		}
		
		Faction oldFaction = player.getTeam().getFaction();
		if (oldFaction != null) {
			// Player belongs already to this faction => is okay
			if (oldFaction.getName().equals(newFaction.getName())) {
				return player;
			}
			else {
				throw new TeamServiceException("Player already belongs to an other faction.");
			}
		}
		
		teamDAO.updateFactionOfTeam(player.getTeam(), newFaction);
		Player p = playerDAO.get(player.getId());
		
		return p;
	}
}
