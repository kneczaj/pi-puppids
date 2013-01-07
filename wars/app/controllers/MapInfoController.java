package controllers;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import models.Location;
import models.Place;
import models.PlayerLocation;
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
	public static Result playersNearby(String latitude, String longitude) {
		Double lng = Double.valueOf(longitude);
		Double lat = Double.valueOf(latitude);

		Location location = new Location(lng, lat);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, -4);
		Date youngerThan = calendar.getTime();

		Map<String, PlayerLocation> playerLocations = mapInfoService
				.findPlayersNearby(location, null, youngerThan);

		return ok(JsonHelper.toJson(playerLocations));
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
