package daos;

import models.notifications.UndeliveredNotification;

import org.bson.types.ObjectId;

import com.google.code.morphia.Morphia;
import com.google.inject.Inject;
import com.mongodb.Mongo;

public class UndeliveredNotificationDAO extends AbstractDAO<UndeliveredNotification, ObjectId>{
	
	@Inject 
	private static Mongo mongo;
	
	@Inject
	private static Morphia morphia;
	
	public UndeliveredNotificationDAO() {
		 super(mongo, morphia);
	}

	public UndeliveredNotificationDAO(Mongo mongo, Morphia morphia) {
		super(mongo, morphia);
	}

}
