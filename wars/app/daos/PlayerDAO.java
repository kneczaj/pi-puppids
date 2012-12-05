package daos;


import models.Player;

import org.bson.types.ObjectId;


public class PlayerDAO extends AbstractDAO<Player, ObjectId> {

	public PlayerDAO() {
        super();
    }
	
}