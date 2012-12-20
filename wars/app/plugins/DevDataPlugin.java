package plugins;

import models.City;
import models.Faction;
import play.Application;
import play.Logger;
import play.Plugin;

import com.google.inject.Injector;

import daos.CityDAO;
import daos.FactionDAO;

/**
 * Takes care of inserting dev data to the database on the application startup
 * 
 * @author markus
 */
public class DevDataPlugin extends Plugin {

	private Application application;

	public DevDataPlugin(Application app) {
		this.application = app;
	}

	public void start() {
		Logger.info("Starting DevDataPlugin");

		GuicePlugin plugin = application.plugin(GuicePlugin.class);
		Injector injector = plugin.getInjector();

		CityDAO cityDAO = injector.getInstance(CityDAO.class);
		FactionDAO factionDAO = injector.getInstance(FactionDAO.class);

		if (cityDAO.findOne("name", "Munich") == null) {
			City munich = new City();
			munich.setCountry("Germany");
			munich.setLatitude(48.136944444444);
			munich.setLongitude(11.575277777778);
			munich.setName("Munich");
			cityDAO.save(munich);
		}

		if (factionDAO.findOne("name", "red") == null) {
			Faction red = new Faction();
			red.setName("red");
		}

		if (factionDAO.findOne("name", "blue") == null) {
			Faction red = new Faction();
			red.setName("blue");
		}
	}

}
