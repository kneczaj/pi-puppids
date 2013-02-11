package controllers;

import java.util.List;
import java.util.Map;

import models.City;
import models.Faction;
import models.Invitation;
import models.Player;
import models.PlayerLocation;
import models.ResourceType;
import models.Team;
import models.UnitType;

import org.bson.types.ObjectId;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import services.api.AuthenticationService;
import services.api.NotificationService;
import services.api.TeamService;
import views.html.index;
import views.html.profile;
import views.html.help;

import assets.constants.UnitMappings;

import com.google.inject.Inject;

import daos.CityDAO;
import daos.FactionDAO;
import daos.PlayerDAO;
import daos.PlayerLocationDAO;

public class Application extends Controller {

	@Inject
	private static AuthenticationService authenticationService;
	
	@Inject
	private static TeamService teamService;
	
	@Inject
	private static NotificationService notificationService;
	
	@Inject
	private static PlayerLocationDAO playerLocationDAO;
	
	@Inject
	private static FactionDAO factionDAO;
	
	@Inject
	private static CityDAO cityDAO;
	
	@Inject
	private static CityDAO invitationDAO;
	
	@Inject
	private static PlayerDAO playerDAO;
	
	@SecureSocial.SecuredAction
	public static Result index() {
		Player player = authenticationService.getPlayer();
		
		Team team = player.getTeam();
		List<Player> teammates = team.getPlayers();
		List<Faction> factions = factionDAO.find().asList();
		List<City> cities = cityDAO.find().asList();
		
		Map<UnitType, Map<ResourceType, Integer>> unitCostMap = UnitMappings.UNIT_COST_MAP;
		
		PlayerLocation playerLocation = playerLocationDAO.findLatestLocation(player);
		List<Invitation> sentInvitations = teamService.getPlayerInvitations(player);
		
		return ok(index.render(player, playerLocation, teammates, factions, cities, unitCostMap, sentInvitations));
	}
	
	@SecureSocial.SecuredAction(ajaxCall=true)
	public static Result getPlayer(String playerId) { 
		Player p = playerDAO.get(new ObjectId(playerId));
		
		ObjectNode team = Json.newObject();
		team.put("name", p.getTeam().getName());
		team.put("lat", p.getTeam().getCity().getLatitude());
		team.put("lng", p.getTeam().getCity().getLongitude());
		
		ObjectNode faction = Json.newObject();
		faction.put("name", p.getTeam().getFaction().getName());
		
		ObjectNode json = Json.newObject();
		json.put("username", p.getUsername());
		json.put("team", team);
		json.put("faction", faction);
		
		return ok(json.toString());
	}
	
	@SecureSocial.SecuredAction
	public static Result profile() {
		Player player = authenticationService.getPlayer();
		return ok(profile.render(player));
	}
	
	public static Result help() {
		return ok(help.render());
	}

}