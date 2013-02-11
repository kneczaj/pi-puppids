class ArWars.ConquerManager

	constructor: () ->

	setNotificationsManager: (nm) ->
		@notificationsManager = nm

	setMapInfoManager: (mim) ->
		@mapInfoManager = mim

	setSideBar: (sb) ->
		@sidebar = sb

	conquer: (conqueringAttemptId) ->
		data = 
			conqueringAttemptId: conqueringAttemptId

		$.getJSON '/conquer/conquer', data, (responseData) => 
			if responseData == 'LOST'
				@sidebar.loadResourcesOfPlayer()
				@sidebar.reloadResourceSourcesOfPlayer()
				@sidebar.loadResourcesOfTeam()
				@sidebar.reloadUnitsOfPlayer()
				
				@notificationsManager.notify 'Conquer lost', 'Your conquering attempt was unsuccessful', 'info'

			if responseData == 'PLAYER_NOT_NEARBY'
				@notificationsManager.notify 'Conquer', 'Could not finish conquering attempt. You have to move towards the place you want to conquer', 'info'

			if responseData == 'PLACE_ALREADY_BELONGS_TO_FACTION'
				@notificationsManager.notify 'Conquer', 'The place already belongs to your faction', 'info'

			if responseData == 'RESOURCES_DO_NOT_SUFFICE'
				@notificationsManager.notify 'Conquer', 'Your resources do not suffice the resource demands of this place', 'info'

			if responseData == 'NUMBER_OF_ATTACKERS_DOES_NOT_SUFFICE'
				@notificationsManager.notify 'Conquer', 'The number of teammates you gathered around the place does not suffice', 'info'

			if responseData == 'PLAYER_HAS_INSUFFICIENT_RESOURCES'
				@notificationsManager.notify 'Conquer', 'Your teammates have inssufficient resources to attack this place', 'info'

			if responseData == 'SUCCESSFUL' 
				@sidebar.loadResourcesOfPlayer()
				@sidebar.reloadResourceSourcesOfPlayer()
				@sidebar.loadResourcesOfTeam()
				@sidebar.reloadUnitsOfPlayer()
				@mapInfoManger.loadConqueredPlaces

				@notificationsManager.notify 'Conquering successful', 'Conquering attempt was successful', 'success'

	joinConquer: (conqueringAttemptId) ->
		d = 
			conqueringAttemptId: conqueringAttemptId

		$.getJSON '/conquer/joinConquer', d, (responseData) =>
			if responseData == 'UNALLOWED_TO_JOIN'
				@notificationsManager.notify 'Join conquer', 'You are not allowed to join the conquering attempt', 'info'

			if responseData == 'CONQUER_CANCELED'
				@notificationsManager.notify 'Join conquer', 'Could not join the conquer because it was canceled', 'info'

			if responseData == 'CONQUER_ALREADY_ENDED'
				@notificationsManager.notify 'Join conquer', 'Could not join the conquer because it already ended', 'info'

			if responseData == 'SUCCESSFUL'
				@notificationsManager.notify 'Joined Conquer', 'You joined the conquer', 'success'

	cancelConquer: (conqueringAttemptId) ->
		d = 
			conqueringAttemptId: conqueringAttemptId

		$.getJSON '/conquer/cancelConquer', d, (responseData) ->
			@notificationsManager.notify 'Canceled Conquer', 'The conquering attempt was canceled', 'success'

	initiateConquer: (uuid, reference) ->
		data = 
			uuid: uuid
			reference: reference

		$.getJSON '/conquer/initiateConquer', data, (responseData) =>
			if responseData.type == 'PLAYER_NOT_NEARBY'	
				@notificationsManager.notify 'Error', 'You are too far away from the place you want to conquer', 'error'

			if responseData.type == 'PLACE_ALREADY_BELONGS_TO_FACTION'
				@notificationsManager.notify '', 'This place already belongs to your faction', 'info'

			if responseData.type == 'SUCCESSFUL'
				if responseData.conqueringStatus == 'CONQUER_POSSIBLE'
					t = "The conquering attempt was started. <a class=\"conductConquer\" attemptId=\"#{responseData.conqueringAttempt.id}\">Conduct it immediately</a>"
				else
					t = 'The conquering attempt was started and team members that are around were invited to join.'

				@notificationsManager.notify 'Initiate conquer', t, 'success'
				$(".conductConquer").click () => 
					attemptId = responseData.conqueringAttempt.id
					@conquer attemptId