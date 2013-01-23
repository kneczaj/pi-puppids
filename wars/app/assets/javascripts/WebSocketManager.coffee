# Handels WebSocket communication
class ArWars.WebSocketManager
	@wsInstance: null
	@socket: null

	constructor: (@playerPositionManager) ->
		@wsInstance = if window['MozWebSocket'] then MozWebSocket else WebSocket

	receiveEvent: (event) => 
		# there are now different types of messages
		# can be distinguished by event.data.messageType
		# possible types:
		#   - PlayerLocationChange
		#   - ConqueringInvitation
		#   - ParticipantJoinedConquer
		#   - ConquerPossible
		data = JSON.parse(event.data)
		@playerPositionManager.push2Map data.id, data.latitude, data.longitude, data.accuracy

	establishWebSocket: (url) ->
		socket = new @wsInstance url 
		socket.onmessage = @receiveEvent