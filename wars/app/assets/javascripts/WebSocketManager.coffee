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

		data = JSON.parse event.data

		switch data.messageType
			when "PlayerLocationChange" 
				@playerPositionManager.push2Map data.id, data.latitude, data.longitude, data.accuracy

			when "ConqueringInvitation" 
				initiator = data.initiatorId
				#place = data.placeName
				lat = data.placeLat
				lnt = data.placeLng

				noticeNode = $("#conqueringInvitationNotice")
				#noticeNode.find("#place").text(place)
				#noticeNode.find("#initiator").text(initiator)

				notice = $.pnotify
					title: 'Conquering Invitation'
					icon: false
					width: 'auto'
					hide: false
					closer: false
					sticker: false
					insert_brs: false
				
				# todo: send ajax request to joing data.conqueringAttemptId
			#when "ParticipantJoinedConquer" 

			#when "ConquerPossible" 
		

	establishWebSocket: (url) ->
		socket = new @wsInstance url 
		socket.onmessage = @receiveEvent