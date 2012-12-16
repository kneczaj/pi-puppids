package daos;

import models.Team;

import org.bson.types.ObjectId;

import com.google.code.morphia.Morphia;
import com.google.inject.Inject;
import com.mongodb.Mongo;

/**
 * 
 * @author markus
 */
public class TeamDAO extends AbstractDAO<Team, ObjectId> {

	@Inject 
	private static Mongo mongo;
	
	@Inject
	private static Morphia morphia;
	
	public TeamDAO() {
        super(mongo, morphia);
    }
	
	public TeamDAO(Mongo mongo, Morphia morphia) {
		super(mongo, morphia);
	}

}
