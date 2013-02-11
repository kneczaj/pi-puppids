package communication;

import models.Player;
import play.Application;
import play.Logger;
import plugins.GuicePlugin;
import services.api.ResourceService;
import akka.actor.UntypedActor;

import com.google.inject.Injector;
import communication.messages.DistributionResultMessage;
import communication.messages.ResourceDistributionMessage;

public class DistributionActor extends UntypedActor {
	
	private static ResourceService resourceService;

	private static Application application;

	public static void setApplication(Application app) {
		application = app;
	}

	public static void doManualInjection() {
		GuicePlugin gp = application.plugin(GuicePlugin.class);
		Injector injector = gp.getInjector();

		resourceService = injector.getInstance(ResourceService.class);
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof ResourceDistributionMessage) {
			Player player = ((ResourceDistributionMessage)message).getPlayer();
			Logger.info("Distributing to " + player.getUsername());
			resourceService.distributeResourcesToPlayer(player);
			getSender().tell(new DistributionResultMessage());
		} else {
			unhandled(message);
		}

	}

}
