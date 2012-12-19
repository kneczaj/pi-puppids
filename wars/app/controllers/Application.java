package controllers;

import java.util.List;

import models.Player;
import models.PlayerLocation;
import models.Team;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import services.api.AuthenticationService;
import views.html.index;
import views.html.profile;

import com.google.inject.Inject;

import daos.PlayerLocationDAO;

public class Application extends Controller {

	@Inject
	private static AuthenticationService authenticationService;
	
	@Inject
	private static PlayerLocationDAO playerLocationDAO;
	
	@SecureSocial.SecuredAction
	public static Result index() {
		Player player = authenticationService.getPlayer(ctx());
		
		Team team = player.getTeam();
		List<Player> teammates = team.getPlayers();
		PlayerLocation playerLocation = playerLocationDAO.findLatestLocation(player);
		
		return ok(index.render(player, playerLocation, teammates));
	}
	
	@SecureSocial.SecuredAction
	public static Result profile() {
		Player player = authenticationService.getPlayer(ctx());
		return ok(profile.render(player));
	}

}