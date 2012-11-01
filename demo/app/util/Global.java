package util;

import play.Application;
import play.GlobalSettings;
import play.Logger;

import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;;

public class Global extends GlobalSettings {
	

	@Override
	public void beforeStart(Application app) {
		MorphiaLoggerFactory.reset();
		MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);
		Logger.info("Application starts");
	}

	@Override
	public void onStop(Application app) {
		Logger.info("Application shutdown...");
	}

}