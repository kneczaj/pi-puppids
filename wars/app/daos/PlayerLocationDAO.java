package daos;


import models.Player;
import models.PlayerLocation;

import org.bson.types.ObjectId;

import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
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
	
	public PlayerLocation findLatestLocation(Player p) {
		Query<PlayerLocation> query = this.createQuery().filter("player", p).order("-timestamp").limit(1);
		return this.findOne(query);
	}

}
