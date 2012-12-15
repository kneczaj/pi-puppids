package daos;

import models.Place;

import org.bson.types.ObjectId;

import com.google.code.morphia.Morphia;
import com.google.inject.Inject;
import com.mongodb.Mongo;

public class PlaceDAO extends AbstractDAO<Place, ObjectId> {

	@Inject 
	private static Mongo mongo;
	
	@Inject
	private static Morphia morphia;
	
	public PlaceDAO() {
		super(mongo, morphia);
	}
	
	public PlaceDAO(Mongo mongo, Morphia morphia) {
		super(mongo, morphia);
	}
	
}
