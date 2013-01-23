package daos;

import models.notifications.Notification;

import org.bson.types.ObjectId;

import com.google.code.morphia.Morphia;
import com.google.inject.Inject;
import com.mongodb.Mongo;

@SuppressWarnings("rawtypes")
public class NotificationDAO extends AbstractDAO<Notification, ObjectId> {
	
	@Inject 
	private static Mongo mongo;
	
	@Inject
	private static Morphia morphia;
	
	public NotificationDAO() {
        super(mongo, morphia);
    }
	
	public NotificationDAO(Mongo mongo, Morphia morphia) {
		super(mongo, morphia);
	}
	
}