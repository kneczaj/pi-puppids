import play.Application;
import play.GlobalSettings;
import play.Logger;

import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;

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
	
}
