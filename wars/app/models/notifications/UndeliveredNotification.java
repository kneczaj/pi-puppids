package models.notifications;

import models.BaseModel;
import models.Player;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;

@Entity("undeliveredNotifications")
public class UndeliveredNotification extends BaseModel {

	@Reference
	private Player player;
	
	@Reference
	private Notification notification;
	
	public UndeliveredNotification() {
		super();
	}
	
	public UndeliveredNotification(Notification notification, Player player) {
		this.player = player;
		this.notification = notification;
		
	}
	
	public Notification getNotification() {
		return notification;
	}
	
	public Player getPlayer() {
		return player;
	}
	
}
