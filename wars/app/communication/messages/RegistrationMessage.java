package communication.messages;

import org.codehaus.jackson.JsonNode;

import play.mvc.WebSocket;

/**
 * Registration Message
 * Is sent to the ClientPushActor when a new player joins the game.
 * 
 * @author markus
 */
public class RegistrationMessage {
	
	private final String id;
	private final WebSocket.Out<JsonNode> channel;

	public RegistrationMessage(String id, WebSocket.Out<JsonNode> channel) {
		this.id = id;
		this.channel = channel;
	}
	
	public String getId() {
		return id;
	}
	
	public WebSocket.Out<JsonNode> getChannel() {
		return channel;
	}
}