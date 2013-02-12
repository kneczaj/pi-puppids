package communication;

import models.Player;
import play.Application;
import play.Logger;
import plugins.GuicePlugin;
import services.api.ScoreService;
import akka.actor.UntypedActor;

import com.google.inject.Injector;
import communication.messages.CalculationMessage;
import communication.messages.CalculationResultMessage;

public class ScoreActor extends UntypedActor {

	private static Application application;
	private static ScoreService scoreService;
	
	public static void setApplication(Application app) {
		application = app;
	}

	public static void doManualInjection() {
		GuicePlugin gp = application.plugin(GuicePlugin.class);
		Injector injector = gp.getInjector();

		scoreService = injector.getInstance(ScoreService.class);
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof CalculationMessage) {
			Player player = ((CalculationMessage)message).getPlayer();
			Logger.info("Distributing to " + player.getUsername());
			scoreService.calculatePlayerScore(player);
			getSender().tell(new CalculationResultMessage());
		} else {
			unhandled(message);
		}
	}

}
