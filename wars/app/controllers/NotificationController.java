package controllers;

import java.util.List;

import models.Player;
import models.notifications.Notification;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import services.api.AuthenticationService;
import services.api.NotificationService;

import com.google.inject.Inject;


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
	
//	private static ArrayNode notificationsListToJSon

	@SecureSocial.SecuredAction(ajaxCall = true)
	public static Result getHistory(String offset, String count) {

		Player p = authenticationService.getPlayer();
		List<Notification> notifications = notificationService
				.getNotificationHistoryOfPlayer(p, Integer.valueOf(offset),
						Integer.valueOf(count));
		
		ArrayNode array = Json.newObject().arrayNode();
		
		for (Notification n : notifications)
			array.add(n.toJson());
		
		return ok(array.toString());
	}
	
	@SecureSocial.SecuredAction(ajaxCall = true)
	public static Result getUndeliveredNotifications(String numberOfFullNotifications) {

		Player p = authenticationService.getPlayer();
		List<Notification> toSend = notificationService
				.takeUndeliveredNotifications(p, Integer.valueOf(numberOfFullNotifications));
		
		ArrayNode fullNotificationsArray = Json.newObject().arrayNode();
		
		for (Notification n : toSend)
			fullNotificationsArray.add(n.toJson());
		
		ObjectNode reply = Json.newObject();
		reply.put("notifications", fullNotificationsArray);
		reply.put("undeliveredNumber", notificationService.countUndeliveredNotifications(p));
		
		return ok(reply.toString());
	}
	
	@SecureSocial.SecuredAction(ajaxCall = true)
	public static Result addHistoryEntry(String title, String message, String notType) {
		Player loggedPlayer = authenticationService.getPlayer();
		notificationService.createNotificationEntry(loggedPlayer, title, message, notType);
		return ok();
	}
	
//	@SecureSocial.SecuredAction(ajaxCall = true)
//	public static Result saveNotification(String message) {
//		notificationService.saveNotification(message);
//	}

}
