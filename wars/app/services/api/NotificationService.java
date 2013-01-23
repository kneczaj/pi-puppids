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
	public void saveNotifications(Notification notification);
	
	/**
	 * Push a notification to the list of players mentioned.
	 * Pushes the notification over WebSocket to the player's browsers.
	 * 
	 * @param notification
	 * @param players
	 */
	public void pushOutNotifications(Notification notification);
	
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
	
}
