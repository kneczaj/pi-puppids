package services.impl;

import java.util.List;

import models.Player;
import models.notifications.Notification;
import services.api.NotificationService;

import com.google.inject.Inject;

import daos.NotificationDAO;

public class NotificationServiceImpl implements NotificationService {
	
	@Inject
	private static NotificationDAO notificationDAO;

	@Override
	public void saveNotifications(Notification notification) {
		
		for (Player p: notification.getPlayers())
			p.addNotification(notification);
		
		notificationDAO.save(notification);
	}

	@Override
	public void pushOutNotifications(Notification notification) {
		
		// use ClientPushActor
	}
	
	@Override
	public List<Notification> getNotificationHistoryOfPlayer(Player player,
			int offset, int count) {
		
		return player.getNotificationsList().subList(offset, offset+count);
	}
}
