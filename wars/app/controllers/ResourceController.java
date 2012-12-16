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
import securesocial.core.java.SocialUser;
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
	private static ResourceService resourceService;
	
	@Inject
	private static PlayerDAO playerDAO;
	
	@Inject
	private static TeamDAO teamDAO;
	
	@SecureSocial.SecuredAction(ajaxCall=true)
	public static Result getResourceSourcesOfPlayer() {
		SocialUser user = (SocialUser) ctx().args.get(SecureSocial.USER_KEY);
		Player p = playerDAO.findOne("email", user.getEmail());
		
		try {
			Map<Place, ResourceType> resourcePlaces = resourceService.getResourceSourcesOfPlayer(p);
			
			Gson gson = new Gson();
			String jsonString = gson.toJson(resourcePlaces);
			return ok(jsonString);
		} catch (ResourceServiceException e) {
			Logger.warn("Could not find resource sources of player", e);

			return internalServerError();
		}
	}
	
	@SecureSocial.SecuredAction(ajaxCall=true)
	public static Result getResourcesOfPlayer() {
		SocialUser user = (SocialUser) ctx().args.get(SecureSocial.USER_KEY);
		Player p = playerDAO.findOne("email", user.getEmail());
		
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
		SocialUser user = (SocialUser) ctx().args.get(SecureSocial.USER_KEY);
		Player p = playerDAO.findOne("email", user.getEmail());
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
