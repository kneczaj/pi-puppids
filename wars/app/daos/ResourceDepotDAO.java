package daos;

import models.ResourceDepot;

import org.bson.types.ObjectId;

import com.google.code.morphia.Morphia;
import com.google.inject.Inject;
import com.mongodb.Mongo;

public class ResourceDepotDAO extends AbstractDAO<ResourceDepot, ObjectId> {

	@Inject 
	private static Mongo mongo;
	
	@Inject
	private static Morphia morphia;
	
	public ResourceDepotDAO(Mongo mongo, Morphia morphia) {
		super(mongo, morphia);
	}
	
	public ResourceDepotDAO() {
		super(mongo, morphia);
	}

}
