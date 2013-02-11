package daos;


import java.util.List;

import models.Player;

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
	
	public List<Player> findTopKScorers(int k) {
		Query<Player> query = this.createQuery().order("-score").limit(k);
		return this.find(query).asList();
	}
	
	public long getRankOfPlayer(Player p) {
		return this.createQuery().filter("score > ", p.getScore()).countAll()+1;
	}
	
}