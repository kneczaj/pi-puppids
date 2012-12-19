package controllers;

import java.util.List;

import models.Player;
import models.PlayerLocation;
import models.Team;

import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;

import com.google.inject.Inject;

import daos.PlayerLocationDAO;

import views.html.index;
import views.html.profile;

import services.api.ProfileService;

public class Application extends Controller {
	
	@Inject
	private static PlayerLocationDAO playerLocationDAO;
	
	@Inject
	private static ProfileService profileService;
	
	@SecureSocial.SecuredAction
	public static Result index() {
		Player player = profileService.loggedPlayer(ctx());
		
		Team team = player.getTeam();
		List<Player> teammates = team.getPlayers();
		
		PlayerLocation playerLocation = playerLocationDAO.findLatestLocation(player);
		
		return ok(index.render(player, playerLocation, teammates));
	}
	
	@SecureSocial.SecuredAction
	public static Result profile() {
		Player player = profileService.loggedPlayer(ctx());
		return ok(profile.render(player));
	}

}