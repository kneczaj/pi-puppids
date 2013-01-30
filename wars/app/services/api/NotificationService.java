package services.api;

import java.util.List;

import models.Player;
import models.notifications.Notification;

public interface NotificationService {

	/**
	 * Associate a notification with several players at once.
	 * Saves it to the database.
	 * 
	 * @param notification
	 * @param players
	 */
	public void saveNotification(Notification notification);
	
	/**
	 * Get the history of notifications for a given player.
	 * The newest notifications should be in the first positions of the returned list.
	 * 
	 * @param player
	 * @param offset the number of notifications to skip 
	 * @param count the number of notifications to return
	 * @return
	 */
	public List<Notification> getNotificationHistoryOfPlayer(Player player, int offset, int count);
	
	
	/**
	 * Saves notification which was not deliver because players were offline
	 * 
	 * @param notification
	 * @param absentPlayers
	 */
	public void saveUndeliveredNotifications(Notification notification, List<Player> absentPlayers);
	
	/**
	 * Returns the list of undelivered notifications, and removes 
	 * them from undeliveredNotifications collection 
	 * 
	 * @param player
	 * @return list of undelivered notifications
	 */
	public List<Notification> takeUndeliveredNotifications(Player player);

}
