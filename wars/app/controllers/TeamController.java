package controllers;

import java.util.List;

import models.Invitation;
import models.Player;
import models.Team;

import org.codehaus.jackson.node.ArrayNode;

import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import securesocial.core.java.SecureSocial.SecuredAction;
import securesocial.core.providers.utils.RoutesHelper;
import services.api.AuthenticationService;
import services.api.AvatarService;
import services.api.PlayerService;
import services.api.TeamService;
import services.api.WebSocketCommunicationService;
import services.api.error.PlayerServiceException;
import services.api.error.TeamServiceException;
import views.html.message;

import com.google.inject.Inject;

import daos.InvitationDAO;
import daos.PlayerDAO;
import daos.TeamDAO;

public class TeamController extends AvatarControler<Team> {

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
	private static TeamDAO teamDAO;
	
	@Inject
	private static InvitationDAO invitationDAO;
	
	@Inject
	private static AvatarService avatarService;
	
	@SecureSocial.SecuredAction
	public static Result tryInvite(String invitedUserOrEmail) {
		
		Player loggedPlayer = authenticationService.getPlayer();
		
		Invitation invitation;
		
		try {
			invitation = teamService.prepareInvitation(invitedUserOrEmail, loggedPlayer);
		} catch (TeamServiceException e) {
			return ok(e.getMessage());
		}
		
		Player recipient = invitation.getRecipient();
		teamService.sendInvitation(invitation);
		
		if (recipient != null)
			webSocketCommunicationService.sendSimpleNotification("Invitation", 
					"You are invited to team \"" + invitation.getTeam().getName() + "\"<br>" +
					"Check your mailbox", "info", recipient);
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
	
	/**
	 * validates and changes team data, consistently to player profile editor
	 * as the result returns json array with fields with wrong format 
	 * 
	 * @param name
	 * @return
	 */
	@SecureSocial.SecuredAction(ajaxCall = true)
	public static Result changeTeamData(String teamname) {
		Player loggedPlayer = authenticationService.getPlayer();
		
		ArrayNode reply = Json.newObject().arrayNode();
		
		if (!loggedPlayer.isTeamMaster()) {
			webSocketCommunicationService.sendSimpleNotification("Error", 
					"Only the team master can change the name of the team", "info", loggedPlayer);
			return ok(reply.toString());
		}
		
		try {
			teamService.changeTeamData(loggedPlayer.getTeam(), teamname);
		} catch (TeamServiceException e) {
			reply.add("teamname");
		}
		
		return ok(reply.toString());
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
			teamService.acceptInvitation(invitation, loggedPlayer);
		} catch (TeamServiceException e) {
			
			switch (e.getMessage()) {
			
			case "validationFailed":
				webSocketCommunicationService.sendSimpleNotification("Error", 
						"Your city or faction doesn't match the invitation's one." +
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
		
		webSocketCommunicationService.sendSimpleNotification("Team changed", 
				"You have successfully joined \"" + invitation.getTeam().getName() + "\" team", "info", loggedPlayer);
		
		List<Player> teammates = invitation.getTeam().getPlayers();
		teammates.remove(loggedPlayer);
		
		webSocketCommunicationService.sendSimpleNotification("New member", 
				"User " + loggedPlayer.getUsername() + " joined your team", "info", teammates);
		return redirect("/");
	}
	
	@SecuredAction(ajaxCall=true)
	public static Result uploadPhoto() {
		Team team = authenticationService.getPlayer().getTeam();
		return uploadPhotoTemplate(team, teamDAO);
	}
	
	@SecuredAction(ajaxCall=true)
	public static Result getAvatar() {
		Team team = authenticationService.getPlayer().getTeam();
		return getAvatarTemplate(team);
	}
}
