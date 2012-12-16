package services.impl;

import java.util.Date;
import java.util.List;

import models.Player;
import models.Team;
import models.City;
import models.Faction;

import services.api.TeamService;

import com.google.inject.Inject;
import com.google.code.morphia.query.*;

import daos.PlayerDAO;
import daos.TeamDAO;

/**
 * Implementation of a TeamService
 * 
 * @author markus
 */
public class TeamServiceImpl implements TeamService {
	
	@Inject
	private TeamDAO teamDAO;
	
	@Inject
	private PlayerDAO playerDAO;
	
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
	public void invite(Player player, Team team) {
		// TODO Auto-generated method stub

	}

	@Override
	public Player acceptInvite(Player player, Team team) {
		// TODO Auto-generated method stub
		return null;
	}

}
