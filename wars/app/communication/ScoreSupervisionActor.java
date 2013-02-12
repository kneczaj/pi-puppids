package communication;

import java.util.List;

import models.Player;
import play.Application;
import play.Logger;
import plugins.GuicePlugin;
import services.api.ScoreService;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.google.inject.Injector;
import communication.messages.CalculationMessage;
import communication.messages.CalculationResultMessage;
import communication.messages.StartCalculationMessage;

import daos.PlayerDAO;

public class ScoreSupervisionActor extends UntypedActor {

	public final static int NUMBER_OF_CALCULATORS = 5;
	
	public static ActorRef actor = null;
	public static ActorRef router = null;
	
	private static PlayerDAO playerDAO;
	private static Application application;
	private static ScoreService scoreService;
	
	private int calculationCounter = 0;
	private int calculationTarget = 0;
	
	public static void setApplication(Application app) {
		application = app;
	}

	public static void doManualInjection() {
		GuicePlugin gp = application.plugin(GuicePlugin.class);
		Injector injector = gp.getInjector();

		playerDAO = injector.getInstance(PlayerDAO.class);
		scoreService = injector.getInstance(ScoreService.class);
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof StartCalculationMessage) {

			Logger.info("Score calculation started.");
			calculationCounter = 0;
			List<Player> players = playerDAO.find().asList();
			Logger.info("Calculation score for " + players.size() + " players.");
			calculationTarget = players.size();
			if (calculationTarget > 0) {
				for (Player player : players) {
					router.tell(new CalculationMessage(player),
							getSelf());
				}
			}

		}
		if (message instanceof CalculationResultMessage) {
			calculationCounter++;
			if (calculationCounter >= calculationTarget) {
				scoreService.calculateScoreForAllTeams();
				scoreService.calculateScoreForAllFactions();
				Logger.info("Score calculation finished.");
				calculationTarget = 0;
			}
		} else {
			unhandled(message);
		}
	}

}
