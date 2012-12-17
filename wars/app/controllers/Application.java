package controllers;

import java.util.List;

import models.Player;
import models.PlayerLocation;
import models.Team;

import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import securesocial.core.java.SocialUser;

import com.google.inject.Inject;

import daos.PlayerDAO;
import daos.PlayerLocationDAO;
import daos.TeamDAO;

import views.html.index;
import views.html.profile;

import services.api.TeamService;

public class Application extends Controller {

	@Inject
	private static PlayerDAO playerDAO;
	
	@Inject
	private static PlayerLocationDAO playerLocationDAO;
	
	@Inject
	private static TeamDAO teamDAO;
	
	@Inject
	private static TeamService teamService;
	
	@SecureSocial.SecuredAction
	public static Result index() {
		SocialUser user = (SocialUser) ctx().args.get(SecureSocial.USER_KEY);
		Player player = playerDAO.findOne("email", user.getEmail());
		
		Team team = player.getTeam();
		List<Player> teammates = teamService.getMembers(team);
		
		PlayerLocation playerLocation = playerLocationDAO.findLatestLocation(player);
		
		return ok(index.render(player, playerLocation, teammates));
	}
	
	@SecureSocial.SecuredAction
	public static Result profile() {
		SocialUser user = (SocialUser) ctx().args.get(SecureSocial.USER_KEY);
		Player player = playerDAO.findOne("email", user.getEmail());
		return ok(profile.render(player));
	}

}