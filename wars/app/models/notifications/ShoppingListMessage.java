package models.notifications;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;

import com.google.common.collect.Maps;

/**
 * 
 * @author kamil
 *
 */
public class ShoppingListMessage extends Notification {
	
	/**
	 * ShoppingList JSON format:
	 * 
	 * messageType = "ShoppingList"
	 * messageToPlayer - message shown above the shopping list
	 * content: - array of fields in the format below
	 *    operations: - operations to process when user confirms the purchase
	 *       operation - operation name distinguished by ShoppingController 
 	 *          arg1 - arguments
	 *          arg2
	 *          ...
	 *    positions: - positions on shopping list
	 *       name: price - user friendly operation name 
	 *       
	 * Then content dict is send back to ShoppingController
	 * to conduct changes
	 *  
	 */

	private Map<String, Float> positionsList = new HashMap<String, Float>();
	private Map<String, Map<String, String>> operationsList = new HashMap<String, Map<String, String>>();
	
	private String shoppingHeader;
	private String messageType = "ShoppingList";
	
	private void addOperation(String operation, Map<String, String> arguments) {
		
		operationsList.put(operation, arguments);
	}
	
	private void addPosition(String userfriendlyName, double price) {
		
		positionsList.put(userfriendlyName, new Float(price));
	}
	
	public void addUsernameChange(String username) {
		
		Map<String, String> arguments = Maps.newHashMap();
		arguments.put("username", username);
		
		addOperation("username", arguments);
		addPosition("Username change", 3.0);
	}
	
	/**
	 * Faction/city change at accepting invitation with different ones 
	 * 
	 * @param factionChange
	 * @param cityChange
	 * @param invitationToken
	 */
	public void addFactionCityChange(boolean factionChange, boolean cityChange, String invitationToken) {
		
		if (!cityChange && !factionChange)
			return;
		
		Map<String, String> arguments = Maps.newHashMap();
		arguments.put("invitationToken", invitationToken);
		
		addOperation("acceptInvitation", arguments);
		
		if (factionChange)
			addPosition("Faction change", 2.0);
		if (cityChange)
			addPosition("City change", 2.0);
	}
	
	public void setShoppingHeader(String message) {
		this.shoppingHeader = message;
	}
	
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	
	public boolean isEmpty() {
		return positionsList.isEmpty();
	}
	

	public ObjectNode toJson() {
		
		ObjectNode message = getInitialJson();
		message.put("messageType", messageType);
		message.put("shoppingHeader", shoppingHeader);
		
		ObjectNode operations = Json.newObject();
		for (Entry<String, Map<String, String>> operation : operationsList.entrySet()) {
			
			ObjectNode arguments = Json.newObject();
			for (Entry<String, String> argument : operation.getValue().entrySet()) {
				arguments.put(argument.getKey(), argument.getValue());
			}
			operations.put(operation.getKey(), arguments);
		}
		
		ObjectNode positions = Json.newObject();
		for (Entry<String, Float> entry : positionsList.entrySet())
			positions.put(entry.getKey(), entry.getValue().floatValue());
		
		ObjectNode content = Json.newObject();
		content.put("positions", positions);
		content.put("operations", operations);
		
		message.put("content", content);

		return message;
	}
}
