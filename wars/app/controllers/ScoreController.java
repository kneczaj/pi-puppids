package controllers;

import java.util.List;

import models.Player;
import models.Team;

import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import services.api.AuthenticationService;
import services.api.ScoreService;

import com.google.inject.Inject;

public class ScoreController extends Controller {
	
	@Inject
	private static AuthenticationService authenticationService;
	
	@Inject
	private static ScoreService scoreService;
	
	@SecureSocial.SecuredAction(ajaxCall=true)
	public static Result getTopTeamsAndPlayers() {
		Player currentPlayer = authenticationService.getPlayer();
		if (currentPlayer == null) {
			return ok();
		}
		
		List<Player> players = scoreService.getTopPlayers(5);
		List<Team> teams = scoreService.getTopTeams(5);
		
		ObjectNode playerEntries = Json.newObject();
		ObjectNode teamEntries = Json.newObject();
		boolean containsCurrentPlayer = false;
		boolean containsCurrentTeam = false;
		String teamName = currentPlayer.getTeam().getName();
		
		for (Player p : players) {
			if (p.getUsername().equals(currentPlayer.getUsername())) {
				containsCurrentPlayer = true;
			}
			
			ObjectNode entry = Json.newObject();
			entry.put("score", p.getScore());
			playerEntries.put(p.getUsername(), entry);
		}
		
		for (Team t : teams) {
			if (t.getName().equals(teamName)) {
				containsCurrentTeam = true;
			}
			
			ObjectNode entry = Json.newObject();
			entry.put("score", t.getScore());
			teamEntries.put(t.getName(), entry);
		}
		
		ObjectNode json = Json.newObject();
		json.put("players", playerEntries);
		json.put("teams", teamEntries);
		
		if (!containsCurrentPlayer) {
			json.put("ownScore", currentPlayer.getScore());
			json.put("ownRank", scoreService.getPlayerRank(currentPlayer));
		}
		
		if (!containsCurrentTeam) {
			json.put("ownTeamScore", currentPlayer.getTeam().getScore());
			json.put("ownTeamRank", scoreService.getTeamRank(currentPlayer.getTeam()));
		}
		
		return ok(json.toString());
	}

}
