# Handels WebSocket communication
class ArWars.WebSocketManager
	@wsInstance: null
	@socket: null

	constructor: (@playerPositionManager) ->
		@wsInstance = if window['MozWebSocket'] then MozWebSocket else WebSocket

	receiveEvent: (event) => 
		data = JSON.parse(event.data)
		@playerPositionManager.push2Map data.id, data.latitude, data.longitude, data.accuracy

	establishWebSocket: (url) ->
		socket = new @wsInstance url 
		socket.onmessage = @receiveEvent