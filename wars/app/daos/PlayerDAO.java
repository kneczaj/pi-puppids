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
	
	public List<Player> findPlayers(List<String> playerIds, Team t) {
		Query<Player> query = this.createQuery().filter("_id in", playerIds).filter("team", t);
		return this.find(query).asList();
	}
	
}