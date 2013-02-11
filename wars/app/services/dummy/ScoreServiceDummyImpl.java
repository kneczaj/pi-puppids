package services.dummy;

import java.util.Random;

import models.Faction;
import models.Player;
import models.Team;
import services.api.ScoreService;
import services.api.error.ScoreServiceException;

/**
 * Dummy implementation of ScoreService for testing purposes
 * 
 * @author michi
 * 
 */
public class ScoreServiceDummyImpl implements ScoreService {

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

}
