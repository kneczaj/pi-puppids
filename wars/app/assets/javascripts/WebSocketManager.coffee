# Handels WebSocket communication
class ArWars.WebSocketManager
	@wsInstance: null
	@socket: null

	constructor: (@playerPositionManager, @conquerManager) ->
		@wsInstance = if window['MozWebSocket'] then MozWebSocket else WebSocket

	receiveEvent: (event) => 
		# there are now different types of messages
		# can be distinguished by event.data.messageType
		# possible types:
		#   - PlayerLocationChange
		#   - ConqueringInvitation
		#   - ParticipantJoinedConquer
		#   - ConquerPossible

		data = JSON.parse event.data

		switch data.messageType
			when "PlayerLocationChange" 
				@playerPositionManager.push2Map data.id, data.latitude, data.longitude, data.accuracy

			when "ConqueringInvitation" 
				@conquerManager.processConqueringInvitation data

			when "ParticipantJoinedConquer" 
				@conquerManager.processParticipantJoined data

			when "ConquerPossible"
				@conquerManager.processConquerPossible data				

	establishWebSocket: (url) ->
		socket = new @wsInstance url 
		socket.onmessage = @receiveEvent