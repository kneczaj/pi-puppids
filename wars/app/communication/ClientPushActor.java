package communication;

import java.util.concurrent.ConcurrentMap;

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
import communication.messages.LocationChangedMessage;
import communication.messages.RegistrationMessage;
import communication.messages.UnregistrationMessage;

/**
 * 
 * @author markus
 */
public class ClientPushActor extends UntypedActor {

	static ActorRef actor = Akka.system().actorOf(
			new Props(ClientPushActor.class));

	/**
	 * The registered clients.
	 */
	ConcurrentMap<String, WebSocket.Out<JsonNode>> registrered = Maps
			.newConcurrentMap();

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

	public static void locationChanged(String id, Long timestamp, Double longitude,
			Double latitude, Integer accuracy, Integer speed) {

		actor.tell(new LocationChangedMessage(id, timestamp, longitude,
				latitude, accuracy, speed));
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof RegistrationMessage) {
			RegistrationMessage registration = (RegistrationMessage) message;
			Logger.info("Registering " + registration.getId());

			registrered.putIfAbsent(registration.getId(),
					registration.getChannel());

		} else if (message instanceof LocationChangedMessage) {
			Logger.info("pushing out player location change");
			LocationChangedMessage msg = (LocationChangedMessage) message;

			// push location change to all other players over websocket
			for (WebSocket.Out<JsonNode> channel : registrered.values()) {
				ObjectNode event = Json.newObject();
				event.put("id", msg.getId());
				event.put("timestamp", msg.getTimestamp());
				event.put("longitude", msg.getLongitude());
				event.put("latitude", msg.getLatitude());
				event.put("speed", msg.getSpeed());
				event.put("accuracy", msg.getAccuracy());

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