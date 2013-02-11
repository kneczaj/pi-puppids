package communication;

import models.Player;
import play.Application;
import play.Logger;
import plugins.GuicePlugin;
import services.api.ResourceService;
import services.api.WebSocketCommunicationService;
import akka.actor.UntypedActor;

import com.google.inject.Injector;
import communication.messages.DistributionResultMessage;
import communication.messages.ResourceDistributionMessage;

public class DistributionActor extends UntypedActor {
	
	private static ResourceService resourceService;

	private static Application application;
	
	private static WebSocketCommunicationService webSocketCommunicationService;

	public static void setApplication(Application app) {
		application = app;
	}

	public static void doManualInjection() {
		GuicePlugin gp = application.plugin(GuicePlugin.class);
		Injector injector = gp.getInjector();

		resourceService = injector.getInstance(ResourceService.class);
		webSocketCommunicationService = injector.getInstance(WebSocketCommunicationService.class);
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof ResourceDistributionMessage) {
			Player player = ((ResourceDistributionMessage)message).getPlayer();
			Logger.info("Distributing to " + player.getUsername());
			resourceService.distributeResourcesToPlayer(player);
			webSocketCommunicationService.sendPlayerResourcesChanged(player);
			getSender().tell(new DistributionResultMessage());
		} else {
			unhandled(message);
		}

	}

}
