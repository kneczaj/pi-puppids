var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket;
var socket;

function receiveEvent(event) {
	var data = JSON.parse(event.data);
	var lat = data.latitude;
	var lng = data.longitude;
	var playerId = data.id;
	console.log("received sth. over websockets");
	console.log(data);

	playerPositionManager.push(playerId, lat, lng, data.accuracy);
}

function establishWebSocket(url) {
	socket = new WS(url);
	socket.onmessage = receiveEvent;
}

