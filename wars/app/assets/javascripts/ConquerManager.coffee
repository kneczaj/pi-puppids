class ArWars.ConquerManager

	playerPositionManager = null

	constructor: () ->

	setPlayerPositionManager: (pm) ->
		@playerPositionManager = pm

	conquer: (conqueringAttemptId) ->
		data = 
			conqueringAttemptId: conqueringAttemptId

		$.getJSON '/conquer/conquer', data, (responseData) => 
			console.log responseData
			if responseData == 'SUCCESSFUL'
				@notify 'Conquering successful', 'Conquering attempt was successful', 'success'

	processParticipantJoined: (data) ->
		@notify 'Participant joined', "#{data.participantName} joined conquer", 'info' 

	processConquerPossible: (data) ->
		@notify 'Conquer is now possible', 'You meet all requirements to conquer the place. Click here to conduct it.', 'success' 

	processConqueringInvitation: (invitation) ->
		noticeNode = $("#conqueringInvitationNotice")
		$(noticeNode).find("#place").text(invitation.placeName)
		$(noticeNode).find("#initiator").text(invitation.initiatorName)

		# Jump to fighting place
		$(noticeNode).find("#place").click () => 
			newLocation = new google.maps.LatLng data.placeLat, data.placeLng
			@playerPositionManager.getMap().panTo newLocation
	
		$.pnotify
			title: 'Conquering Invitation'
			icon: false
			width: 'auto'
			hide: false
			closer: false
			sticker: false
			insert_brs: false
		
		# Click on join conquer button
		$(noticeNode).find("button[name=join]").click () =>
			@joinConquer invitation.conqueringAttemptId

	joinConquer: (conqueringAttemptId) ->
		d = 
			conqueringAttemptId: conqueringAttemptId

		$.getJSON '/conquer/joinConquer', d, (responseData) ->
			@notify 'Joined Conquer', 'You joined the conquer', 'info'

	cancelConquer: (conqueringAttemptId) ->
		d = 
			conqueringAttemptId: conqueringAttemptId

		$.getJSON '/conquer/cancelConquer', d, (responseData) ->
			@notify 'Canceled Conquer', 'The conquering attempt was canceled', 'success'

	initiateConquer: (uuid, reference) ->
		data = 
			uuid: uuid
			reference: reference

		$.getJSON '/conquer/initiateConquer', data, (responseData) =>
			if responseData.type == 'PLAYER_NOT_NEARBY'	
				@notify 'Error', 'You are too far away from the place you want to conquer', 'error'

			if responseData.type == 'PLACE_ALREADY_BELONGS_TO_FACTION'
				@notify '', 'This place already belongs to your faction', 'info'

			if responseData.type == 'SUCCESSFUL'
				if responseData.conqueringStatus == 'CONQUER_POSSIBLE'
					t = "The conquering attempt was started. <a class=\"conductConquer\" attemptId=\"#{responseData.conqueringAttempt.id}\">Conduct it immediately</a>"
				else
					t = 'The conquering attempt was started and team members that are around were invited to join.'

				@notify 'Initiate conquer', t, 'success'
				$(".conductConquer").click () -> 
					attemptId = $(@).attr 'attemptId'
					console.log "conduct #{attemptId} immediately"
					@conquer attemptId

	notify: (title, text, type) ->
		$.pnotify
			title: title
			text: text
			type: type