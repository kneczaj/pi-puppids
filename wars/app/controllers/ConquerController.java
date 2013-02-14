package controllers;

import java.util.List;

import models.Player;
import models.conquer.CheckConquerConditionsResult;
import models.conquer.ConqueringAttempt;
import models.conquer.InitiateConquerResult;

import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import services.api.AuthenticationService;
import services.api.ConqueringService;
import services.api.ConqueringService.ConqueringStatus;
import services.api.ConqueringService.JoinConquerResult;
import services.api.NotificationService;
import services.api.WebSocketCommunicationService;
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
	
	@Inject
	private static WebSocketCommunicationService webSocketCommunicationService;

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
				
				p = authenticationService.getPlayer();
				List<Player> teamMembers = p.getTeam().getPlayers();
			
				if (conqueringResult == ConqueringStatus.LOST)
					webSocketCommunicationService.sendSimpleNotification("Conquer lost", 
							"Your conquering attempt was unsuccessful", "info", teamMembers);

				if (conqueringResult == ConqueringStatus.PLAYER_NOT_NEARBY)
					webSocketCommunicationService.sendSimpleNotification( "Conquer", 
							"Could not finish conquering attempt. You have to move towards" + 
							"the place you want to conquer", "info", teamMembers);

				if (conqueringResult == ConqueringStatus.PLACE_ALREADY_BELONGS_TO_FACTION)
					webSocketCommunicationService.sendSimpleNotification( "Conquer", 
							"The place already belongs to your faction", "info", teamMembers);

				if (conqueringResult == ConqueringStatus.RESOURCES_DO_NOT_SUFFICE)
					webSocketCommunicationService.sendSimpleNotification( "Conquer", 
							"Your resources do not suffice the resource demands of this place", 
							"info", teamMembers);

				if (conqueringResult == ConqueringStatus.NUMBER_OF_ATTACKERS_DOES_NOT_SUFFICE)
					webSocketCommunicationService.sendSimpleNotification( "Conquer", 
							"The number of teammates you gathered around the place does not suffice", 
							"info", teamMembers);

				if (conqueringResult == ConqueringStatus.PLAYER_HAS_INSUFFICIENT_RESOURCES)
					webSocketCommunicationService.sendSimpleNotification( "Conquer", 
							"Your teammates have inssufficient resources to attack this place", 
							"info", teamMembers);

				if (conqueringResult == ConqueringStatus.SUCCESSFUL) // TODO: update places list
					webSocketCommunicationService.sendSimpleNotification( "Conquering successful", 
							"Conquering attempt was successful", "success", teamMembers);
			
			return ok(JsonHelper.toJson(conqueringResult));
		} catch (GPlaceServiceException e) {
			Logger.info(e.toString() + e.getMessage());
			return ok("error");
		}
	}

}
