package plugins;

import play.Application;
import play.Plugin;

public class UserServicePluginStarter extends Plugin {
	
	private Application application;

	public UserServicePluginStarter(Application app) {
		this.application = app;
	}
	
	@Override
	public void onStart() {
		UserServicePlugin usp = application.plugin(UserServicePlugin.class);
		usp.start();
		
		DevDataPlugin ddp = application.plugin(DevDataPlugin.class);
		ddp.start();
	}

}
