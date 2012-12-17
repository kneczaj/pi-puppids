package services.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import models.Player;
import models.Team;
import models.City;
import models.Faction;
import models.Invitation;

import services.api.TeamService;

import com.google.inject.Inject;
import com.google.code.morphia.query.Query;
import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;

import daos.PlayerDAO;
import daos.TeamDAO;
import daos.InvitationDAO;

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
		Team t = new Team();
		t.setCity(city);
		t.setCreatedAt(new Date());
		t.setFaction(faction);
		t.setName(name);
		
		return teamDAO.get(t.getId());
	}

	@Override
	public List<Player> getMembers(Team team) {
		
		Query<Player> membersQ = playerDAO.createQuery().field("team").equal(team);
		List<Player> members = playerDAO.find(membersQ).asList();
		
		return members;
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
		
		MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
		
		mail.setSubject("ARWars - invitation to team " + invitation.getTeam().getName());
		mail.addRecipient(invitation.getRecipient().getEmail());
		mail.addFrom("Peter Hausel <noreply@email.com>");
		mail.send( "Invitation" );
		
		invitation.setSent();
		invitationDAO.save(invitation);
	}
}
