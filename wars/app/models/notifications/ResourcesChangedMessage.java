package models.notifications;

import java.util.Map;

import models.Player;
import models.ResourceType;

import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;

public class ResourcesChangedMessage {
	
	public Player player;
	
	public Map<ResourceType, Integer> teamResources;
	
	public ObjectNode toJson() {
		ObjectNode message = Json.newObject();
		
		message.put("messageType", "ResourcesChanged");
		
		ObjectNode playerResources = Json.newObject();
		for (Map.Entry<ResourceType, Integer> entry : player.getResourceDepot().entrySet()) {
			playerResources.put(entry.getKey().name(), entry.getValue());
		}
		message.put("player", playerResources);
		
		ObjectNode teamResourcesJson = Json.newObject();
		for (Map.Entry<ResourceType, Integer> entry : teamResources.entrySet()) {
			teamResourcesJson.put(entry.getKey().name(), entry.getValue());
		}
		message.put("team", teamResourcesJson);
		
		return message;
	}

}
