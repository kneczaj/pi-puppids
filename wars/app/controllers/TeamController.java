package controllers;

import models.Invitation;
import models.Player;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import services.api.AuthenticationService;
import services.api.TeamService;
import views.html.message;

import com.google.inject.Inject;

import daos.InvitationDAO;
import daos.PlayerDAO;

public class TeamController extends Controller {

	@Inject
	private static AuthenticationService authenticationService;
	
	@Inject
	private static TeamService teamService;

	@Inject
	private static PlayerDAO playerDAO;
	
	@Inject
	private static InvitationDAO invitationDAO;
	
	@SecureSocial.SecuredAction
	public static Result tryInvite(String invitedUserOrEmail) {
		
		Player loggedPlayer = authenticationService.getPlayer(ctx());
		
		if (invitedUserOrEmail.equals(loggedPlayer.getEmail())	|| invitedUserOrEmail.equals(loggedPlayer.getUsername()))
			return ok("userIsYou");
		
		Player invitedPlayer;
		Boolean isEmail = invitedUserOrEmail.contains("@");
		if (isEmail)
			invitedPlayer = playerDAO.findOne("email", invitedUserOrEmail);
		else
			invitedPlayer = playerDAO.findOne("username", invitedUserOrEmail);
		
		Invitation invitation;
		if (invitedPlayer == null) {
			if (!isEmail)
				return ok("givenUserDoesntExist");
		
			invitation = teamService.iniviteStranger(loggedPlayer, invitedUserOrEmail);
		} else
			invitation = teamService.invite(loggedPlayer, invitedPlayer);
		teamService.sendInvitation(invitation);
		
		return ok("ok");
	}
	
	@SecureSocial.SecuredAction
	public static Result acceptInvitation(String token) {
		
		Invitation invitation = invitationDAO.findOne("token", token);
		if (invitation == null)
			return ok(message.render("Given invitation token is invalid"));
		
		Player loggedPlayer = authenticationService.getPlayer(ctx());
		
//		if (!loggedPlayer.equals(invitation.getRecipient())
			
		
		teamService.acceptInvite(invitation);
		return ok(message.render("You have successfully joined " + invitation.getTeam().getName() + " team"));
	}

}
