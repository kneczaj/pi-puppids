package services.api;

import models.Faction;
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
	public Integer calculatePlayerScore(Player player) throws ScoreServiceException;
	
	/**
	 * Calculates the score for a team
	 * @param team
	 * @return
	 * @throws ScoreServiceException
	 */
	public Integer calculateTeamScore(Team team) throws ScoreServiceException;

	public Integer calculateScoreForAllTeams() throws ScoreServiceException;
	
	/**
	 * Calculates the score for a faction
	 * @param faction
	 * @return
	 * @throws ScoreServiceException
	 */
	public Integer calculateFactionScore(Faction faction) throws ScoreServiceException;
	
	public Integer calculateScoreForAllFactions() throws ScoreServiceException;
	
}
