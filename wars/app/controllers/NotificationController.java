package controllers;

import java.util.List;

import models.Player;
import models.notifications.Notification;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import services.api.AuthenticationService;
import services.api.NotificationService;
import util.JsonHelper;

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

	@SecureSocial.SecuredAction(ajaxCall = true)
	public static Result getHistory(String offset, String count) {

		Player p = authenticationService.getPlayer();
		List<Notification> notifications = notificationService
				.getNotificationHistoryOfPlayer(p, Integer.valueOf(offset),
						Integer.valueOf(count));

		return ok(JsonHelper.toJson(notifications));
	}

}
