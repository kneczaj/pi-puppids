package controllers;

import java.util.Map;

import models.Place;
import models.Player;
import models.ResourceType;
import models.Team;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import services.api.AuthenticationService;
import services.api.ResourceService;
import services.api.error.ResourceServiceException;

import com.google.gson.Gson;
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
	private static PlayerDAO playerDAO;
	
	@Inject
	private static TeamDAO teamDAO;
	
	@SecureSocial.SecuredAction(ajaxCall=true)
	public static Result getResourceSourcesOfPlayer() {
		Player p = authenticationService.getPlayer(ctx());
		
		try {
			Map<Place, ResourceType> resourcePlaces = resourceService.getResourceSourcesOfPlayer(p);
			
			Gson gson = new Gson();
			return ok(gson.toJson(resourcePlaces.keySet()));
		} catch (ResourceServiceException e) {
			Logger.warn("Could not find resource sources of player", e);

			return internalServerError();
		}
	}
	
	@SecureSocial.SecuredAction(ajaxCall=true)
	public static Result getResourcesOfPlayer() {
		Player p = authenticationService.getPlayer(ctx());
		
		try {
			Map<ResourceType, Integer> resourcePlaces = resourceService.getResourcesOfPlayer(p);
			
			Gson gson = new Gson();
			String jsonString = gson.toJson(resourcePlaces);
			return ok(jsonString);
		} catch (ResourceServiceException e) {
			Logger.warn("Could not find resources of player", e);

			return internalServerError();
		}
	}
	
	@SecureSocial.SecuredAction(ajaxCall=true)
	public static Result getResourcesOfTeam() {
		Player p = authenticationService.getPlayer(ctx());
		Team t = p.getTeam();
		
		try {
			Map<ResourceType, Integer> resourcePlaces = resourceService.getResourcesOfTeam(t);
			
			Gson gson = new Gson();
			String jsonString = gson.toJson(resourcePlaces);
			return ok(jsonString);
		} catch (ResourceServiceException e) {
			Logger.warn("Could not find resources of player", e);

			return internalServerError();
		}
	}

}
