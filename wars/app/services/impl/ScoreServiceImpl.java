package services.impl;

import models.Place;
import models.Player;
import models.ResourceType;
import models.Team;
import services.api.ScoreService;
import services.api.error.ScoreServiceException;
import assets.constants.ScoreValues;

import com.google.inject.Inject;

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
	
	@Override
	public Integer getPlayerScore(Player player) throws ScoreServiceException {
		
		Player load = playerDAO.findOne("id", player.getId());
		Integer score = 0;
		
		for (Place place : load.getConquered()) {
			if (place.getResource().equals(ResourceType.Special))
				score += place.getAmount();
			else
				score += ScoreValues.PLACE_SCORE_VALUE;
		}
		
		load.setScore(score);
		playerDAO.save(load);
		
		return score;
	}

	@Override
	public Integer getTeamScore(Team team) throws ScoreServiceException {
		
		Team load = teamDAO.findOne("id", team.getId());
		Integer score = 0;
		
		for (Player player : load.getPlayers()) {
			score += this.getPlayerScore(player);
		}
		
		load.setScore(score);
		teamDAO.save(load);
		
		return score;
	}

}
