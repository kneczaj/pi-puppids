package communication;

import java.util.List;

import models.Player;
import play.Logger;
import play.libs.Akka;
import services.api.ResourceService;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;

import com.google.inject.Inject;
import communication.messages.DistributionResultMessage;
import communication.messages.ResourceDistributionMessage;
import communication.messages.StartDistributionMessage;

import daos.PlayerDAO;

public class DistributionActor extends UntypedActor {

	@Inject
	private static PlayerDAO playerDAO;

	@Inject
	private static ResourceService resourceService;

	private final static int NUMBER_OF_ACTORS = 100;

	public static ActorRef actor = Akka.system().actorOf(
			new Props(DistributionActor.class));

	private ActorRef router = this.getContext().actorOf(
			new Props(DistributionActor.class).withRouter(new RoundRobinRouter(
					NUMBER_OF_ACTORS)), "router");

	private long distributionCounter = 0;

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
