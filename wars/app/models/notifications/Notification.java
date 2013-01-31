package models.notifications;

import java.util.List;

import models.Player;
import models.TimeStampedModel;

import org.codehaus.jackson.node.ObjectNode;

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

	public abstract ObjectNode toJson();
	
}