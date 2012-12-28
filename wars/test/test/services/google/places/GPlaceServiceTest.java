package test.services.google.places;

import java.util.List;
import java.util.Map;

import models.Location;
import models.PlaceType;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import services.google.places.api.GPlace;
import services.google.places.api.GPlaceService;
import services.google.places.api.GPlaceServiceException;
import test.util.InjectorHelper;
import test.util.SampleLocations;

import com.google.common.collect.Lists;
import com.google.inject.Injector;

/**
 * Test the Google Places subsystem
 * 
 * @author markus
 */
public class GPlaceServiceTest {
	
	static GPlaceService gps;
	
	@BeforeClass
	public static void startUp() {
		Injector injector = InjectorHelper.getInjector();
		Assert.assertNotNull(injector);

		gps = injector.getInstance(GPlaceService.class);
	}

	/**
	 * Tests whether a nearby search can be performed
	 * 
	 * @throws GPlaceServiceException
	 */
	@Test
	public void searchTest() throws GPlaceServiceException {
		// Search for pizza near the center of munich
		List<GPlace> pizzaPlaces = gps.search("pizza", SampleLocations.MUNICH, 100);
		Assert.assertFalse(pizzaPlaces.isEmpty());
	}

	/**
	 * Tests whether a radar search can be performed After that, one of the
	 * elements returned by the radar search is used to get details of a places
	 * reference id
	 * 
	 * @throws GPlaceServiceException
	 */
	@Test
	public void radarAndDetailsTest() throws GPlaceServiceException {
		Map<Location, String> mapping = gps.radarSearchByPlaceTypes(
				Lists.newArrayList(PlaceType.values()), SampleLocations.MUNICH,
				30);

		Assert.assertNotNull(mapping);

		for (Location l : mapping.keySet()) {
			Assert.assertNotNull(mapping.get(l));
			
			gps.details(mapping.get(l));
		}
	}

}
