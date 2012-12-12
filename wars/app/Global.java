import play.Application;
import play.GlobalSettings;

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
	}
	
}
