package controllers;

import models.Player;
import models.conquer.CheckConquerConditionsResult;
import models.conquer.ConqueringAttempt;
import models.conquer.InitiateConquerResult;

import org.codehaus.jackson.node.ObjectNode;

import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import services.api.AuthenticationService;
import services.api.ConqueringService;
import services.api.ConqueringService.ConqueringStatus;
import services.api.ConqueringService.JoinConquerResult;
import services.api.NotificationService;
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

	@Inject
	private static NotificationService notificationService;

	@SecureSocial.SecuredAction(ajaxCall = true)
	public static Result initiateConquer(String uuid, String reference) {
		Player p = authenticationService.getPlayer();

		try {
			InitiateConquerResult result = conqueringService.initiateConquer(p,
					uuid, reference);
			ObjectNode json = result.toJson();
			
			ConqueringAttempt ca = result.getConqueringAttempt();

			if (result.getType().equals(InitiateConquerResult.Type.SUCCESSFUL)
					&& ca != null) {
				conqueringService.sendOutInvitations(ca.getId().toString());

				CheckConquerConditionsResult ccc = conqueringService
						.checkConquerConditions(ca.getId().toString(), p);
				
				json.put("conqueringStatus", ccc.getConqueringStatus().toString());
			}
			
			return ok(json.toString());
		} catch (GPlaceServiceException e) {
			return ok("error");
		}
	}

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

			// TODO: send info about the result of the battle to all
			// participants
			return ok(JsonHelper.toJson(conqueringResult));
		} catch (GPlaceServiceException e) {
			return ok("error");
		}
	}

}
