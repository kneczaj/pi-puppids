package daos;

import java.util.List;

import models.City;
import models.Faction;
import models.Team;

import org.bson.types.ObjectId;

import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
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
	
	public void updateFactionOfTeam(Team team, Faction faction) {
		Query<Team> selection = this.createQuery().field("_id").equal(team.getId());
		UpdateOperations<Team> update = this.createUpdateOperations().set("faction", faction);
		this.update(selection, update);
	}

	public void updateCityOfTeam(Team team, City city) {
		Query<Team> selection = this.createQuery().field("_id").equal(team.getId());
		UpdateOperations<Team> update = this.createUpdateOperations().set("city", city);
		this.update(selection, update);
	}

	public List<Team> findTopKScorers(int k) {
		Query<Team> query = this.createQuery().order("-score").limit(k);
		return this.find(query).asList();
	}
	
	public long getRankOfTeam(Team t) {
		return this.createQuery().filter("score > ", t.getScore()).countAll()+1;
	}
}
