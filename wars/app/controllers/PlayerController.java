package controllers;

import java.util.Date;

import models.Player;

import org.codehaus.jackson.node.ArrayNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial.SecuredAction;
import services.api.AuthenticationService;
import services.api.PlayerService;
import util.Validation;

import com.google.inject.Inject;

/**
 * 
 * @author kamil
 *
 */
public class PlayerController extends Controller {
	
	@Inject
	private static AuthenticationService authenticationService;
	
	@Inject
	private static PlayerService playerService;
	
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
	public static Result changeProfile(String firstname, String lastname, String email, String hometown, String birthday) {
		ArrayNode reply = Json.newObject().arrayNode();
		
		boolean isOk = true;
		if (!Validation.validateMandatoryWord(firstname))
		{
			isOk = false;
			reply.add("firstname");
		}
		if (!Validation.validateMandatoryWord(lastname))
		{
			isOk = false;
			reply.add("lastname");
		}
		if (!Validation.validateEmail(email))
		{
			isOk = false;
			reply.add("email");
		}
		if (!Validation.validateOptionalWord(hometown))
		{
			isOk = false;
			reply.add("hometown");
		}
		Date birthdayDate = null;
		if (!birthday.isEmpty()) {
			birthdayDate = Validation.validateDate(birthday);
			if (birthdayDate == null) {
				isOk = false;
				reply.add("birthday");
			}
		}
		if (!isOk)
			return ok(reply.toString());
		
		Player loggedPlayer = authenticationService.getPlayer();
		
		playerService.setData(loggedPlayer, firstname, lastname, email, hometown, birthdayDate);
		
		return ok(reply.toString());
	}
}
