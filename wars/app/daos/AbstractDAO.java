package daos;

import com.google.code.morphia.Morphia;
import com.google.code.morphia.dao.BasicDAO;
import com.mongodb.Mongo;

public class AbstractDAO<T, V> extends BasicDAO<T, V> {

	public final static String dbName = "arwars";
	
	public AbstractDAO(Mongo mongo, Morphia morphia) {
		super(mongo, morphia, dbName);
	}
	
}
