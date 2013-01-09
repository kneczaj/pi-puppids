package controllers;

import java.util.List;

import models.City;
import models.Faction;
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

import daos.CityDAO;
import daos.FactionDAO;
import daos.PlayerLocationDAO;

public class Application extends Controller {

	@Inject
	private static AuthenticationService authenticationService;
	
	@Inject
	private static PlayerLocationDAO playerLocationDAO;
	
	@Inject
	private static FactionDAO factionDAO;
	
	@Inject
	private static CityDAO cityDAO;
	
	@SecureSocial.SecuredAction
	public static Result index() {
		Player player = authenticationService.getPlayer();
		
		Team team = player.getTeam();
		List<Player> teammates = team.getPlayers();
		List<Faction> factions = factionDAO.find().asList();
		List<City> cities = cityDAO.find().asList();
		
		PlayerLocation playerLocation = playerLocationDAO.findLatestLocation(player);
		
		Boolean teamMaster = player.getId().equals(team.getTeamMaster().getId());
		
		return ok(index.render(player, playerLocation, teammates, factions, cities, teamMaster));
	}
	
	@SecureSocial.SecuredAction
	public static Result profile() {
		Player player = authenticationService.getPlayer();
		return ok(profile.render(player));
	}

}