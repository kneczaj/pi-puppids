package services.impl;

import java.util.List;

import models.Player;
import models.notifications.Notification;
import models.notifications.UndeliveredNotification;
import services.api.NotificationService;
import services.api.WebSocketCommunicationService;

import com.google.inject.Inject;

import daos.NotificationDAO;
import daos.UndeliveredNotificationDAO;

public class NotificationServiceImpl implements NotificationService {
	
	@Inject
	private NotificationDAO notificationDAO;
	
	@Inject
	private UndeliveredNotificationDAO undeliveredNotificationDAO;
	
	@Inject
	private WebSocketCommunicationService webSocketCommunicationService;

	@Override
	public void saveNotification(Notification notification) {
		
		for (Player p: notification.getPlayers())
			p.addNotification(notification);
		
		notificationDAO.save(notification);
	}
	
	@Override
	public List<Notification> getNotificationHistoryOfPlayer(Player player,
			int offset, int count) {
		
		return player.getNotificationsList().subList(offset, offset+count);
	}
	
	@Override
	public void saveUndeliveredNotifications(Notification notification, List<Player> absentPlayers) {
		
		for (Player player: absentPlayers)
			undeliveredNotificationDAO.save(new UndeliveredNotification(notification, player));
	}
	
}
