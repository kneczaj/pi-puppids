package daos;


import models.PlayerLocation;

import org.bson.types.ObjectId;

import com.google.code.morphia.Morphia;
import com.google.inject.Inject;
import com.mongodb.Mongo;


public class PlayerLocationDAO extends AbstractDAO<PlayerLocation, ObjectId> {

	@Inject 
	private static Mongo mongo;
	
	@Inject
	private static Morphia morphia;
	
	public PlayerLocationDAO() {
		super(mongo, morphia);
	}

}
