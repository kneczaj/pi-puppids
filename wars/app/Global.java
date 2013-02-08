import java.util.concurrent.TimeUnit;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Akka;
import akka.util.Duration;
import assets.constants.ResourceDistributionValues;

import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;
import communication.DistributionActor;
import communication.messages.StartDistributionMessage;

/**
 * Global settings for the play framework.
 * Do not move this file! play assumes it in the default package.
 * 
 * @author markus
 */
public class Global extends GlobalSettings {

	@Override
	public void beforeStart(Application app) {
		MorphiaLoggerFactory.reset();
		MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);

		Logger.info("withUserNameSupport=" + app.configuration().getBoolean("securesocial.userpass.withUserNameSupport").toString());
	}
	
	@Override
	public void onStart(Application app) {
		//Start the ressource distribution actor
		Akka.system().scheduler().schedule(
				  Duration.create(5000, TimeUnit.MILLISECONDS), //Initial delay 0 milliseconds
				  Duration.create(ResourceDistributionValues.TIME_BETWEEN_DISTRIBUTION_IN_SECONDS, TimeUnit.SECONDS),     //Frequency 30 minutes
				  DistributionActor.actor,
				  new StartDistributionMessage()
				);
	}
	
}
