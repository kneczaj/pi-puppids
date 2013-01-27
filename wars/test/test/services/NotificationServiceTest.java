package test.services;

import java.util.List;

import models.Player;
import models.Team;
import models.notifications.Notification;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import services.api.NotificationService;
import services.api.PlayerService;
import services.api.error.PlayerServiceException;
import test.util.InjectorHelper;

import com.google.common.collect.Lists;
import com.google.inject.Injector;
import communication.messages.ConqueringInvitationMessage;
import communication.messages.SimpleNotificationMessage;

import daos.NotificationDAO;
import daos.PlayerDAO;
import daos.TeamDAO;

public class NotificationServiceTest {

	private PlayerService playerService;
	private NotificationService notificationService;
	private PlayerDAO playerDAO;
	private TeamDAO teamDAO;
	private NotificationDAO notificationDAO;

	private Player player1;
	private Player player2;

	@Before
	public void startUp() throws PlayerServiceException {
		Injector injector = InjectorHelper.getInjector();
		Assert.assertNotNull(injector);

		playerService = injector.getInstance(PlayerService.class);
		notificationService = injector.getInstance(NotificationService.class);
		playerDAO = injector.getInstance(PlayerDAO.class);
		teamDAO = injector.getInstance(TeamDAO.class);
		notificationDAO = injector.getInstance(NotificationDAO.class);

		player1 = playerService.register("userpass", "test4@test.com", "bob",
				"the builder", "haha", "username4");
		player2 = playerService.register("userpass", "test5@test.com", "bob",
				"the builder", "haha", "username5");
	}

	/**
	 * Ensure that the notifications are saved with a reference to the propriate
	 * player. Also tests the backreference from players to notifications.
	 * 
	 * @throws PlayerServiceException
	 */
	@Test
	public void saveNotificationTest() throws PlayerServiceException {
		Notification notification = new ConqueringInvitationMessage();
		notification.setPlayers(Lists.newArrayList(player1, player2));

		notificationService.saveNotification(notification);

		Player p = playerDAO.get(player1.getId());
		Assert.assertNotNull(p);
		Assert.assertNotNull(p.getNotificationsList());
		Assert.assertEquals(1, p.getNotificationsList().size());
		Assert.assertTrue(p.getNotificationsList().get(0) instanceof ConqueringInvitationMessage);

		p = playerDAO.get(player2.getId());
		Assert.assertNotNull(p);
		Assert.assertNotNull(p.getNotificationsList());
		Assert.assertEquals(1, p.getNotificationsList().size());
		Assert.assertTrue(p.getNotificationsList().get(0) instanceof ConqueringInvitationMessage);

		Notification n = notificationDAO.get(notification.getId());
		Assert.assertNotNull(n);
		Assert.assertNotNull(n.getPlayers());
		Assert.assertEquals(2, n.getPlayers().size());
	}

	/**
	 * Tests if the getNotificationHistoryOfPlayer method returns the
	 * notifications in descending order of their creation time.
	 * So list.get(0) is assumed to be the latest notification for a player
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void getNotificationHistoryTest() {
		Notification invitation = new ConqueringInvitationMessage();
		invitation.setPlayers(Lists.newArrayList(player1, player2));

		notificationService.saveNotification(invitation);

		Notification simpleNotification = new SimpleNotificationMessage();
		simpleNotification.setPlayers(Lists.newArrayList(player1, player2));

		notificationService.saveNotification(simpleNotification);

		Player p = playerDAO.get(player1.getId());
		Assert.assertNotNull(p);
		Assert.assertNotNull(p.getNotificationsList());
		Assert.assertEquals(2, p.getNotificationsList().size());

		List history = notificationService.getNotificationHistoryOfPlayer(
				player1, 0, 100);
		Assert.assertNotNull(history);
		Assert.assertEquals(2, history.size());
		Assert.assertNotNull(history.get(0));
		Assert.assertTrue(history.get(0) instanceof SimpleNotificationMessage);
		Assert.assertNotNull(history.get(1));
		Assert.assertTrue(history.get(1) instanceof ConqueringInvitationMessage);

		history = notificationService.getNotificationHistoryOfPlayer(player2,
				0, 100);
		Assert.assertNotNull(history);
		Assert.assertEquals(2, history.size());
		Assert.assertNotNull(history.get(0));
		Assert.assertTrue(history.get(0) instanceof SimpleNotificationMessage);
		Assert.assertNotNull(history.get(1));
		Assert.assertTrue(history.get(1) instanceof ConqueringInvitationMessage);
	}

	@After
	public void tearDown() {
		List<Player> players = playerDAO.find().asList();
		List<Team> teams = teamDAO.find().asList();
		List<Notification> notifications = notificationDAO.find().asList();

		for (Notification n : notifications) {
			notificationDAO.delete(n);
		}

		for (Team t : teams) {
			teamDAO.delete(t);
		}

		for (Player p : players) {
			playerDAO.delete(p);
		}
	}

}
