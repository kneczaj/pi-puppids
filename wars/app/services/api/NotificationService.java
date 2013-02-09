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
	 * Number of undelivered notifications
	 * @param player
	 * @return
	 */
	public long countUndeliveredNotifications(Player player);
	
	/**
	 * Returns the list of undelivered notifications 
	 * 
	 * @param player
	 * @return list of undelivered notifications
	 */
	public List<Notification> takeUndeliveredNotifications(Player player, int count);
	
	/**
	 * Marks notifications as read
	 * 
	 * @param player
	 * @param notifications
	 * @return
	 */
	public void markAsRead(Player player, List<Notification> notifications);
	
	/**
	 * Marks all undelivered notifications for given player as read
	 * 
	 * @param player
	 */
	public void markAllUndeliveredAsRead(Player player);
	
	/**
	 * Creates entry in db for notification
	 * @param title
	 * @param message
	 * @param type
	 * 		type for pnotify
	 */
	public void createNotificationEntry(Player player, String title, String message, String type);
}
