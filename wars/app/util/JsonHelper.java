package util;

import com.google.gson.Gson;

/**
 * Helper-Class for turning Objects into Json with Gson
 * 
 * @author markus
 */
public class JsonHelper {
	
	public static String toJson(Object obj) {
		Gson gson = new Gson();
		return gson.toJson(obj);
	}

}
