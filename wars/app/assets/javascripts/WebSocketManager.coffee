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

			when "PlayerResourcesChanged"
				$.each data.data, (key, val) ->
					$("#p" + key).text val
					$("#p" + key).parent().popover
						trigger: "hover"
						placement: "bottom"
						content: $("#p" + key).parent().attr("data-content") + val

			when "ConqueringInvitation" 
				@notificationsManager.notifyConquerInvitation data
				@notificationsManager.reloadNotifications()

			when "ParticipantJoinedConquer" 
				@notificationsManager.notifyConquerParticipantJoined data
				@notificationsManager.reloadNotifications()

			when "ConquerPossible"
				@notificationsManager.notifyConquerPossible data
				@notificationsManager.reloadNotifications()
				
			when "OtherNotification"
				@notificationsManager.notify data.title, data.message, "info"
				@notificationsManager.reloadNotifications()								

	establishWebSocket: (url) ->
		socket = new @wsInstance url 
		socket.onmessage = @receiveEvent