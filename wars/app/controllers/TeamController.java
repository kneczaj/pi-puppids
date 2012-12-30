package controllers;

import models.Invitation;
import models.Player;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import securesocial.core.providers.utils.RoutesHelper;
import services.api.AuthenticationService;
import services.api.PlayerService;
import services.api.TeamService;
import services.api.error.PlayerServiceException;
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
	private static PlayerDAO playerDAO;
	
	@Inject
	private static InvitationDAO invitationDAO;
	
	@SecureSocial.SecuredAction
	public static Result tryInvite(String invitedUserOrEmail) {
		
		Player loggedPlayer = authenticationService.getPlayer();
		
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
	
	@SecureSocial.SecuredAction(ajaxCall=true)
	public static Result joinFactionAndCity(String factionId, String cityId) {
		Player player = authenticationService.getPlayer();
		
		Logger.debug("player tries to join " + factionId + " faction and the city " + cityId);
		try {
			playerService.joinFaction(player, factionId);
			playerService.joinCity(player, cityId);
			
			return ok("ok");
		} catch (PlayerServiceException e) {
			Logger.info(e.toString());
			
			return ok("error");
		}
	}
	
	@SecureSocial.SecuredAction
	public static Result checkInvitationToken(String token) {
		
		Invitation invitation = invitationDAO.findOne("token", token);
		if (invitation == null)
			return ok(message.render("Given invitation token is invalid"));
		
		Player loggedPlayer = authenticationService.getPlayer();
		
		if ((loggedPlayer == null) || (!loggedPlayer.getUsername().equals(invitation.getRecipient().getUsername()) ) )
		{
			ctx().session().put("securesocial.originalUrl", ctx().request().uri());
			ctx().flash().put("error", "You must log in as the recipient of the invitation");
			return redirect(RoutesHelper.login());
		}
		
		teamService.acceptInvite(invitation);
		return ok(message.render("You have successfully joined " + invitation.getTeam().getName() + " team"));
	}

}
