package models.notifications;

import org.codehaus.jackson.node.ObjectNode;


public class SimpleNotificationMessage extends Notification {
	
	private String message;
	private String title;
	private String type;
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	@Override
	public ObjectNode toJson() {
		ObjectNode notification = getInitialJson();

		notification.put("messageType", "OtherNotification");
		notification.put("title", getTitle());
		notification.put("message", getMessage());
		notification.put("type", getType());

		return notification;
	}

}
