package communication;

import java.util.List;

import models.Player;
import play.Application;
import play.Logger;
import plugins.GuicePlugin;
import services.api.ResourceService;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.google.inject.Injector;
import communication.messages.DistributionResultMessage;
import communication.messages.ResourceDistributionMessage;
import communication.messages.StartDistributionMessage;

import daos.PlayerDAO;

public class DistributionActor extends UntypedActor {

	private static PlayerDAO playerDAO;

	private static ResourceService resourceService;

//	private final static int NUMBER_OF_ACTORS = 5;
	
	public static ActorRef actor = null;
	private ActorRef router = null;

//	public static ActorRef actor = Akka.system().actorOf(
//			new Props(DistributionActor.class));
//
//	private ActorRef router = this.getContext().actorOf(
//			new Props(DistributionActor.class).withRouter(new RoundRobinRouter(
//					NUMBER_OF_ACTORS)), "router");

	private long distributionCounter = 0;
	
	private static Application application;
	
	public static void setApplication(Application app) {
		application = app;
	}
	
	public static void doManualInjection() {
		GuicePlugin gp = application.plugin(GuicePlugin.class);
	    Injector injector = gp.getInjector();
	    
	    playerDAO = injector.getInstance(PlayerDAO.class);
	    resourceService = injector.getInstance(ResourceService.class);
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof StartDistributionMessage) {
			
			Logger.info("Started resource distribution.");
			List<Player> players = playerDAO.find().asList();
			for (Player player : players) {
				router.tell(new ResourceDistributionMessage(player), getSelf());
			}

		} else if (message instanceof ResourceDistributionMessage) {

			Player player = ((ResourceDistributionMessage) message).getPlayer();
			Logger.info("Got a Message for " + player.getUsername() + ", distributing..");
			resourceService.distributeResourcesToPlayer(player);
			getSender().tell(new DistributionResultMessage(), getSelf());

		} else if (message instanceof DistributionResultMessage) {

			distributionCounter++;

			// if every player got resources, we can stop
			if (distributionCounter >= playerDAO.count()) {
				Logger.info("Distribution finished, shutting down.");
				getContext().stop(getSelf());
			}

		} else {
			unhandled(message);
		}

	}

}
