package daos;

import models.Faction;

import org.bson.types.ObjectId;

import com.google.code.morphia.Morphia;
import com.google.inject.Inject;
import com.mongodb.Mongo;

/**
 * 
 * @author markus
 */
public class FactionDAO extends AbstractDAO<Faction, ObjectId> {

	@Inject 
	private static Mongo mongo;
	
	@Inject
	private static Morphia morphia;
	
	public FactionDAO() {
		super(mongo, morphia);
	}
	
	public FactionDAO(Mongo mongo, Morphia morphia) {
		super(mongo, morphia);
	}
	
}
