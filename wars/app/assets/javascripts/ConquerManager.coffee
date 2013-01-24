class ArWars.ConquerManager

	constructor: () ->

	conquer: (conqueringAttemptId) ->
		data = 
			conqueringAttemptId: conqueringAttemptId

		$.getJSON '/conquer/conquer', data, (responseData) => 
			console.log responseData
			if responseData == 'SUCCESSFUL'
				$.pnotify
					title: 'Conquering successful'
					text: 'Conquering attempt was successful'
					type: 'success'

	initiateConquer: (uuid, reference) ->
		data = 
			uuid: uuid
			reference: reference

		$.getJSON '/conquer/initiateConquer', data, (responseData) =>
			if responseData.type == 'PLAYER_NOT_NEARBY'	
				$.pnotify 
					title: 'Error'
					text: 'You are too far away from the place you want to conquer'			
					type: 'error'

			if responseData.type == 'PLACE_ALREADY_BELONGS_TO_FACTION'
				$.pnotify 
					title: ''
					text: 'This place already belongs to your faction'
					type: 'info'

			if responseData.type == 'SUCCESSFUL'
				if responseData.conqueringStatus == 'CONQUER_POSSIBLE'
					t = "The conquering attempt was started. <a class=\"conductConquer\" attemptId=\"#{place.id}\">Conduct it immediately</a>"
				else
					t = 'The conquering attempt was started and team members that are around were invited to join.'

				$.pnotify 
					title: 'Initiate conquer'
					text: t
					type: 'success'

			if responseData.conqueringStatus == 'CONQUER_POSSIBLE'
				@conquer responseData.conqueringAttempt.id