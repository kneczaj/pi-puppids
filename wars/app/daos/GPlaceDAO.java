package daos;

import models.GPlace;

import org.bson.types.ObjectId;

import com.google.code.morphia.Morphia;
import com.google.inject.Inject;
import com.mongodb.Mongo;


public class GPlaceDAO extends AbstractDAO<GPlace, ObjectId> {
	
	@Inject 
	private static Mongo mongo;
	
	@Inject
	private static Morphia morphia;
	
	public GPlaceDAO() {
		super(mongo, morphia);
	}
	
	public GPlaceDAO(Mongo mongo, Morphia morphia) {
		super(mongo, morphia);
	}
}
