package test.util;

import java.util.List;
import java.util.Map;

import play.test.FakeApplication;
import play.test.Helpers;
import plugins.GuicePlugin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Injector;

/**
 * Helps to get an injector instance within our tests
 * 
 * @author markus
 */
public class InjectorHelper {
	
	public static Injector getInjector() {
		Map<String, String> configuration = Maps.newHashMap();
		configuration.put("application.mode", "test");
		List<String> plugins = Lists.newArrayList();
		plugins.add("plugins.GuicePlugin");
		FakeApplication app = Helpers.fakeApplication(configuration, plugins);
	    Helpers.start(app);
	    
	    GuicePlugin gp = app.getWrappedApplication().plugin(GuicePlugin.class).get();
	    Injector injector = gp.getInjector();
	    
	    return injector;
	}

}
