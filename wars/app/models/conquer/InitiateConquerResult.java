package models.conquer;

import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;

import com.google.common.base.Objects;

public class InitiateConquerResult {

	public enum Type {
		SUCCESSFUL, PLAYER_NOT_NEARBY, PLACE_ALREADY_BELONGS_TO_FACTION
	}

	private Type type;
	private ConqueringAttempt conqueringAttempt = null;

	public InitiateConquerResult(Type type) {
		this(type, null);
	}

	public InitiateConquerResult(Type type,
			ConqueringAttempt conqueringAttempt) {
		this.type = type;
		this.conqueringAttempt = conqueringAttempt;
	}
	
	public Type getType() {
		return type;
	}
	
	public ConqueringAttempt getConqueringAttempt() {
		return conqueringAttempt;
	}

	public void setConqueringAttempt(ConqueringAttempt conqueringAttempt) {
		this.conqueringAttempt = conqueringAttempt;
	}
	
	public ObjectNode toJson() {
		ObjectNode result = Json.newObject();
		result.put("type", type.toString());
		result.put("conqueringAttempt", conqueringAttempt.toJson());
		
		return result;
	}

	public String toString() {
		return Objects.toStringHelper(this).add("type", type)
				.add("conqueringAttempt", conqueringAttempt).toString();
	}

}