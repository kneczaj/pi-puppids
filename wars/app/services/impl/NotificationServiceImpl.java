package services.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import models.Player;
import models.notifications.Notification;
import models.notifications.SimpleNotificationMessage;
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
	public void createNotificationEntry(Player player, String title, String message, String type) {
		SimpleNotificationMessage notification = new SimpleNotificationMessage();
		notification.setTitle(title);
		notification.setMessage(message);
		notification.setType(type);
		
		List<Player> playerList = new ArrayList<Player>();
    	playerList.add(player);
		notification.setPlayers(playerList);
		
		// Cannot uncoment this line
//		saveNotification(notification);
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
	
	public long countUndeliveredNotifications(Player player) {
		Query<UndeliveredNotification> q = undeliveredNotificationDAO.createQuery().field("player").equal(player);
		return q.countAll();
	}
	
	
	@Override
	public List<Notification> takeUndeliveredNotifications(Player player, int count) {
		Query<UndeliveredNotification> q = undeliveredNotificationDAO.createQuery().field("player").equal(player).limit(count);
		List<UndeliveredNotification> pairs = undeliveredNotificationDAO.find(q).asList();
		
		List<Notification> notifications = new LinkedList<Notification>();
		for (UndeliveredNotification n : pairs) {
			notifications.add(n.getNotification());
			undeliveredNotificationDAO.delete(n);
		}
		
//		undeliveredNotificationDAO.deleteByQuery(q);
		
		return notifications;
	}
	
	public void markAsRead(Player player, List<Notification> notifications) {
		Query<UndeliveredNotification> q = undeliveredNotificationDAO.createQuery();
		q.and(
			q.criteria("player").equal(player),
			q.criteria("notification").hasAnyOf(notifications)
		);
		undeliveredNotificationDAO.deleteByQuery(q);
	}
	
	
	public void markAllUndeliveredAsRead(Player player) {
		Query<UndeliveredNotification> q = undeliveredNotificationDAO.createQuery().field("player").equal(player);
		undeliveredNotificationDAO.deleteByQuery(q);
	}
	
	public void deletePlayersNotifications(Player player) {
		
		// delete undelivered references
		Query<UndeliveredNotification> un = undeliveredNotificationDAO.createQuery().field("player").equal(player);
		undeliveredNotificationDAO.deleteByQuery(un);
		
		// delete base notifications
		List<Notification> notifications = player.getNotificationsList();
		for (Notification n: notifications) {
			n.removePlayer(player);
			if (n.getPlayers().isEmpty())
				notificationDAO.delete(n);
			else
				notificationDAO.save(n);
		}
		
		player.clearNotificationsList();
		playerDAO.save(player);
	}
}
