package communication;

import java.util.concurrent.ConcurrentMap;

import models.PlayerLocation;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.libs.Akka;
import play.libs.F.Callback0;
import play.libs.Json;
import play.mvc.WebSocket;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

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
	static ActorRef actor = Akka.system().actorOf(
			new Props(ClientPushActor.class));

	/**
	 * Map that holds the registrations. Maps player IDs to a WebSocket channel.
	 */
	private ConcurrentMap<String, WebSocket.Out<JsonNode>> registrered = Maps
			.newConcurrentMap();

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

			registrered.putIfAbsent(registration.getId(),
					registration.getChannel());

		} else if (message instanceof PlayerLocationChangedMessage) {
			Logger.info("pushing out player location change");
			PlayerLocationChangedMessage msg = (PlayerLocationChangedMessage) message;
			PlayerLocation location = msg.getPlayerLocation();

			// push location change to all other players over websocket
			for (WebSocket.Out<JsonNode> channel : registrered.values()) {
				ObjectNode event = Json.newObject();

				event.put("id", location.getPlayer().getId().toString());
				event.put("timestamp", location.getTimestamp().getTime());
				event.put("longitude", location.getLongitude().toString());
				event.put("latitude", location.getLatitude().toString());
				event.put("speed", location.getSpeed());
				event.put("accuracy", location.getUncertainty());

				channel.write(event);
			}
		} else if (message instanceof UnregistrationMessage) {
			UnregistrationMessage quit = (UnregistrationMessage) message;

			Logger.info("Unregistering " + quit.getId());
			registrered.remove(quit.getId());

		} else {
			unhandled(message);
		}
	}

}