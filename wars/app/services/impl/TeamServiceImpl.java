package services.impl;

import java.util.UUID;

import models.City;
import models.Faction;
import models.Invitation;
import models.Player;
import models.Team;
import services.api.TeamService;

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
		Team team = new Team(name);
		team.setCity(city);
		team.setFaction(faction);
		
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
	public Player joinTeam(Player player, Team team) {
		
		Team oldTeam = player.getTeam();
		if (oldTeam != null) {
			
			oldTeam.removePlayer(player);
			if (oldTeam.getPlayers().isEmpty())
				teamDAO.delete(oldTeam);
			else
				teamDAO.save(oldTeam);
		}
		
		player.setTeam(team);
		playerDAO.save(player);
		
		team.addPlayer(player);
		teamDAO.save(team);
		
		return player;
	}

	@Override
	public Player acceptInvite(Invitation invitation) {
		
		Player player = invitation.getRecipient();
		Team team = invitation.getTeam();
		
		player = joinTeam(player, team);
		
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
	public Team createInitialTeam(Player player){
		Team team = new Team(player.getUsername());
		teamDAO.save(team);
		
		return team;
	}
}
