package daos;


import java.util.List;

import models.Player;
import models.Team;

import org.bson.types.ObjectId;

import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.google.inject.Inject;
import com.mongodb.Mongo;


public class PlayerDAO extends AbstractDAO<Player, ObjectId> {

	@Inject 
	private static Mongo mongo;
	
	@Inject
	private static Morphia morphia;
	
	public PlayerDAO() {
        super(mongo, morphia);
    }
	
	public PlayerDAO(Mongo mongo, Morphia morphia) {
		super(mongo, morphia);
	}
	
}