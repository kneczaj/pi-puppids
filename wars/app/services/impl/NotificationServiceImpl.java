package services.impl;

import java.util.List;

import models.Player;
import models.notifications.Notification;
import services.api.NotificationService;

import com.google.inject.Inject;

import daos.NotificationDAO;

@SuppressWarnings("rawtypes")
public class NotificationServiceImpl implements NotificationService {
	
	@Inject
	private static NotificationDAO notificationDAO;

	@Override
	public void saveNotifications(Notification notification,
			List<Player> players) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pushOutNotifications(Notification notification,
			List<Player> players) {
		
		// use ClientPushActor
	}
	
	@Override
	public List<Notification> getNotificationHistoryOfPlayer(Player player,
			int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

}
