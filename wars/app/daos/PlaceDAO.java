package daos;

import java.util.Set;

import models.Place;
import models.Player;

import org.bson.types.ObjectId;

import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.inject.Inject;
import com.mongodb.Mongo;

public class PlaceDAO extends AbstractDAO<Place, ObjectId> {

	@Inject
	private static Mongo mongo;

	@Inject
	private static Morphia morphia;

	public PlaceDAO() {
		super(mongo, morphia);
	}

	public PlaceDAO(Mongo mongo, Morphia morphia) {
		super(mongo, morphia);
	}

	public void updateConquerors(Place place, Set<Player> conquerors) {
		Query<Place> selection = this.createQuery().field("_id")
				.equal(place.getId());
		UpdateOperations<Place> update = this.createUpdateOperations()
				.set("conqueredBy", conquerors)
				.set("numberOfConquerors", conquerors.size());
		this.update(selection, update);
	}

}
