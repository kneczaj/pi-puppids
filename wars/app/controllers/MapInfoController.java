package controllers;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.Faction;
import models.Location;
import models.Place;
import models.PlayerLocation;
import models.Team;

import org.json.JSONException;
import org.json.JSONObject;

import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import services.api.MapInfoService;
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
			
			Team t = pl.getPlayer().getTeam();
			team.put("name", t.getName());
			team.put("id", t.getId().toString());
			team.put("lat", t.getCity().getLatitude());
			team.put("lng", t.getCity().getLongitude());
			element.put("team", team);
			
			JSONObject faction = new JSONObject();
			Faction f = pl.getPlayer().getTeam().getFaction();
			if (f != null) {
				faction.put("name", f.getName());
				faction.put("id", f.getId().toString());
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
	public static Result loadConqueredPlaces() {
		List<Place> places = mapInfoService.findConqueredPlaces();
		JSONObject obj = new JSONObject();
		for (Place p : places) {
			JSONObject element = new JSONObject();
			try {
				element.put("uuid", p.getUuid());
				element.put("name", p.getName());
				element.put("lat", p.getLat());
				element.put("lng", p.getLng());
				element.put("type", p.getType());
				element.put("resource", p.getResource().toString());
				element.put("resAmount", p.getAmount().toString());
				element.put("units", p.getDeployedUnits().size());
				element.put("faction", p.getConqueredBy().get(0).getTeam().getFaction().getName());
				element.put("team", p.getConqueredBy().get(0).getTeam().getName());
				
				obj.put(p.getUuid(), element);
			} catch (JSONException e) {
				return ok("error");
			}
		}
		return ok(obj.toString());
	}

}
