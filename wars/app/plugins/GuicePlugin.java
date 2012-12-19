package plugins;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import play.Application;
import play.Logger;
import services.api.Service;

import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.typesafe.plugin.inject.Helper;
import com.typesafe.plugin.inject.InjectPlugin;
import com.typesafe.plugin.inject.RequestStaticInjection;

import daos.AbstractDAO;

public class GuicePlugin extends InjectPlugin {

	private Injector injector = null;

	public GuicePlugin(Application app) {
		super(app);
		
		MorphiaLoggerFactory.reset();
		MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);
		Logger.info("Loading guice plugin");
	}

	@Override
	public boolean enabled() {
		return !(app.configuration().getString("guiceplugin") != null && app
				.configuration().getString("guiceplugin").equals("disabled"));
	}

	public <T> T getInstance(Class<T> type) {
		if (injector == null)
			Logger.warn("injector is null - perhaps plugin is not configured before GlobalPlugin or onStart was not called yet");

		return injector.getInstance(type);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Class<Object>[] filterControllerClasses(Class[] fullClassList) {
		ArrayList<Class<Object>> classNames = new ArrayList<Class<Object>>();
		for (Class c : fullClassList) {
			if (play.mvc.Controller.class.isAssignableFrom(c)) {
				classNames.add(c);
				continue;
			}
			if (play.api.mvc.Controller.class.isAssignableFrom(c)) {
				classNames.add(c);
			}
		}

		Class[] r = (Class[]) classNames.toArray(new Class[classNames.size()]);
		return (Class<Object>[]) r;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Class<Object>[] filterServiceClasses(Class[] fullClassList) {
		ArrayList<Class<Object>> classNames = new ArrayList<Class<Object>>();
		for (Class c : fullClassList) {
			if (Service.class.isAssignableFrom(c)
					&& !c.getName().equals("Service")) {
				classNames.add(c);
			}
		}

		Class[] r = (Class[]) classNames.toArray(new Class[classNames.size()]);
		return (Class<Object>[]) r;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Class<Object>[] filterDAOClasses(Class[] fullClassList) {
		ArrayList<Class<Object>> classNames = new ArrayList<Class<Object>>();
		for (Class c : fullClassList) {
			if (AbstractDAO.class.isAssignableFrom(c)
					&& !c.getName().equals("daos.AbstractDAO")) {
				classNames.add(c);
			}
		}

		Class[] r = (Class[]) classNames.toArray(new Class[classNames.size()]);
		return (Class<Object>[]) r;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void onStart() {
		Logger.info("Starting guice plugin");
		Class[] services = filterServiceClasses(Helper.getClasses(
				"services.impl", app.classloader()));
		Class[] daos = filterDAOClasses(Helper.getClasses("daos",
				app.classloader()));
		Class[] controllers = filterControllerClasses(Helper.getClasses(
				"controllers", app.classloader()));

		Class<Object>[] injectables = (Class<Object>[]) ArrayUtils.addAll(daos,
				services);
		injectables = (Class<Object>[]) ArrayUtils.addAll(injectables,
				controllers);

		// create injector with static support
		injector = Guice.createInjector(convertToModules(availableModules(),
				injectables));

		// inject
		for (Class<Object> clazz : injectables) {
			try {
				Logger.debug("injection for " + clazz.getName());
				injector.injectMembers(createOrGetInstane(clazz));
			} catch (java.lang.IllegalArgumentException ex) {
				Logger.debug("skipping injection for " + clazz.getName());
			}
		}
	}
	
	public Injector getInjector() {
		return injector;
	}

	/**
	 * converts modules into Guice modules, also adding Static Injection support
	 */
	private List<Module> convertToModules(List<Object> modules,
			Class<Object>[] injectables) {
		List<Module> guiceModules = new ArrayList<Module>();

		guiceModules.add(new RequestStaticInjection(injectables));

		for (Object m : modules) {
			guiceModules.add((Module) m);
		}

		return guiceModules;
	}

	/**
	 * creates instance for default constructor
	 */
	private Object createOrGetInstane(Class<Object> clazz)
			throws java.lang.IllegalArgumentException {
		try {
			try {
				return clazz.newInstance();
			} catch (IllegalAccessException ex) {
				Field field = clazz.getField("MODULE$");
				return field.get(null);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new java.lang.IllegalArgumentException();
		}
	}
}