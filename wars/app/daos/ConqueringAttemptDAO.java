package daos;

import models.ConqueringAttempt;

import org.bson.types.ObjectId;

import com.google.code.morphia.Morphia;
import com.google.inject.Inject;
import com.mongodb.Mongo;

public class ConqueringAttemptDAO extends AbstractDAO<ConqueringAttempt, ObjectId> {

	@Inject 
	private static Mongo mongo;
	
	@Inject
	private static Morphia morphia;
	
	public ConqueringAttemptDAO() {
		super(mongo, morphia);
	}
	
	public ConqueringAttemptDAO(Mongo mongo, Morphia morphia) {
		super(mongo, morphia);
	}
	
}
