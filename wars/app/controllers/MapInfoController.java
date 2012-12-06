package controllers;

import java.util.Map;

import models.Location;
import models.Place;
import models.Player;
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
	
	public static Result playersNearby() {
		try {
			Map<Player, Location> playerLocations = mapInfoService.findPlayersNearby();
			
			Gson gson = new Gson();
			return ok(gson.toJson(playerLocations));
		} catch (MapInfoServiceException e) {
			Logger.warn("Could not find nearby players", e);
			
			return internalServerError();
		}
	}
	
	public static Result placesNearby() {
		try {
			Map<Place, Location> placeLocations = mapInfoService.findPlacesNearby();
			
			Gson gson = new Gson();
			return ok(gson.toJson(placeLocations));
		} catch (MapInfoServiceException e) {
			Logger.warn("Could not find nearby places", e);
			
			return internalServerError();
		}
	}

}
