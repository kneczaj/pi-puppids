package controllers;

import models.Player;
import models.Invitation;

import views.html.invite;
import views.html.message;

import play.mvc.Controller;
import play.mvc.Result;

import services.api.TeamService;
//import services.api.ProfileService;

import securesocial.core.java.SecureSocial;
import securesocial.core.java.SocialUser;

import com.google.inject.Inject;

import daos.PlayerDAO;
import daos.InvitationDAO;

public class TeamController extends Controller {
	
	@Inject
	private static TeamService teamService;
	
//	@Inject
//	private static ProfileService profileService;
	
	@Inject
	private static PlayerDAO playerDAO;
	
	@Inject
	private static InvitationDAO invitationDAO;
	
	@SecureSocial.SecuredAction
	public static Result canInvite(String invitedEmail) {
		
		SocialUser user = (SocialUser) ctx().args.get(SecureSocial.USER_KEY);
		Player loggedPlayer = playerDAO.findOne("email", user.getEmail()); // profileService.loggedPlayer(ctx());
		
		if (invitedEmail.equals(loggedPlayer.getEmail()))
			return ok("usersEmailGiven");
		
		Player invitedPlayer = playerDAO.findOne("email", invitedEmail);
		
		if (invitedPlayer == null)
			return ok("givenEmailDoesntExist");
		
		Invitation invitation = teamService.invite(loggedPlayer, invitedPlayer);
		teamService.sendInvitation(invitation);
		
		return ok("ok");
	}
	
	public static Result inviteForm() {
		return ok(invite.render());
	}
	
	public static Result acceptInvitation(String token) {
		Invitation invitation = invitationDAO.findOne("token", token);
		if (invitation == null)
			return ok(message.render("Given invitation token is invalid"));
		
		teamService.acceptInvite(invitation);
		return ok(message.render("You have successfully joined " + invitation.getTeam().getName() + " team"));
	}

}
