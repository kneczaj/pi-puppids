package controllers;

import models.Player;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import services.api.AuthenticationService;
import services.api.ConqueringService;
import services.api.ConqueringService.ConqueringStatus;
import services.api.ConqueringService.JoinConquerResult;
import services.google.places.api.GPlaceServiceException;
import util.JsonHelper;

import com.google.inject.Inject;

/**
 * Controller for conquering (attemps)
 * 
 * @author markus
 */
public class ConquerController extends Controller {

	@Inject
	private static AuthenticationService authenticationService;

	@Inject
	private static ConqueringService conqueringService;

	@SecureSocial.SecuredAction(ajaxCall = true)
	public static Result joinConquer(String conqueringAttemptId) {
		Player p = authenticationService.getPlayer();
		
		try {
			JoinConquerResult joinResult = conqueringService.joinConquer(
					conqueringAttemptId, p);
	
			return ok(JsonHelper.toJson(joinResult));
		} catch (GPlaceServiceException e) {
			return ok("error");
		}
	}

	@SecureSocial.SecuredAction(ajaxCall = true)
	public static Result cancelConquer(String conqueringAttemptId) {
		Player p = authenticationService.getPlayer();

		boolean cancelResult = conqueringService.cancelConquer(
				conqueringAttemptId, p);

		return ok(JsonHelper.toJson(cancelResult));
	}

	@SecureSocial.SecuredAction(ajaxCall = true)
	public static Result conquer(String conqueringAttemptId) {
		Player p = authenticationService.getPlayer();

		try {
			ConqueringStatus conqueringResult = conqueringService.conquer(
					conqueringAttemptId, p);
	
			return ok(JsonHelper.toJson(conqueringResult));
		} catch (GPlaceServiceException e) {
			return ok("error");
		}
	}

}
