package plugins;

import play.Application;
import play.Logger;
import play.Plugin;

public class UserServicePluginStarter extends Plugin {
	
	private Application application;

	public UserServicePluginStarter(Application app) {
		this.application = app;
	}
	
	@Override
	public void onStart() {
		Logger.info("UserServicePluginInjector");
		UserServicePlugin usp = application.plugin(UserServicePlugin.class);
		usp.onMyStart();
	}

}
