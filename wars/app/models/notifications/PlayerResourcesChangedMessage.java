package models.notifications;

import java.util.Map;

import models.Player;
import models.ResourceType;

import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;

public class PlayerResourcesChangedMessage {
	
	public Player player;
	
	public ObjectNode toJson() {
		ObjectNode message = Json.newObject();
		
		message.put("messageType", "PlayerResourcesChanged");
		ObjectNode data = Json.newObject();
		
		for (Map.Entry<ResourceType, Integer> entry : player.getResourceDepot().entrySet()) {
			data.put(entry.getKey().name(), entry.getValue());
		}
		message.put("data", data);
		
		return message;
	}

}
