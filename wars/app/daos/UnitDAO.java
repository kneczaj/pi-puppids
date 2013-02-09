package daos;

import java.util.List;

import models.Player;
import models.Unit;

import org.bson.types.ObjectId;

import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.google.inject.Inject;
import com.mongodb.Mongo;

/**
 * 
 * @author markus
 */
public class UnitDAO extends AbstractDAO<Unit, ObjectId> {

	@Inject 
	private static Mongo mongo;
	
	@Inject
	private static Morphia morphia;
	
	public UnitDAO() {
        super(mongo, morphia);
    }
	
	public UnitDAO(Mongo mongo, Morphia morphia) {
		super(mongo, morphia);
	}
	
	public List<Unit> findByPlayer(Player p) {
		Query<Unit> q = this.createQuery().filter("player", p);
		return this.find(q).asList();
	}

}
