package communication.messages;

import models.notifications.Notification;

public class SimpleNotificationMessage extends Notification {
	
	private String messageContent;
	
	public void setMessageContent(String m) {
		messageContent = m;
	}
	
	// this method should overwrite Notification.getMessage() in every
	// derived class
	public String getMessage() {
		return messageContent;
	}
}
