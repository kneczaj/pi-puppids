package services.impl;

import java.util.LinkedList;
import java.util.List;

import models.Player;
import models.notifications.Notification;
import models.notifications.UndeliveredNotification;
import services.api.NotificationService;
import services.api.WebSocketCommunicationService;

import com.google.code.morphia.query.Query;
import com.google.inject.Inject;

import daos.NotificationDAO;
import daos.PlayerDAO;
import daos.UndeliveredNotificationDAO;

public class NotificationServiceImpl implements NotificationService {
	
	@Inject
	private PlayerDAO playerDAO;
	
	@Inject
	private NotificationDAO notificationDAO;
	
	@Inject
	private UndeliveredNotificationDAO undeliveredNotificationDAO;
	
	@Inject
	private WebSocketCommunicationService webSocketCommunicationService;
	
	@Override
	public void saveNotification(Notification notification) {
		notificationDAO.save(notification);
		
		for (Player p: notification.getPlayers()) {
			p.addNotification(notification);
			playerDAO.save(p);
		}
		
	}
	
	@Override
	public List<Notification> getNotificationHistoryOfPlayer(Player player,
			int offset, int count) {
		
		List<Notification> notifications = player.getNotificationsList();
		int toIndex = (notifications.size() < (offset+count)) ? notifications.size() : offset+count;
		return notifications.subList(offset, toIndex);
	}
	
	@Override
	public void saveUndeliveredNotifications(Notification notification, List<Player> absentPlayers) {
		for (Player player: absentPlayers) {
			undeliveredNotificationDAO.save(new UndeliveredNotification(notification, player));
		}

	}
	
	public List<Notification> takeUndeliveredNotifications(Player player) {
		Query<UndeliveredNotification> q = undeliveredNotificationDAO.createQuery().field("player").equal(player);
		List<UndeliveredNotification> pairs = undeliveredNotificationDAO.find(q).asList();
		
		List<Notification> notifications = new LinkedList<Notification>();
		for (UndeliveredNotification n : pairs) {
			notifications.add(n.getNotification());
		}
		
		undeliveredNotificationDAO.deleteByQuery(q);
		return notifications;
	}
	
}
