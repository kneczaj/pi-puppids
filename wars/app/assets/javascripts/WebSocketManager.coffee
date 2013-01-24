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
				noticeNode = $("#conqueringInvitationNotice")
				$(noticeNode).find("#place").text(data.placeName)
				$(noticeNode).find("#initiator").text(data.initiatorName)

				# Click on join conquer button
				$(noticeNode).find("button[name=join]").click () -> 
					d = 
						conqueringAttemptId: data.conqueringAttemptId

					$.getJSON '/conquer/joinConquer', d, (responseData) ->
						$.pnotify
							title: 'Joined Conquer'
							text: 'You joined the conquer'
							type: 'info'

				# Jump to fighting place
				$(noticeNode).find("#place").click () => 
					newLocation = new google.maps.LatLng data.placeLat, data.placeLng
					@playerPositionManager.getMap().panTo newLocation

				notice = $.pnotify
					title: 'Conquering Invitation'
					icon: false
					width: 'auto'
					hide: false
					closer: false
					sticker: false
					insert_brs: false
				
				# todo: send ajax request to joing data.conqueringAttemptId
			when "ParticipantJoinedConquer" 
				$.pnotify
					title: 'Participant joined'
					text: "#{data.participantName} joined conquer"
					type: 'info' 

			when "ConquerPossible"
				$.pnotify
					title: 'Conquer is now possible'
					text: 'You meet all requirements to conquer the place. Click here to conduct it.'
					type: 'success' 
		

	establishWebSocket: (url) ->
		socket = new @wsInstance url 
		socket.onmessage = @receiveEvent