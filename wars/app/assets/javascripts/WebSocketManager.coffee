# Handels WebSocket communication
class ArWars.WebSocketManager
	@wsInstance: null
	@socket: null

	constructor: (@playerPositionManager, @conquerManager, @notificationsManager) ->
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
		
		if data.hasOwnProperty "notification-message" and data.hasOwnProperty "notification-title" 
			@notify(data.notification-title, notification-message)

		switch data.messageType
			when "PlayerLocationChange" 
				@playerPositionManager.push2Map data.id, data.latitude, data.longitude, data.accuracy

			when "ConqueringInvitation" 
				@notificationsManager.notifyConquerInvitation data

			when "ParticipantJoinedConquer" 
				@notificationsManager.notifyConquerParticipantJoined data

			when "ConquerPossible"
				@notificationsManager.notifyConquerPossible data
				
			when "OtherNotification"
				@notificationsManager.notify data.title, data.message, "info"								

	establishWebSocket: (url) ->
		socket = new @wsInstance url 
		socket.onmessage = @receiveEvent