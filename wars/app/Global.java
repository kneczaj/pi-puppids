import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Akka;
import akka.actor.Props;

import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;
import communication.ClientPushActor;
import communication.DistributionActor;

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
		Logger.info("Starting ClientPushActor");
		ClientPushActor.actor = Akka.system().actorOf(
				new Props(ClientPushActor.class));
		
		Logger.info("Starting DistributionActor");
		DistributionActor.setApplication(app);
		DistributionActor.doManualInjection();
		
//		DistributionActor.actor = Akka.system().actorOf(
//				new Props(DistributionActor.class));
//		
//		Akka.system().scheduler().schedule(
//				  Duration.create(5000, TimeUnit.MILLISECONDS), //Initial delay 0 milliseconds
//				  Duration.create(ResourceDistributionValues.TIME_BETWEEN_DISTRIBUTION_IN_SECONDS, TimeUnit.SECONDS),     //Frequency 30 minutes
//				  DistributionActor.actor,
//				  new StartDistributionMessage()
//				);
	}
	
}
