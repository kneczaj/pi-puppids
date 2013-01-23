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
	public Integer getPlayerScore(Player player) throws ScoreServiceException {
		Random rnd = new Random();
		return rnd.nextInt(1024);
	}

	@Override
	public Integer getTeamScore(Team team) throws ScoreServiceException {
		Random rnd = new Random();
		return (rnd.nextInt(8192)+4096);
	}

	@Override
	public Integer getFactionScore(Faction faction)
			throws ScoreServiceException {
		Random rnd = new Random();
		return (rnd.nextInt(100000)+40096);
	}

}
