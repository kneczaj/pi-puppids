package communication;

import java.util.List;

import models.Player;
import play.Application;
import play.Logger;
import plugins.GuicePlugin;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.google.inject.Injector;
import communication.messages.CalculationResultMessage;
import communication.messages.CalculationMessage;
import communication.messages.StartCalculationMessage;

import daos.PlayerDAO;

public class DistributionSupervisionActor extends UntypedActor {

	public static int NUMBER_OF_DISTRIBUTEES = 5;

	private static PlayerDAO playerDAO;

	public static ActorRef actor = null;
	public static ActorRef router = null;

	private long distributionCounter = 0;
	private long distributionTarget = 0;

	private static Application application;

	public static void setApplication(Application app) {
		application = app;
	}

	public static void doManualInjection() {
		GuicePlugin gp = application.plugin(GuicePlugin.class);
		Injector injector = gp.getInjector();

		playerDAO = injector.getInstance(PlayerDAO.class);
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof StartCalculationMessage) {

			Logger.info("Resource distribution started.");
			distributionCounter = 0;
			List<Player> players = playerDAO.find().asList();
			Logger.info("Distributing to " + players.size() + " players.");
			distributionTarget = players.size();
			if (distributionTarget > 0) {
				for (Player player : players) {
					router.tell(new CalculationMessage(player),
							getSelf());
				}
			}

		}
		if (message instanceof CalculationResultMessage) {
			distributionCounter++;
			if (distributionCounter >= distributionTarget) {
				Logger.info("Distribution finished.");
				distributionTarget = 0;
			}
		} else {
			unhandled(message);
		}

	}

}
