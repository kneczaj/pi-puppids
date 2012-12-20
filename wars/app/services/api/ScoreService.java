package services.api;

import models.Player;
import models.Team;
import services.api.error.ScoreServiceException;

/**
 * ScoreService calculates scores for players or teams
 * @author michi
 *
 */
public interface ScoreService extends Service {
	
	/**
	 * Calculates the score for a player
	 * @param player
	 * @return
	 * @throws ScoreServiceException
	 */
	public Integer getPlayerScore(Player player) throws ScoreServiceException;
	
	/**
	 * Calculates the score for a team
	 * @param team
	 * @return
	 * @throws ScoreServiceException
	 */
	public Integer getTeamScore(Team team) throws ScoreServiceException;
	
}
