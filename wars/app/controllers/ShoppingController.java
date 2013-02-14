package controllers;

import java.util.Iterator;

import models.Invitation;
import models.Player;

import org.codehaus.jackson.JsonNode;

import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial.SecuredAction;
import services.api.AuthenticationService;
import services.api.PlayerService;
import services.api.TeamService;
import services.api.error.TeamServiceException;

import com.google.inject.Inject;

import daos.InvitationDAO;

public class ShoppingController extends Controller {
	
	@Inject
	private static PlayerService playerService;
	
	@Inject
	private static AuthenticationService authenticationService;
	
	@Inject
	private static TeamService teamService;
	
	@Inject
	private static InvitationDAO invitationDAO;
	
	/**
	 * 
	 * @param shoppingList
	 * @return
	 */
	@SecuredAction(ajaxCall=true)
	public static Result buy() {
		
		JsonNode shoppingList = request().body().asJson();
		
		JsonNode operationsNode = shoppingList.findValue("content").findValue("operations");
		Iterator<String> it = operationsNode.getFieldNames();
		
		Player loggedPlayer = authenticationService.getPlayer();
		
		while (it.hasNext()) {
			String operation = it.next(); 
			JsonNode currentOperation = operationsNode.get(operation);

			switch (operation) {
			case "acceptInvitation":
				
				String invitationToken = currentOperation.get("invitationToken").asText();
				Invitation invitation;
				try {
					invitation = teamService.getInvitation(invitationToken);
				} catch (TeamServiceException e) {
					return ok("error");
				}
				loggedPlayer = teamService.joinTeam(loggedPlayer, invitation.getTeam());
				invitationDAO.delete(invitation);
				teamService.sendInvitationAcceptanceNotifications(loggedPlayer, invitation);
				break;
				
			case "username":
				String username = currentOperation.get("username").asText();
				playerService.setUsername(loggedPlayer, username);
				break;
				
			}
		}

		return ok("bought");
	}
	
	
	
}
