package controllers;

import models.Invitation;
import models.Player;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import securesocial.core.java.SecureSocial.SecuredAction;
import securesocial.core.providers.utils.RoutesHelper;
import services.api.AuthenticationService;
import services.api.PlayerService;
import services.api.TeamService;
import services.api.WebSocketCommunicationService;
import services.api.error.PlayerServiceException;
import services.api.error.TeamServiceException;
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
	private static PlayerService playerService;
	
	@Inject
	private static WebSocketCommunicationService webSocketCommunicationService;

	@Inject
	private static PlayerDAO playerDAO;
	
	@Inject
	private static InvitationDAO invitationDAO;
	
	@SecureSocial.SecuredAction
	public static Result tryInvite(String invitedUserOrEmail) {
		
		Player loggedPlayer = authenticationService.getPlayer();
		
		Invitation invitation;
		
		try {
			invitation = teamService.prepareInvitation(invitedUserOrEmail, loggedPlayer);
		} catch (TeamServiceException e) {
			return ok(e.getMessage());
		}
		
		teamService.sendInvitation(invitation);
		return ok("ok");
	}
	
	@SecureSocial.SecuredAction(ajaxCall=true)
	public static Result joinFactionAndCity(String factionId, String cityId) {
		Player player = authenticationService.getPlayer();
		
		Logger.debug("player tries to join " + factionId + " faction and the city " + cityId);
		try {
			playerService.joinFaction(player, factionId);
			playerService.joinCity(player, cityId);
			playerDAO.save(player);
			
			return ok("ok");
		} catch (PlayerServiceException e) {
			Logger.info(e.toString());
			
			return ok("error");
		}
	}
	
	@SecureSocial.SecuredAction(ajaxCall = true)
	public static Result sendHi() {
		Player player = authenticationService.getPlayer();
		webSocketCommunicationService.sendHi(player);
		return ok("ok");
	}
	
	@SecuredAction
	public static Result deleteMember(String memberName) {
		
		Player loggedPlayer = authenticationService.getPlayer();
		if (!loggedPlayer.isTeamMaster())
			return ok("error - player is not a team master");
		
		Player member = playerDAO.findOne("username", memberName);
		
		try {
			teamService.removePlayer(loggedPlayer.getTeam(), member);
		} catch (TeamServiceException e) {
			return ok(e.getMessage());
		}
		return redirect("/");
	}
	
	@SecureSocial.SecuredAction
	public static Result acceptInvitation(String token) throws TeamServiceException {
		
		Player loggedPlayer = authenticationService.getPlayer();
		Invitation invitation;
		try {
			invitation = teamService.getInvitation(token);
		} catch (TeamServiceException e) {
			return ok(message.render("Given invitation token is invalid"));
		}
		
		
		
		try {
			Player player = teamService.acceptInvitation(invitation, loggedPlayer);
			
			webSocketCommunicationService.sendSimpleNotification("Team changed", 
					"You have successfully joined " + invitation.getTeam().getName() + " team", "info", player);
		} catch (TeamServiceException e) {
			
			switch (e.getMessage()) {
			
			case "validationFailed":
				webSocketCommunicationService.sendSimpleNotification("Error", "Your city or faction doesn't match the invitation's one." +
						"You can change them in our shop ;)", "info", loggedPlayer);
				return redirect("/");
				
			case "playerNotRegistered":
				
				ctx().flash().put("error", "You are a new player - please make the account");
				return redirect(RoutesHelper.startSignUp());
				
			case "playerNotLogged":
				
				// After logging in try again this action
				ctx().session().put("securesocial.originalUrl", ctx().request().uri());
				
				// redirect to login page with the error message below.
				ctx().flash().put("error", "You must log in as the recipient of the invitation");
				return redirect(RoutesHelper.login());
				
			default:
				throw e;
			}
		}
		
		return redirect("/");
	}
}
