package services.impl;

import java.util.List;

import models.Player;
import models.Team;
import services.api.TeamService;

import com.google.inject.Inject;

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
	public Team createTeam(Team t) {
		teamDAO.save(t);
		return teamDAO.get(t.getId());
	}

	@Override
	public List<Player> getMembers(Team team) {
		// TODO Auto-generated method stub
		return null;
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
