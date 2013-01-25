package services.impl;

import java.util.List;

import com.google.inject.Inject;

import play.Logger;
import models.ConqueringAttempt;
import models.Player;
import models.PlayerLocation;
import models.notifications.Notification;
import services.api.NotificationService;
import services.api.WebSocketCommunicationService;
import communication.ClientPushActor;
import communication.messages.ConquerPossibleMessage;
import communication.messages.ConqueringInvitationMessage;
import communication.messages.ParticipantJoinedConquerMessage;
import communication.messages.PlayerLocationChangedMessage;
import communication.messages.SimpleNotificationMessage;

public class WebSocketCommunicationServiceImpl implements
		WebSocketCommunicationService {
	
	@Inject
	private NotificationService notificationService;
	
	// ---- BACKEND ----
	
	private void tellActor(Object message) {
		if (message instanceof Notification) {
            Notification notification = (Notification) message;
            notification.toJson();
            if (notification.isMessageGenerated())
                    notificationService.saveNotification(notification);
            else
                    Logger.error("Notification not sent because of wrong implementation - " +
                                  "toJson() should retrive the initial message from Notification class");
		}
		ClientPushActor.actor.tell(message);
	}
	
	// --- MESSAGES -----
	
	
    public void sendHi(Player player) {
            SimpleNotificationMessage testNotification = new SimpleNotificationMessage();
            testNotification.setMessageContent("Hi!");
            testNotification.setPlayers(player.getTeammates());
           
            tellActor(testNotification);
    }
   
    public void sendConqueringInvitation(ConqueringAttempt ca,
                    List<Player> onlinePlayersOfTeam) {
           
            ConqueringInvitationMessage ci = new ConqueringInvitationMessage();
            ci.conqueringAttempt = ca;
            ci.setPlayers(onlinePlayersOfTeam);
           
            tellActor(ci);
    }
   
    public void conquerParticipantJoined(Player participant, ConqueringAttempt conqueringAttempt) {
            ParticipantJoinedConquerMessage pm = new ParticipantJoinedConquerMessage();
            pm.participant = participant;
            pm.conqueringAttempt = conqueringAttempt;
           
            tellActor(pm);
    }

    public void sendConquerPossible(ConqueringAttempt conqueringAttempt) {
            ConquerPossibleMessage cpm = new ConquerPossibleMessage();
            cpm.conqueringAttempt = conqueringAttempt;
           
            tellActor(cpm);
    }
    

    public void playerLocationChanged(PlayerLocation playerLocation) {
            tellActor(new PlayerLocationChangedMessage(playerLocation));
    }



}
