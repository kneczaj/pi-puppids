package controllers;

import java.util.List;
import java.util.Map;

import models.Place;
import models.Player;
import models.ResourceType;
import models.Team;
import models.Unit;

import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import services.api.AuthenticationService;
import services.api.PlaceService;
import services.api.ResourceService;
import services.api.error.ResourceServiceException;
import util.JsonHelper;

import com.google.inject.Inject;

import daos.PlayerDAO;
import daos.TeamDAO;

/**
 * Controller for the ResourceService
 * 
 * @author Peter
 *
 */
public class ResourceController extends Controller {
	
	@Inject
	private static AuthenticationService authenticationService;
	
	@Inject
	private static ResourceService resourceService;
	
	@Inject
	private static PlaceService placeService;
	
	@Inject
	private static PlayerDAO playerDAO;
	
	@Inject
	private static TeamDAO teamDAO;
	
	@SecureSocial.SecuredAction(ajaxCall=true)
	public static Result getResourceSourcesOfPlayer() {
		Player p = authenticationService.getPlayer();
		
		try {
			Map<Place, ResourceType> map = resourceService.getResourceSourcesOfPlayer(p);
			
			ObjectNode resourceSources = Json.newObject();
			ObjectNode unitDeployments = Json.newObject();
			
			for (Place place : map.keySet()) {
				ObjectNode placeJson = Json.newObject();
				placeJson.put("uuid", place.getUuid());
				placeJson.put("name", place.getName());
				placeJson.put("amount", place.getAmount());
				placeJson.put("lat", place.getLat());
				placeJson.put("lng", place.getLng());
				placeJson.put("resource", place.getResource().toString());
				
				resourceSources.put(place.getIdString(), placeJson);
				
				List<Unit> units = placeService.getDeployedUnitsOfPlayer(p, place);
				unitDeployments.put(place.getIdString(), units.size());
			}

			ObjectNode json = Json.newObject();
			json.put("resourceSources", resourceSources);
			json.put("unitDeployments", unitDeployments);
			
			return ok(json.toString());
		} catch (ResourceServiceException e) {
			Logger.warn("Could not find resource sources of player", e);

			return internalServerError();
		}
	}
	
	@SecureSocial.SecuredAction(ajaxCall=true)
	public static Result getResourcesOfPlayer() {
		Player p = authenticationService.getPlayer();
		
		Map<ResourceType, Integer> resourcePlaces = resourceService.getResourcesOfPlayer(p);
		
		return ok(JsonHelper.toJson(resourcePlaces));
	}
	
	@SecureSocial.SecuredAction(ajaxCall=true)
	public static Result getResourcesOfTeam() {
		Player p = authenticationService.getPlayer();
		Team t = p.getTeam();
		
		try {
			Map<ResourceType, Integer> resourcePlaces = resourceService.getResourcesOfTeam(t);
			
			return ok(JsonHelper.toJson(resourcePlaces));
		} catch (ResourceServiceException e) {
			Logger.warn("Could not find resources of player", e);

			return internalServerError();
		}
	}

}
