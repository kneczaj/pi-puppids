package services.impl;

import java.util.List;

import models.Player;
import models.PlayerLocation;
import models.conquer.ConqueringAttempt;
import models.notifications.ConquerPossibleMessage;
import models.notifications.ConqueringInvitationMessage;
import models.notifications.Notification;
import models.notifications.ParticipantJoinedConquerMessage;
import models.notifications.SimpleNotificationMessage;
import services.api.NotificationService;
import services.api.WebSocketCommunicationService;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import communication.ClientPushActor;
import communication.messages.PlayerLocationChangedMessage;

public class WebSocketCommunicationServiceImpl implements
		WebSocketCommunicationService {
	
	@Inject
	private NotificationService notificationService;
	
	// ---- BACKEND ----
	
	private void tellActor(Object message) {
		if (message instanceof Notification) {
            Notification notification = (Notification) message;
            List<Player> absentPlayers = ClientPushActor.getAbsentPlayersOf(notification.getPlayers());
            
            notificationService.saveNotification(notification);

            if (absentPlayers.size() > 0) {
            	notificationService.saveUndeliveredNotifications(notification, absentPlayers);
            }
		}
		ClientPushActor.actor.tell(message);
	}
	
	// --- MESSAGES -----
	
	
    public void sendHi(Player player) {
            SimpleNotificationMessage testNotification = new SimpleNotificationMessage();
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
            pm.setPlayers(Lists.newArrayList(conqueringAttempt.getInitiator()));
            pm.participant = participant;
            pm.conqueringAttempt = conqueringAttempt;
           
            tellActor(pm);
    }

    public void sendConquerPossible(ConqueringAttempt conqueringAttempt) {
            ConquerPossibleMessage cpm = new ConquerPossibleMessage();
            cpm.setPlayers(Lists.newArrayList(conqueringAttempt.getInitiator()));
            cpm.conqueringAttempt = conqueringAttempt;
           
            tellActor(cpm);
    }
    

    public void playerLocationChanged(PlayerLocation playerLocation) {
            tellActor(new PlayerLocationChangedMessage(playerLocation));
    }



}
