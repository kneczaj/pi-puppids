package controllers;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import models.Faction;
import models.Location;
import models.Place;
import models.PlayerLocation;

import org.json.JSONException;
import org.json.JSONObject;

import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import services.api.MapInfoService;
import util.JsonHelper;

import com.google.inject.Inject;

/**
 * Controller for the MapInfoService
 * 
 * @author markus
 */
public class MapInfoController extends Controller {

	@Inject
	private static MapInfoService mapInfoService;

	@SecureSocial.SecuredAction(ajaxCall=true)
	public static Result playersNearby(String latitude, String longitude) throws JSONException {
		Double lng = Double.valueOf(longitude);
		Double lat = Double.valueOf(latitude);

		Location location = new Location(lng, lat);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, -4);
		Date youngerThan = calendar.getTime();

		Map<String, PlayerLocation> playerLocations = mapInfoService
				.findPlayersNearby(location, 100, youngerThan);
		
		// Construct JSON-Object manually; avoids StackOverflowExceptions due to cyclic dependencies
		JSONObject obj = new JSONObject();
		for (Map.Entry<String, PlayerLocation> entry : playerLocations.entrySet()) {
			PlayerLocation pl = entry.getValue();
			JSONObject element = new JSONObject();
			element.put("latitude", pl.getLatitude());
			element.put("longitude", pl.getLongitude());
			element.put("speed", pl.getSpeed());
			element.put("timestamp", pl.getTimestamp());
			element.put("uncertainty", pl.getUncertainty());
			
			JSONObject team = new JSONObject();
			
			team.put("name", pl.getPlayer().getTeam().getName());
			team.put("id", pl.getPlayer().getTeam().getId().toString());
			element.put("team", team);
			
			JSONObject faction = new JSONObject();
			Faction f = pl.getPlayer().getTeam().getFaction();
			if (f != null) {
				faction.put("name", pl.getPlayer().getTeam().getFaction().getName());
				faction.put("id", pl.getPlayer().getTeam().getFaction().getId().toString());
				element.put("faction", faction);
			}

			JSONObject player = new JSONObject();
			player.put("username", pl.getPlayer().getUsername());
			player.put("id", pl.getPlayer().getId().toString());
			player.put("team", team);
			player.put("faction", faction);
			
			element.put("player", player);
			
			obj.put(entry.getKey(), element);
		}

		return ok(obj.toString());
	}

	@SecureSocial.SecuredAction(ajaxCall=true)
	public static Result placesNearby(String latitude, String longitude) {
		Double lng = Double.valueOf(longitude);
		Double lat = Double.valueOf(latitude);

		Location l = new Location(lng, lat);
		Map<Place, Location> placeLocations = mapInfoService
				.findPlacesNearby(l);

		return ok(JsonHelper.toJson(placeLocations));
	}

}
