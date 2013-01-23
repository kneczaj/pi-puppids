package services.impl;

import java.util.List;

import models.Faction;
import models.Place;
import models.Player;
import models.ResourceType;
import models.Team;
import models.Unit;
import services.api.ScoreService;
import services.api.error.ScoreServiceException;
import assets.constants.ScoreValues;

import com.google.code.morphia.query.Query;
import com.google.inject.Inject;

import daos.FactionDAO;
import daos.PlayerDAO;
import daos.TeamDAO;

/**
 * Implementation of the ScoreService
 * @author michi
 *
 */
public class ScoreServiceImpl implements ScoreService {

	@Inject
	private PlayerDAO playerDAO;
	
	@Inject
	private TeamDAO teamDAO;
	
	@Inject
	private FactionDAO factionDAO;
	
	@Override
	public Integer getPlayerScore(Player player) throws ScoreServiceException {
		
		Player load = playerDAO.findOne("id", player.getId());
		if (load == null)
			throw new ScoreServiceException("Player with id " + player.getId() + " not found.");
		
		Integer score = 0;
		
		//Count places
		for (Place place : load.getConquered()) {
			if (place.getResource().equals(ResourceType.Special))
				score += ScoreValues.SPECIAL_PLACE_SCORE_VALUE;
			else
				score += ScoreValues.PLACE_SCORE_VALUE;
		}
		
		//Count units
		for (Unit unit : load.getUnits()) {
			Integer unitScore = 0;
			for (Integer value : unit.getCosts().values()) {
				unitScore += value;
			}
			score += unitScore;
		}
		
		score += load.getResourceDepot(ResourceType.Credits);
		score += load.getResourceDepot(ResourceType.Material);
		
		load.setScore(score);
		playerDAO.save(load);
		
		return score;
	}

	@Override
	public Integer getTeamScore(Team team) throws ScoreServiceException {
		
		Team load = teamDAO.findOne("id", team.getId());
		if (load == null)
			throw new ScoreServiceException("Team with id " + team.getId() + " not found.");
		
		Integer score = 0;
		
		for (Player player : load.getPlayers()) {
			score += this.getPlayerScore(player);
		}
		
		load.setScore(score);
		teamDAO.save(load);
		
		return score;
	}
	
	@Override
	public Integer getFactionScore(Faction faction) throws ScoreServiceException {
		Faction load = factionDAO.findOne("id", faction.getId());
		if (load == null)
			throw new ScoreServiceException("Faction with id " + faction.getId() + " not found.");
		
		Integer score = 0;
		
		Query<Team> teamQuery = teamDAO.createQuery();
		teamQuery.and(teamQuery.criteria("faction").equal(load));
		
		List<Team> teams = teamDAO.find(teamQuery).asList();
		
		for (Team team : teams) {
			score += getTeamScore(team);
		}
		
		return score;
	}

}
