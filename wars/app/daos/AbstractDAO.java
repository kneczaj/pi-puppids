package daos;

import play.Play;

import com.google.code.morphia.Morphia;
import com.google.code.morphia.dao.BasicDAO;
import com.mongodb.Mongo;

public class AbstractDAO<T, V> extends BasicDAO<T, V> {

	protected static String dbName = Play.isTest() ? "arwars_test" : "arwars";
	
	protected Mongo mongo;
	
	protected Morphia morphia;
	
	public AbstractDAO(Mongo mongo, Morphia morphia) {
		super(mongo, morphia, dbName);
		
		this.mongo = mongo;
		this.morphia = morphia;
	}
	
}
