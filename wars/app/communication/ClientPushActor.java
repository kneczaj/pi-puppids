package communication;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import models.ConqueringAttempt;
import models.Player;
import models.PlayerLocation;
import models.notifications.Notification;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.libs.Akka;
import play.libs.F.Callback0;
import play.mvc.WebSocket;
import play.mvc.WebSocket.Out;
import services.api.NotificationService;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import communication.messages.ConquerPossibleMessage;
import communication.messages.ConqueringInvitationMessage;
import communication.messages.ParticipantJoinedConquerMessage;
import communication.messages.PlayerLocationChangedMessage;
import communication.messages.RegistrationMessage;
import communication.messages.SimpleNotificationMessage;
import communication.messages.UnregistrationMessage;

/**
 * Akka Actor that is mainly responsible for the WebSocket push communication to
 * clients / players.
 * 
 * @author markus
 */
public class ClientPushActor extends UntypedActor {
	
	private static NotificationService notificationService;

	/**
	 * Create a static actor instance
	 */
	static ActorRef actor = Akka.system().actorOf(
			new Props(ClientPushActor.class));

	/**
	 * Map that holds the registrations. Maps player IDs to a WebSocket channel.
	 */
	private static ConcurrentMap<String, WebSocket.Out<JsonNode>> registered = Maps
			.newConcurrentMap();

	/**
	 * Returns the set of playerId which are currently having a web socket open.
	 */
	public static List<String> getReachablePlayers() {
		return Lists.newArrayList(registered.keySet());
	}
	
//	public static void sendNotification(Notification notification) {
//		
//	}
	
	/*
	 * Sends "Hi!" message to the teammates
	 * for testing purposes
	 */
	public static void sendHi(Player player) {
		SimpleNotificationMessage testNotification = new SimpleNotificationMessage();
		testNotification.setMessageContent("Hi!");
		testNotification.setPlayers(player.getTeammates());
		
		actor.tell(testNotification);
	}
	
	public static void sendConqueringInvitation(ConqueringAttempt ca,
			List<Player> onlinePlayersOfTeam) {
		
		ConqueringInvitationMessage ci = new ConqueringInvitationMessage();
		ci.conqueringAttempt = ca;
		ci.setPlayers(onlinePlayersOfTeam);
		
		actor.tell(ci);
	}
	
	public static void conquerParticipantJoined(Player participant, ConqueringAttempt conqueringAttempt) {
		ParticipantJoinedConquerMessage pm = new ParticipantJoinedConquerMessage();
		pm.participant = participant;
		pm.conqueringAttempt = conqueringAttempt;
		
		actor.tell(pm);
	}

	public static void sendConquerPossible(ConqueringAttempt conqueringAttempt) {
		ConquerPossibleMessage cpm = new ConquerPossibleMessage();
		cpm.conqueringAttempt = conqueringAttempt;
		
		actor.tell(cpm);
	}
	
	/**
	 * Static method to register a players' WebSocket channels
	 * 
	 * @param id
	 *            playerId
	 * @param in
	 *            receiving channel of the WebSocket
	 * @param out
	 *            send channel of the WebSocket
	 * @throws Exception
	 */
	public static void register(final String id,
			final WebSocket.In<JsonNode> in, final WebSocket.Out<JsonNode> out)
			throws Exception {

		actor.tell(new RegistrationMessage(id, out));

		// When the socket is closed.
		in.onClose(new Callback0() {
			@Override
			public void invoke() {
				actor.tell(new UnregistrationMessage(id));
			}
		});
	}

	/**
	 * Used by the LocationTrackingService to tell the Actor to fan out the
	 * change of the location of a player to all others.
	 * 
	 * @param playerLocation
	 */
	public static void playerLocationChanged(PlayerLocation playerLocation) {
		actor.tell(new PlayerLocationChangedMessage(playerLocation));
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof RegistrationMessage) {
			RegistrationMessage registration = (RegistrationMessage) message;
			Logger.info("Registering " + registration.getId());

			registered.putIfAbsent(registration.getId(),
					registration.getChannel());

		} else if (message instanceof PlayerLocationChangedMessage) {
			Logger.info("pushing out player location change");
			PlayerLocationChangedMessage msg = (PlayerLocationChangedMessage) message;
			ObjectNode json = msg.toJson();
			
			// push location change to all other players over websocket
			for (WebSocket.Out<JsonNode> channel : registered.values()) {
				channel.write(json);
			}
		} else if (message instanceof UnregistrationMessage) {
			UnregistrationMessage quit = (UnregistrationMessage) message;

			Logger.info("Unregistering " + quit.getId());
			registered.remove(quit.getId());

		} else if (message instanceof ConqueringInvitationMessage) {
			ConqueringInvitationMessage ci = (ConqueringInvitationMessage) message;
			ObjectNode json = ci.toJson();
			Logger.info("Sending invitations to (" + ci.getPlayers().size() + " players): " + json.toString());
			
			// Push out the invitation
			for (Player invitedPlayer : ci.getPlayers()) {
				String playerId = invitedPlayer.getId().toString();
				Out<JsonNode> socket = registered.get(playerId);
				if (socket != null) {
					socket.write(json);	
				}
			}
		} else if (message instanceof ParticipantJoinedConquerMessage) {
			ParticipantJoinedConquerMessage m = (ParticipantJoinedConquerMessage) message;
			ObjectNode json = m.toJson();
			
			ConqueringAttempt ca = m.conqueringAttempt;
			Player initiator = ca.getInitiator();
			
			String initiatorName = initiator.getUsername();
			String initiatorId = initiator.getId().toString();
			String joiner = m.participant.getUsername();
			
			Logger.info(joiner + " joined " + initiatorName + "'s conquering attempt for " + ca.getUuid());
			Logger.info("informing the conquer initiator with a message: " + json.toString());
			
			registered.get(initiatorId).write(json);
		} else if (message instanceof ConquerPossibleMessage) {
			ConquerPossibleMessage m = (ConquerPossibleMessage) message;
			ObjectNode json = m.toJson();
			
			Player initiator = m.conqueringAttempt.getInitiator();
			String initiatorId = initiator.getId().toString();
			
			Logger.info("conquer is no possible " + json.toString());
			registered.get(initiatorId).write(json);
			
		} else if (message instanceof Notification) {
			
			Notification notification = (Notification) message;
			ObjectNode json = notification.toJson();
			
			for (Player recipient : notification.getPlayers()) {
				String playerId = recipient.getId().toString();
				registered.get(playerId).write(json);
			}
			
		} else {
			unhandled(message);
		}
		
		if (message instanceof Notification) {
			Notification notification = (Notification) message;
//			notificationService.pushOutNotifications(notification);
			//if (notification.isSent())
				//notificationService.saveNotifications(notification);
			//else
				//Logger.error("Notification not sent because of wrong implementation - " +
				//		"toJson() should retrive the initial message from Notification class");
		}
	}

}