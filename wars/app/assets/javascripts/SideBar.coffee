class ArWars.SideBar
	
	places: []

	constructor: (@playerPositionManager, @conquerManager, @mapInfoManger) ->
		@notificationNode = $("#notifications .accordion-inner")
		@map = playerPositionManager.getMap()

	loadNotifications: () ->
		@notificationNode.html " "

		d = 
			offset: 0
			count: 100

		$.getJSON 'notifications/getHistory', d, (responseData) => 
			content = ''
			$.each responseData, (index, notification) =>
				notificationHtml = ''
				switch notification.messageType 
					when 'ConqueringInvitation'
						@renderConqueringInvitation notification
					when 'ParticipantJoinedConquer'
						@renderParticipantJoinedConquer notification
					when 'ConquerPossible'
						@renderConquerPossible notification

	renderConqueringInvitation: (notification) ->
		aId = notification.conqueringAttemptId
		content = "You are invited to <a name=\"joinConquer-#{aId}\">join the conquer</a> of #{notification.placeName}. It was initiated by #{notification.initiatorName}"
		@notificationNode.append content
		$("a[name='joinConquer-#{aId}']").click () =>
			@conquerManager.joinConquer aId

	renderParticipantJoinedConquer: (notification) ->
		content = "#{notification.participantName} joined your conquering attempt."
		@notificationNode.append content

	renderConquerPossible: (notification) ->
		aId = notification.conqueringAttemptId
		content = "Your conquering attempt for #{notification.placeName} is now possible. <a name=\"conquer-#{aId}\">Conduct it immediately</a>"
		@notificationNode.append content
		$("a[name='conquer-#{aId}']").click() =>
			@conquerManager.conquer aId

	loadResourcesOfPlayer: () ->
		$.getJSON "/resource/getResourcesOfPlayer", (data) =>
			if $.isEmptyObject(data)
				@notify 'Resources', 'The player resources could not be loaded', 'error'
			else
				$.each data, (key, val) ->
					$("#p" + key).text val
					$("#p" + key).parent().popover
						trigger: "hover"
						placement: "bottom"
						content: $("#p" + key).parent().attr("data-content") + val

	loadResourcesOfTeam: () ->
		$.getJSON "/resource/getResourcesOfTeam", (data) =>
			if $.isEmptyObject(data)
				@notify 'Resources', 'The team resources could not be loaded', 'error'
			else
				$.each data, (key, val) ->
					$("#t" + key).text val

	loadResourceSourcesOfPlayer: () ->
		$.getJSON '/resource/getResourceSourcesOfPlayer', (data) =>
			items = []
			itemsOptions = []

			if $.isEmptyObject(data)
				$('#resourceSources').append "<p>Currently there are no conquered places available</p>"
			else
				$.each data.resourceSources, (key, val) =>
					@places[key] = val 

					units = data.unitDeployments[key]
					items.push "<tr><td>#{val.name} <a title='Jump to place on the map' name='jumpToPlace' placeId='#{key}'><img src=\"/assets/images/jump.png\" /></a></td><td>#{val.amount} <img src=\"/assets/images/resources/#{val.resource.toLowerCase()}_white.png\" /></td><td>#{units}</td></tr>"
					itemsOptions.push "<option value=\"#{key}\">#{val.name}</option>"

				$(items.join('')).appendTo '#resourceSources tbody'
				$(itemsOptions.join('')).appendTo '#deployAt'
				$("#deployAt").append $("#deployAt").find("option").sort((a, b) ->
					(if a.text is b.text then 0 else (if a.text < b.text then -1 else 1))
				)
				$("#deployAt").val(0)
				
				$('#resourceSources').dataTable
					sDom: "<'row'<'span2'l><'span8'f>r>t<'row'<'span6'i><'span6'p>>"
					sPaginationType: "bootstrap"
					oLanguage:
						sLengthMenu: "_MENU_ records per page"
					 
					bPaginate: false
					bLengthChange: false
					bFilter: true
					bSort: true
					bInfo: false
					bAutoWidth: false

				$("#resourceSources a[name='jumpToPlace']").bind 'click', (event) =>
					target = event.target or event.srcElement
					@jumpToPlace $(target).parent().attr("placeId")
						
	loadUnitsOfPlayer: () ->
		$.getJSON '/unit/getUnitsOfPlayer', (data) =>
			items = []
			
			totalDeployed = 0
			totalOverall = 0

			if $.isEmptyObject(data)
				$('#playerUnits').append "<p>Currently there are no units available</p>"
			else
				$.each data.undeployedUnits, (key, val) =>
					unitName = key[0].toUpperCase() + key[1..-1].toLowerCase()
					selector = key.toLowerCase() + "DeploySlider"
					overallNumber = data.overallUnits[key]
					totalDeployed += val
					totalOverall += overallNumber
					items.push "<tr><td>#{unitName}</td><td>#{val}</td><td>#{overallNumber}</td></tr>"
					
				$(items.join('')).appendTo '#playerUnits tbody'
				$('#playerUnits tfoot').append "<tr><td>Sum</td><td>#{totalDeployed}</td><td>#{totalOverall}</td></tr>"
				$('#playerUnits').dataTable
					sDom: "<'row'<'span2'l><'span8'f>r>t<'row'<'span6'i><'span6'p>>"
					sPaginationType: "bootstrap"							
					oLanguage:
						sLengthMenu: "_MENU_ records per page"
					 
					bPaginate: false
					bLengthChange: false
					bFilter: false
					bSort: true
					bInfo: false
					bAutoWidth: false
	
	jumpToPlace: (placeId) ->
		place = @places[placeId]
		newLocation = new google.maps.LatLng place.lat, place.lng

		@map.setZoom(19)
		@map.panTo newLocation
		@mapInfoManger.setInfowindow(placeId, @mapInfoManger.places[placeId], @mapInfoManger.placeMarkers[placeId])
		
	buildUnitsClickHandler: () ->
		gruntAmount = $("input#gruntBuildAmount").val()
		infantryAmount = $("input#infantryBuildAmount").val()

		data = 
			gruntAmount: gruntAmount
			infantryAmount: infantryAmount

		$.ajax
			url : "/unit/buildUnits"
			type : "get"
			data : data
			success : (response, textStatus, jqXHR) =>
				if response is 'ok'
					$('#playerUnits').dataTable().fnClearTable()
					$('#playerUnits').dataTable().fnDestroy()
					$('#playerUnits tfoot').empty()
					@loadUnitsOfPlayer()
					@loadResourcesOfPlayer()
					@notify 'Building units', 'You successfully built new units', 'success'
					$("#buildUnitsModal").modal('hide')
					
				else
					@notify 'Building units', 'The building of new units failed', 'error'
			
			error : (jqXHR, textStatus, errorThrown) =>
					@notify 'Building units', 'There was a server error. Please try again later.', 'error'

		return false
		
	deployUnitsClickHandler: () ->
		gruntAmount = $("input#gruntDeployAmount").val()
		infantryAmount = $("input#infantryDeployAmount").val()
		placeId = $("#deployAt option:selected").val()

		data = 
			gruntAmount: gruntAmount
			infantryAmount: infantryAmount
			placeId: placeId

		$.ajax
			url : "/unit/deployUnits"
			type : "get"
			data : data
			success : (response, textStatus, jqXHR) =>
				if response is 'ok'
					$('#resourceSources').dataTable().fnClearTable()
					$('#resourceSources').dataTable().fnDestroy()
					@loadResourceSourcesOfPlayer()
					@notify 'Deploying units', 'You successfully deployed units to a place', 'success'
					$("#deployUnitsModal").modal('hide')
					
				else
					@notify 'Deploying units', 'The deploying of new units failed', 'error'
			
			error : (jqXHR, textStatus, errorThrown) =>
					@notify 'Deploying units', 'There was a server error. Please try again later.', 'error'

		return false
		
	notify: (title, text, type) ->
		$.pnotify
			title: title
			text: text
			type: type
