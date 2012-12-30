package daos;

import models.City;

import org.bson.types.ObjectId;

import com.google.code.morphia.Morphia;
import com.google.inject.Inject;
import com.mongodb.Mongo;

/**
 * 
 * @author markus
 */
public class CityDAO extends AbstractDAO<City, ObjectId> {

	@Inject 
	private static Mongo mongo;
	
	@Inject
	private static Morphia morphia;
	
	public CityDAO() {
		super(mongo, morphia);
	}
	
	public CityDAO(Mongo mongo, Morphia morphia) {
		super(mongo, morphia);
	}

}
