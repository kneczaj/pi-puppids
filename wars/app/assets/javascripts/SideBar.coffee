class ArWars.SideBar
	
	places: []

	constructor: (@playerPositionManager) ->


	loadResourceSourcesOfPlayer: () ->
		$.getJSON '/resource/getResourceSourcesOfPlayer', (data) =>
			items = []

			if $.isEmptyObject(data)
				$('#resourceSources').append "<p>Currently there are no sources for resources available</p>"
			else
				$.each data.resourceSources, (key, val) =>
					@places[key] = val 

					units = data.unitDeployments[key]
					items.push "<tr><td>#{val.name} <a name='jumpToPlace' placeId='#{key}'>Jump</a></td><td>#{val.amount} <img src=\"/assets/images/resources/#{val.resource.toLowerCase()}_white.png\" /></td><td>#{units}</td></tr>"

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
						@jumpToPlace $(event.srcElement).attr("placeId")
	
	jumpToPlace: (placeId) ->
		place = @places[placeId]
		newLocation = new google.maps.LatLng place.lat, place.lng

		@playerPositionManager.getMap().panTo newLocation
