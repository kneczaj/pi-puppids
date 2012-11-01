package controllers;

import java.net.UnknownHostException;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {

	/**
	 * Index-Page: show an overview over all photos in the gallery
	 * @throws UnknownHostException 
	 */
	public static Result index() {
		
//		Mongo mongo = new Mongo();
//		Morphia morphia = new Morphia();
//		Datastore ds = morphia.createDatastore(mongo, "demo");
//		Photo p = new Photo();
//		//p.setId(ObjectId.get());
//		p.setIdentifier("test");
//		p.setFilename("bla");
//		ds.save(p);
		
		/**
		 * TODO: query all photos from MongoDB and assemble HTML-Page
		 */
		return ok(index.render());
	}

	/**
	 * Returns the raw image for a given photo id (so this URL can be used in the img-tag)
	 */
	public static Result getPhoto(String id) {
		/**
		 * TODO: query MongoDB for the filename corresponding to this photo id and return the raw image data
		 * set headers for an jpg image
		 */
		return TODO;
	}

	/**
	 * Returns a thumbnail of a photo
	 */
	public static Result getThumbnail(String id) {
		/**
		 * TODO: query MongoDB for the photo id and return the raw data of the thumbnail
		 * set headers for an jpg image
		 */
		return TODO;
	}

}