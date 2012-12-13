var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket;
var socket;

function receiveEvent(event) {
	var lat = event.data.latitude;
	var lng = event.data.longitude;
	var playerId = event.data.id;

	setPlayerMarker(playerId, lat, lng);
}

function establishWebSocket(url) {
	socket = new WS(url);
	socket.onmessage = receiveEvent;
}

