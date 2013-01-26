package controllers;

import java.util.List;

import models.Player;

import org.codehaus.jackson.node.ArrayNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import services.api.AuthenticationService;
import services.api.NotificationService;

import com.google.inject.Inject;
import communication.messages.ConqueringInvitationMessage;

/**
 * Controller for the NotificationService
 * 
 * @author markus
 */
public class NotificationController extends Controller {

	@Inject
	private static AuthenticationService authenticationService;

	@Inject
	private static NotificationService notificationService;

	@SecureSocial.SecuredAction(ajaxCall = true)
	public static Result getHistory(String offset, String count) {

		Player p = authenticationService.getPlayer();
		List notifications = notificationService
				.getNotificationHistoryOfPlayer(p, Integer.valueOf(offset),
						Integer.valueOf(count));
		
		ArrayNode array = Json.newObject().arrayNode();
		
		for (Object n : notifications) {
			if (n instanceof ConqueringInvitationMessage) {
				ConqueringInvitationMessage cim = (ConqueringInvitationMessage) n;
				array.add(cim.toJson());
			}
		}
		
		return ok(array.toString());
	}

}
