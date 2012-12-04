package dao;

import java.net.UnknownHostException;

import com.google.code.morphia.Morphia;
import com.google.code.morphia.dao.BasicDAO;
import com.mongodb.Mongo;

public class AbstractDAO<T, V> extends BasicDAO<T, V> {

	public final static String dbName = "arwars";
	private static Mongo mongo;
	private static Morphia morphia;
	
	static {
		try {
			mongo = new Mongo();
			morphia = new Morphia();
		} catch (UnknownHostException e) {
			// bad bad error
		}
	}
	
	public AbstractDAO() {
		super(mongo, morphia, dbName);
	}
	
}
