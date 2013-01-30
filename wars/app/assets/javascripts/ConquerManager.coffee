class ArWars.ConquerManager

	playerPositionManager = null

	constructor: () ->

	setPlayerPositionManager: (pm) ->
		@playerPositionManager = pm

	conquer: (conqueringAttemptId) ->
		data = 
			conqueringAttemptId: conqueringAttemptId

		$.getJSON '/conquer/conquer', data, (responseData) => 
			if responseData == 'LOST'
				@notify 'Conquer lost', 'Your conquering attempt was unsuccessful', 'info'

			if responseData == 'PLAYER_NOT_NEARBY'
				@notify 'Conquer', 'Could not finish conquering attempt. You have to move towards the place you want to conquer', 'info'

			if responseData == 'PLACE_ALREADY_BELONGS_TO_FACTION'
				@notify 'Conquer', 'The place already belongs to your faction', 'info'

			if responseData == 'RESOURCES_DO_NOT_SUFFICE'
				@notify 'Conquer', 'Your resources do not suffice the resource demands of this place', 'info'

			if responseData == 'NUMBER_OF_ATTACKERS_DOES_NOT_SUFFICE'
				@notify 'Conquer', 'The number of teammates you gathered around the place does not suffice', 'info'

			if responseData == 'PLAYER_HAS_INSUFFICIENT_RESOURCES'
				@notify 'Conquer', 'Your teammates have inssufficient resources to attack this place', 'info'

			if responseData == 'SUCCESSFUL'
				@notify 'Conquering successful', 'Conquering attempt was successful', 'success'

	processParticipantJoined: (data) ->
		@notify 'Participant joined', "#{data.participantName} joined conquer", 'info' 

	processConquerPossible: (data) ->
		@notify 'Conquer is now possible', 'You meet all requirements to conquer the place. <a name="conquerNow">Click here to conduct it.</a>', 'success' 
		$("a[name='conquerNow']").click () =>
			@conquer data.conqueringAttemptId


	processConqueringInvitation: (invitation) ->
		console.log invitation
		noticeNode = $ "#conqueringInvitationNotice"
		$(noticeNode).find("#place").text invitation.placeName
		$(noticeNode).find("#invitator").text invitation.initiatorName

		# Jump to fighting place
		$(noticeNode).find("#place").click () => 
			newLocation = new google.maps.LatLng data.lat, data.lng
			@playerPositionManager.getMap().panTo newLocation
	
		$.pnotify
			text: $(noticeNode).html()
			title: 'Conquering Invitation'
			insert_brs: false
		
		# Click on join conquer button
		$("button[name='join']").click () =>
			@joinConquer invitation.conqueringAttemptId

	joinConquer: (conqueringAttemptId) ->
		d = 
			conqueringAttemptId: conqueringAttemptId

		$.getJSON '/conquer/joinConquer', d, (responseData) =>
			if responseData == 'UNALLOWED_TO_JOIN'
				@notify 'Join conquer', 'You are not allowed to join the conquering attempt', 'info'

			if responseData == 'CONQUER_CANCELED'
				@notify 'Join conquer', 'Could not join the conquer because it was canceled', 'info'

			if responseData == 'CONQUER_ALREADY_ENDED'
				@notify 'Join conquer', 'Could not join the conquer because it already ended', 'info'

			if responseData == 'SUCCESSFUL'
				@notify 'Joined Conquer', 'You joined the conquer', 'success'

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
				$(".conductConquer").click () => 
					attemptId = $(@).attr 'attemptId'
					console.log "conduct #{attemptId} immediately"
					@conquer attemptId

	notify: (title, text, type) ->
		$.pnotify
			title: title
			text: text
			type: type