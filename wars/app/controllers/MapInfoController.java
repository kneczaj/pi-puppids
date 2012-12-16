package controllers;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import models.Location;
import models.Place;
import models.PlayerLocation;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import services.api.MapInfoService;
import services.api.error.MapInfoServiceException;

import com.google.gson.Gson;
import com.google.inject.Inject;

/**
 * Controller for the MapInfoService
 * 
 * @author markus
 */
public class MapInfoController extends Controller {

	@Inject
	private static MapInfoService mapInfoService;

	public static Result playersNearby(String latitude, String longitude) {
		Double lng = Double.valueOf(longitude);
		Double lat = Double.valueOf(latitude);

		try {
			Location location = new Location(lng, lat);
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.HOUR, -2);
			Date youngerThan = calendar.getTime();

			Map<String, PlayerLocation> playerLocations = mapInfoService
					.findPlayersNearby(location, null, youngerThan);

			Gson gson = new Gson();
			String jsonString = gson.toJson(playerLocations);
			return ok(jsonString);
		} catch (MapInfoServiceException e) {
			Logger.warn("Could not find nearby players", e);

			return internalServerError();
		}
	}

	public static Result placesNearby(String latitude, String longitude) {
		Double lng = Double.valueOf(longitude);
		Double lat = Double.valueOf(latitude);

		try {
			Location l = new Location(lng, lat);
			Map<Place, Location> placeLocations = mapInfoService
					.findPlacesNearby(l);

			Gson gson = new Gson();
			return ok(gson.toJson(placeLocations));
		} catch (MapInfoServiceException e) {
			Logger.warn("Could not find nearby places", e);

			return internalServerError();
		}
	}

}
