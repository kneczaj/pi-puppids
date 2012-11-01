package controllers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.UUID;

import javax.imageio.ImageIO;

import model.Photo;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;

public class Application extends Controller {

	private static Datastore ds = null;
	private static final String mongoDbName = "demo";

	private static Datastore getDatastore() {
		if (ds == null) {
			try {
				Mongo mongo = new Mongo();
				Morphia morphia = new Morphia();
				ds = morphia.createDatastore(mongo, mongoDbName);
				ds.ensureIndexes();
			} catch (UnknownHostException e) {
				Logger.error("Could not connect to MongoDB");
			}
		}

		return ds;
	}

	/**
	 * Index-Page: show an overview over all photos in the gallery
	 * 
	 * @throws UnknownHostException
	 */
	public static Result index() throws UnknownHostException {
		for (Photo photo : getDatastore().find(Photo.class)) {
			// ...
			Logger.info(photo.toString());
		}

		return ok(index.render());
	}

	/**
	 * Returns the raw image for a given photo id (so this URL can be used in
	 * the img-tag)
	 */
	public static Result getPhoto(String id) {
		Photo p = getDatastore().find(Photo.class, "identifier", id).get();
		if (p == null) {
			return badRequest("Photo cannot be found");
		}

		try {
			FileInputStream fis = new FileInputStream(new File(p.getFilename()));
			BufferedInputStream bis = new BufferedInputStream(fis);

			return ok(bis);
		} catch (FileNotFoundException e) {
			Logger.warn("could not find photo");
			return notFound();
		}
	}

	/**
	 * Returns a thumbnail of a photo
	 */
	public static Result getThumbnail(String id) {
		Photo p = getDatastore().find(Photo.class, "identifier", id).get();
		if (p == null) {
			return badRequest("Photo cannot be found");
		}

		InputStream fis = new ByteArrayInputStream(p.getThumbnail());
		return ok(fis);
	}
	
	public static Result loadTestData() {
		try {
			Photo p = new Photo();
			String fullFilename = Paths.get("photos/schlern.jpg")
					.toAbsolutePath().toString();
			File f = new File(fullFilename);

			BufferedImage img = ImageIO.read(f);
			BufferedImage thumb = new BufferedImage(100, 200,
					BufferedImage.TYPE_INT_RGB);

			// BufferedImage has a Graphics2D
			Graphics2D g2d = (Graphics2D) thumb.getGraphics();
			g2d.drawImage(img, 0, 0, thumb.getWidth() - 1,
					thumb.getHeight() - 1, 0, 0, img.getWidth() - 1,
					img.getHeight() - 1, null);
			g2d.dispose();

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(thumb, "jpg", os);

			p.setFilename(fullFilename);
			p.setIdentifier("photo-" + UUID.randomUUID());
			p.setThumbnail(os.toByteArray());
			getDatastore().save(p);

			return ok("loaded test data");
		} catch (IOException e) {
			return internalServerError("could not load test data");
		}
	}

}