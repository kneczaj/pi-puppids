package controllers;

import models.Player;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.mvc.Controller;
import play.mvc.WebSocket;

import com.google.inject.Inject;
import communication.ClientPushActor;

import daos.PlayerDAO;

public class WebSocketController extends Controller {
	
	@Inject
	private static PlayerDAO playerDAO;

	public static WebSocket<JsonNode> connect(String playerId) {
		final Player player = playerDAO.get(new ObjectId(playerId));
		
		WebSocket<JsonNode> webSocket = new WebSocket<JsonNode>() {
			// Called when the Websocket Handshake is done.
			public void onReady(WebSocket.In<JsonNode> in,
					WebSocket.Out<JsonNode> out) {
				try {
					Logger.info("WebSocket connection established");
					ClientPushActor.register(player.getId().toString(), in, out);
				} catch (Exception e) {
					Logger.warn("Could not establish WebSocket connection", e);
				}
			}
		};
		
		return webSocket;
	}
}
