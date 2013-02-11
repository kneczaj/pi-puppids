package communication.messages;

/**
 * Is sent to the client push actor if a player leaves / WebSocket was closed
 * 
 * @author markus
 */
public class UnregistrationMessage {
	
	private final String id;

	public UnregistrationMessage(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
}