package daos;

import models.AccessToken;

import org.bson.types.ObjectId;

import com.google.code.morphia.Morphia;
import com.google.inject.Inject;
import com.mongodb.Mongo;

public class AccessTokenDAO extends AbstractDAO<AccessToken, ObjectId> {
	
	@Inject 
	private static Mongo mongo;
	
	@Inject
	private static Morphia morphia;
	
	public AccessTokenDAO() {
		super(mongo, morphia);
	}
	
	public AccessTokenDAO(Mongo mongo, Morphia morphia) {
		super(mongo, morphia);
	}
	
}
