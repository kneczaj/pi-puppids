import java.util.concurrent.TimeUnit;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Akka;
import akka.actor.Props;
import akka.routing.RoundRobinRouter;
import akka.util.Duration;
import assets.constants.TimeConstants;

import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;
import communication.ClientPushActor;
import communication.DistributionActor;
import communication.DistributionSupervisionActor;
import communication.ScoreActor;
import communication.ScoreSupervisionActor;
import communication.messages.StartCalculationMessage;

/**
 * Global settings for the play framework. Do not move this file! play assumes
 * it in the default package.
 * 
 * @author markus
 */
public class Global extends GlobalSettings {

	@Override
	public void beforeStart(Application app) {
		MorphiaLoggerFactory.reset();
		MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);

		Logger.info("withUserNameSupport="
				+ app.configuration()
						.getBoolean("securesocial.userpass.withUserNameSupport")
						.toString());
	}

	@Override
	public void onStart(Application app) {
		Logger.info("bla");
		ClientPushActor.actor = Akka.system().actorOf(
				new Props(ClientPushActor.class));

		Logger.info("Starting DistributionActor");
		DistributionSupervisionActor.setApplication(app);
		DistributionSupervisionActor.doManualInjection();

		DistributionActor.setApplication(app);
		DistributionActor.doManualInjection();

		DistributionSupervisionActor.actor = Akka.system().actorOf(
				new Props(DistributionSupervisionActor.class));
		DistributionSupervisionActor.router = Akka
				.system()
				.actorOf(
						new Props(DistributionActor.class)
								.withRouter(new RoundRobinRouter(
										DistributionSupervisionActor.NUMBER_OF_DISTRIBUTEES)));

		Akka.system()
				.scheduler()
				.schedule(
						Duration.create(5000, TimeUnit.MILLISECONDS), // Initial
																		// delay
																		// in
																		// milliseconds
						Duration.create(
								TimeConstants.TIME_BETWEEN_DISTRIBUTION_IN_MILLISECONDS,
								TimeUnit.MILLISECONDS),
						DistributionSupervisionActor.actor,
						new StartCalculationMessage());

		Logger.info("Starting ScoreActor");
		ScoreSupervisionActor.setApplication(app);
		ScoreSupervisionActor.doManualInjection();

		ScoreActor.setApplication(app);
		ScoreActor.doManualInjection();

		ScoreSupervisionActor.actor = Akka.system().actorOf(
				new Props(ScoreSupervisionActor.class));
		ScoreSupervisionActor.router = Akka.system().actorOf(
				new Props(ScoreActor.class).withRouter(new RoundRobinRouter(
						ScoreSupervisionActor.NUMBER_OF_CALCULATORS)));
		
		Akka.system()
		.scheduler()
		.schedule(
				Duration.create(5000, TimeUnit.MILLISECONDS), // Initial
																// delay
																// in
																// milliseconds
				Duration.create(
						TimeConstants.TIME_BETWEEN_SCORE_CALCULATION_IN_MILLISECONDS,
						TimeUnit.MILLISECONDS),
				ScoreSupervisionActor.actor,
				new StartCalculationMessage());
	}
}
