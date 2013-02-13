package controllers;

import models.Player;
import models.notifications.ShoppingListMessage;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;
import play.mvc.Result;
import securesocial.core.java.SecureSocial.SecuredAction;
import services.api.AuthenticationService;
import services.api.AvatarService;
import services.api.PlayerService;
import services.api.WebSocketCommunicationService;
import util.Validation;

import com.google.inject.Inject;

import daos.PlayerDAO;

/**
 * 
 * @author kamil
 *
 */
public class PlayerController extends AvatarControler<Player> {
	
	@Inject
	private static AuthenticationService authenticationService;
	
	@Inject
	private static AvatarService avatarService;
	
	@Inject
	private static PlayerService playerService;
	
	@Inject
	private static PlayerDAO playerDAO;
	
	@Inject
	private static WebSocketCommunicationService webSocketCommunicationService;
	
	/**
	 * Validates and saves player's profile
	 * @param firstname
	 * @param lastname
	 * @param email
	 * @param hometown
	 * @param birthday
	 * @return json array with invalid variables
	 */
	@SecuredAction(ajaxCall=true)
	public static Result changeProfile(String firstname, String lastname, String email, String hometown, String username) {
		ArrayNode errorList = Json.newObject().arrayNode();
		
		ShoppingListMessage shopping = new ShoppingListMessage();
		Player loggedPlayer = authenticationService.getPlayer();
		
		ObjectNode reply = Json.newObject();
		
		boolean isOk = true;
		if (!Validation.validateMandatoryWord(username))
		{
			isOk = false;
			errorList.add("username");
		}
		else if (!username.equals(loggedPlayer.getUsername())) {
			shopping.addUsernameChange(username);
		}
		if (!Validation.validateMandatoryWord(firstname))
		{
			isOk = false;
			errorList.add("firstname");
		}
		if (!Validation.validateMandatoryWord(lastname))
		{
			isOk = false;
			errorList.add("lastname");
		}
		if (!Validation.validateEmail(email))
		{
			isOk = false;
			errorList.add("email");
		}
		if (!Validation.validateOptionalWord(hometown))
		{
			isOk = false;
			errorList.add("hometown");
		}
		if (!isOk) {
			reply.put("errors", errorList);
			return ok(reply.toString());
		}
		
		playerService.setData(loggedPlayer, firstname, lastname, email, hometown);
		if (!shopping.isEmpty())
			reply.put("shopping", shopping.toJson());
		
		return ok(reply.toString());
	}
	
	@SecuredAction(ajaxCall=true)
	public static Result uploadPhoto() {
		Player loggedPlayer = authenticationService.getPlayer();
		return uploadPhotoTemplate(loggedPlayer, playerDAO);
	}
	
	@SecuredAction(ajaxCall=true)
	public static Result getAvatar() {
		Player loggedPlayer = authenticationService.getPlayer();
		return getAvatarTemplate(loggedPlayer);
	}
}
