package services.impl;

import java.util.Date;
import java.util.UUID;

import models.City;
import models.Faction;
import models.Invitation;
import models.Player;
import models.Team;
import play.Logger;
import services.api.TeamService;

import com.google.inject.Inject;
import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;

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
	private TeamDAO teamDAO;
	
	@Inject
	private PlayerDAO playerDAO;
	
	@Inject
	private InvitationDAO invitationDAO;
	
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
	
	public void sendInvitation(Invitation invitation) {
		
		String token = UUID.randomUUID().toString();
		invitation.setToken(token);
		
		String link = "http://localhost:9000/acceptInvitation/" + token;
		
		Logger.info("to accept invitation go to " + link);
		
		MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
		
		mail.setSubject("ARWars - invitation to team " + invitation.getTeam().getName());
		mail.addRecipient(invitation.getRecipient().getEmail());
		mail.addFrom("arwars.game@gmail.com");
		mail.send( "Invitation " + link);
		
		invitation.setSent();
		invitationDAO.save(invitation);
	}
}
