package communication;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import models.Player;
import models.conquer.ConqueringAttempt;
import models.notifications.ConquerPossibleMessage;
import models.notifications.ConqueringInvitationMessage;
import models.notifications.Notification;
import models.notifications.ParticipantJoinedConquerMessage;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.libs.F.Callback0;
import play.mvc.WebSocket;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import communication.messages.PlayerLocationChangedMessage;
import communication.messages.RegistrationMessage;
import communication.messages.UnregistrationMessage;

/**
 * Akka Actor that is mainly responsible for the WebSocket push communication to
 * clients / players.
 * 
 * @author markus
 */
public class ClientPushActor extends UntypedActor {

	/**
	 * Create a static actor instance
	 */
	public static ActorRef actor;

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
	
	/**
	 * Get the subset of players of playersToCheck that are currently reachable over a WebSocket connection
	 * 
	 * @param playersToCheck
	 * @return
	 */
	public static List<Player> getAbsentPlayersOf(List<Player> playersToCheck) {
		 List<String> playerIds = ClientPushActor.getReachablePlayers();
         List<Player> absentPlayers = Lists.newArrayList();
         
         for (Player p : playersToCheck) {
         	boolean isAbsent = true;
         	for (String playerId : playerIds) {
         		if (playerId.equals(p.getId().toString())) {
         			isAbsent = false;
         		}
         	}
         	
         	if (isAbsent) {
         		absentPlayers.add(p);
         	}
         }
         
         return absentPlayers;
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
			sendToPlayers(json, ci.getPlayers());
			
		} else if (message instanceof ParticipantJoinedConquerMessage) {
			ParticipantJoinedConquerMessage m = (ParticipantJoinedConquerMessage) message;
			ObjectNode json = m.toJson();
			
			ConqueringAttempt ca = m.conqueringAttempt;
			Player initiator = ca.getInitiator();
			
			String initiatorName = initiator.getUsername();
			String joiner = m.participant.getUsername();
			
			Logger.info(joiner + " joined " + initiatorName + "'s conquering attempt for " + ca.getUuid());
			Logger.info("informing the conquer initiator with a message: " + json.toString());
			
			sendToPlayer(json, initiator);
			
		} else if (message instanceof ConquerPossibleMessage) {
			ConquerPossibleMessage m = (ConquerPossibleMessage) message;
			ObjectNode json = m.toJson();
			
			Player initiator = m.conqueringAttempt.getInitiator();
			
			Logger.info("conquer is no possible " + json.toString());
			sendToPlayer(json, initiator);
			
		} else if (message instanceof Notification) {
			
			Notification notification = (Notification) message;
			ObjectNode json = notification.toJson();
			
			sendToPlayers(json, notification.getPlayers());
			
		} else {
			unhandled(message);
		}
	}
	
	private void sendToPlayer(ObjectNode json, Player recipient) {
		String playerId = recipient.getId().toString();
		WebSocket.Out<JsonNode> webSocket = registered.get(playerId);
		if (webSocket != null)
			webSocket.write(json);
	}
	
	private void sendToPlayers(ObjectNode json, List<Player> playerList) {
		
		for (Player recipient : playerList) {
			sendToPlayer(json, recipient);
		}
	}
	
}