package daos;

import models.Invitation;

import org.bson.types.ObjectId;

import com.google.code.morphia.Morphia;
import com.google.inject.Inject;
import com.mongodb.Mongo;

public class InvitationDAO extends AbstractDAO<Invitation, ObjectId> {
	
	@Inject 
	private static Mongo mongo;
	
	@Inject
	private static Morphia morphia;
	
	public InvitationDAO() {
        super(mongo, morphia);
    }
	
	public InvitationDAO(Mongo mongo, Morphia morphia) {
		super(mongo, morphia);
	}
	
}
