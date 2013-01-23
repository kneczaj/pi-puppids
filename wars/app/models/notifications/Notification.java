package models.notifications;

import java.util.List;

import org.codehaus.jackson.node.ObjectNode;

import models.TimeStampedModel;
import models.Player;

import play.Logger;
import play.libs.Json;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;
import com.google.common.collect.Lists;

@Entity("notification")
public class Notification extends TimeStampedModel {
	
	private boolean sent;
	
	// cache for notification message - generated at sent event
	// guarantees that message will be readable without gathering
	// needed information from the db again.
	private String message;
	
	@Reference
	private List<Player> players = Lists.newLinkedList();
	
	public Notification() {
		super();
		sent = false;
	}
	
	public String getMessage() {
		if (message.isEmpty())
			generateMessage();
		return message;
	}
	
	// derived classes should override this method and return string message depending 
	// on the content of appropriate variables
	// The function should write message to this.message 
	private void generateMessage() {
		Logger.error("generateMessage() function is not implemented in " + this.getClass().getName() + " class");
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}
	
	public List<Player> getPlayers() {
		return this.players;
	}
	
	public boolean isSent() {
		return sent;
	}
	
	public ObjectNode toJson() {
		
		// Set sent to true only if this function is run from
		// derived class toJson() function.
		// Only then this message will be treated as a notification message
		// when received.
		sent = true;
		
		ObjectNode notification = Json.newObject();
		notification.put("notificationMessage", getMessage());
		
		return notification;
	}
	
}