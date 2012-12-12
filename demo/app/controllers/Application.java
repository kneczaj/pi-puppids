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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Iterator;

import javax.imageio.ImageIO;

import models.Photo;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.loading;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.routing.RoundRobinRouter;

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
		List<Photo> photos = getDatastore().find(Photo.class).asList();
		return ok(index.render(photos));
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

			return ok(bis).as("image/jpeg");
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
		return ok(fis).as("image/jpeg");
	}
	
	
	/**
	 * Starts loading sample photos
	 */
	public static Result loadTestData() {
		
		//load a list of available photos
		List<String> samplePhotos = new ArrayList<String>();
		
		for (File file : new File("photos/").listFiles()) {
			samplePhotos.add(file.toString());
		}
		
		ActorSystem system = ActorSystem.create("ImageLoader");

		// create master actor with 2 processing actores 
		ActorRef master = system.actorOf(new Props(new UntypedActorFactory() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public UntypedActor create() {
				return new Master(2);
			}
		}), "master");

		// start loading
		master.tell(new StartLoading(samplePhotos));

		return ok(loading.render("loading test data"));
	}
	
	/**
	 *  Message class, sent to master to start loading
	 * @author Kamil
	 *
	 */
	static class StartLoading {
		private final List<String> photosList;
		
		public StartLoading(List<String> photosList) {
			this.photosList = photosList;
		}
		
		public List<String> getPhotosList() {
			return photosList;
		}
	}
	
	/**
	 * Message sent after a finished task
	 * @author Kamil
	 *
	 */
	static class Finished {
	}
	
	/**
	 * Message with new task to process
	 * @author Kamil
	 *
	 */
	static class Task {
		private final String photoPath;

		public Task(String photoPath) {
			this.photoPath = photoPath;
		}

		public String getPhotoPath() {
			return photoPath;
		}
	}
	
	static class Failed extends Task {
		
		public Failed(String photoPath) {
			super(photoPath);
		}
	}
	

	/**
	 * Worker class - actor which loads photos to the db
	 * @author Kamil
	 *
	 */
	public static class Worker extends UntypedActor {

		/**
		 * creates Photo object from given path
		 *
		 */
		public static Photo loadPhoto(String path) throws IOException {
			Photo p = new Photo();
			String fullFilename = Paths.get(path)
					.toAbsolutePath().toString();
			File f = new File(fullFilename);

			BufferedImage img = ImageIO.read(f);
			int thumbWidth = 200;
			int thumbHeight = 200;
			
			if (img.getWidth() > img.getHeight()) {
				thumbWidth = 200;
				thumbHeight = (int)((200d/img.getWidth())*img.getHeight());
			} else {
				thumbHeight = 200;
				thumbWidth = (int)((200d/img.getHeight())*img.getWidth());
			}			
			
			BufferedImage thumb = new BufferedImage(thumbWidth, thumbHeight,
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
			return p;
		}


		/**
		 * handles messages
		 * @author Kamil
		 *
		 */
		public void onReceive(Object message) throws IOException {
			if (message instanceof Task) {
				Task task = (Task) message;
				String photoPath = task.getPhotoPath();
				Logger.info("Loading photo " + photoPath + " started");
				
				try {
					Photo photo = loadPhoto(photoPath);
					getDatastore().save(photo);
					getSender().tell(new Finished(), getSelf());
					Logger.info("Loading photo " + photoPath + " finished successfully");
				} catch(IOException e) {
					getSender().tell(new Failed(photoPath), getSelf());
				}
			} else {
				unhandled(message);
			}
		}
	}
	
	/**
	 * Main actor - supervisor
	 * @author Kamil
	 *
	 */
	public static class Master extends UntypedActor {
		private int photosNum = 0;
		private int processedNum = 0;

		private final ActorRef router;

		public Master(final int nrOfWorkers) {

			router = this.getContext().actorOf(new Props(Worker.class).withRouter(new RoundRobinRouter(nrOfWorkers)),
					"router");
		}

		public void onReceive(Object message) {
			if (message instanceof StartLoading) {
				startLoading(message);
			} else if (message instanceof Finished || message instanceof Failed) {
				processedNum += 1;
				if (message instanceof Finished) {
					Logger.info("New result received by master, already processed: " + processedNum + "/" + photosNum);
				} else {
					Logger.info(((Failed)message).getPhotoPath() + " failed, already processed: " + processedNum + "/" + photosNum);
				}
				if (processedNum == photosNum) {
					finish();
				}
			} else {
				unhandled(message);
			}
		}
		
		/**
		 * adds photos
		 * @author Kamil
		 *
		 */
		private void startLoading(Object message) {
			Logger.info("StartLoading message recieved by master");
			
			List<String> photos = ((StartLoading) message).getPhotosList();
			this.photosNum += photos.size();
			
			for (Iterator<String> photosIter = photos.iterator(); photosIter.hasNext(); ) {
				router.tell(new Task(photosIter.next()), getSelf());
			}
		}
		
		/**
		 * finishes, stops the system
		 * @author Kamil
		 *
		 */
		private void finish() {
			// Stops this actor and all its supervised children
			getContext().stop(getSelf());
			getContext().system().shutdown();
			Logger.info("Loading finished");
		}
	}

}