class ArWars.SideBar
	
	places: []

	constructor: (@playerPositionManager) ->	

	loadNotifications: () ->
		notificationNode = $("#notifications .accordion-inner")
		notificationNode.html " "

		d = 
			offset: 0
			count: 100

		$.getJSON 'notifications/getHistory', d, (responseData) => 
			$.each responseData, (index, notification) ->
				notificationNode.append notification.notificationMessage

	loadResourceSourcesOfPlayer: () ->
		$.getJSON '/resource/getResourceSourcesOfPlayer', (data) =>
			items = []

			if $.isEmptyObject(data)
				$('#resourceSources').append "<p>Currently there are no conquered places available</p>"
			else
				$.each data.resourceSources, (key, val) =>
					@places[key] = val 

					units = data.unitDeployments[key]
					items.push "<tr><td>#{val.name} <a title='Jump to place on the map' name='jumpToPlace' placeId='#{key}'><img src=\"/assets/images/jump.png\" /></a></td><td>#{val.amount} <img src=\"/assets/images/resources/#{val.resource.toLowerCase()}_white.png\" /></td><td>#{units}</td></tr>"

				$(items.join('')).appendTo '#resourceSources tbody'
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

		@playerPositionManager.getMap().panTo newLocation
		
	buildUnitsClickHandler: () ->
		gruntAmount = $("input#gruntAmount").val()
		infantryAmount = $("input#infantryAmount").val()

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
					@notify 'Building units', 'You successfully built new units', 'success'
					$("#buildUnitsModal").modal('hide')
					
				else
					@notify 'Building units', 'The building of new units failed', 'error'
			
			error : (jqXHR, textStatus, errorThrown) =>
					@notify 'Building units', 'There was a server error. Please try again later.', 'error'

		return false
		
	notify: (title, text, type) ->
		$.pnotify
			title: title
			text: text
			type: type
