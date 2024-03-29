package models.notifications;

import java.util.List;
import java.util.UUID;

import models.Player;
import models.TimeStampedModel;

import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;
import com.google.common.collect.Lists;

@Entity("notifications")
public abstract class Notification extends TimeStampedModel {

	@Reference
	private List<Player> players = Lists.newLinkedList();

	public void setPlayers(List<Player> players) {
		this.players = players;
	}
	
	public List<Player> getPlayers() {
		return this.players;
	}
	
	public boolean removePlayer(Player p) {
		return players.remove(p);
	}

	/**
	 * Has to be overwritten in subclasses of Notification
	 */
	public abstract ObjectNode toJson(); 
	
	protected final ObjectNode getInitialJson() {
		ObjectNode notification = Json.newObject();
		notification.put("time", getCreationDateTimeString());
		notification.put("id", UUID.randomUUID().toString());
		return notification;
	}
	
}