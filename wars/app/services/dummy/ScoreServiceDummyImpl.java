package services.dummy;

import java.util.List;
import java.util.Random;

import models.Faction;
import models.Player;
import models.Team;
import services.api.ScoreService;
import services.api.error.ScoreServiceException;

import com.google.inject.Inject;

import daos.PlayerDAO;
import daos.TeamDAO;

/**
 * Dummy implementation of ScoreService for testing purposes
 * 
 * @author michi
 * 
 */
public class ScoreServiceDummyImpl implements ScoreService {
	
	@Inject
	private PlayerDAO playerDAO;
	
	@Inject
	private TeamDAO teamDAO;

	@Override
	public Integer calculatePlayerScore(Player player) throws ScoreServiceException {
		Random rnd = new Random();
		return rnd.nextInt(1024);
	}

	@Override
	public Integer calculateTeamScore(Team team) throws ScoreServiceException {
		Random rnd = new Random();
		return (rnd.nextInt(8192)+4096);
	}

	@Override
	public Integer calculateFactionScore(Faction faction)
			throws ScoreServiceException {
		Random rnd = new Random();
		return (rnd.nextInt(100000)+40096);
	}

	@Override
	public Integer calculateScoreForAllTeams() throws ScoreServiceException {
		return 10 * calculateTeamScore(new Team());
	}

	@Override
	public Integer calculateScoreForAllFactions() throws ScoreServiceException {
		return 2 * calculateFactionScore(new Faction());
	}
	
	@Override
	public List<Player> getTopPlayers(int limit) {
		return playerDAO.findTopKScorers(limit);
	}

	@Override
	public List<Team> getTopTeams(int limit) {
		return teamDAO.findTopKScorers(limit);
	}

	@Override
	public long getPlayerRank(Player p) {
		return playerDAO.getRankOfPlayer(p);
	}

	@Override
	public long getTeamRank(Team t) {
		return teamDAO.getRankOfTeam(t);
	}

}
