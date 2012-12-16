package daos;

import models.TeamInvite;

import org.bson.types.ObjectId;

import com.google.code.morphia.Morphia;
import com.google.inject.Inject;
import com.mongodb.Mongo;

public class TeamInviteDAO extends AbstractDAO<TeamInvite, ObjectId> {

	@Inject 
	private static Mongo mongo;
	
	@Inject
	private static Morphia morphia;
	
	public TeamInviteDAO() {
        super(mongo, morphia);
    }
	
	public TeamInviteDAO(Mongo mongo, Morphia morphia) {
		super(mongo, morphia);
	}
	
}
