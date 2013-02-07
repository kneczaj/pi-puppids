package models.notifications;

import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;


public class SimpleNotificationMessage extends Notification {

	@Override
	public ObjectNode toJson() {
		return Json.newObject();
	}

}
