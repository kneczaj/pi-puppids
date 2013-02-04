class ArWars.MapInfoManager
	
	places: []
	placeMarkers: []

	constructor: (@playerPositionManager) ->
		@map = @playerPositionManager.getMap()

	
	loadConqueredPlaces: () ->
		$.getJSON "/mapinfo/getConqueredPlaces", (data) =>
			@infowindow = new google.maps.InfoWindow(content: "Loading...")
			$.each data, (key, val) =>
				@places[key] = val
				@createMarker key, val
					
	createMarker: (pid, place) ->
		placeLoc = new google.maps.LatLng place.lat, place.lng
		iconUrl = "/assets/images/resources/" + place.resource.toLowerCase() + "_marker_" + place.faction + ".png"
		
		markerOpts = 
			map: @map
			position: placeLoc
			icon: iconUrl
		
		marker = new google.maps.Marker markerOpts
		@placeMarkers[pid] = marker
		
		google.maps.event.addListener marker, "click", () =>
			@setInfowindow place, marker
			
	setInfowindow: (place, marker) =>
		buttonClass = ""
		switch place.faction
		  when "red"
		    buttonClass = "danger"
		  when "blue"
		    buttonClass = "primary"
		    
		content = "#{place.name}<br/>Type: #{place.type}<br/>Resources: #{place.resAmount} <img src=\"/assets/images/resources/#{place.resource.toLowerCase()}_#{place.faction}.png\"><br/>Units: #{place.units}<br/>Conquered by: #{place.team} (#{place.faction} faction)<br/><br/><button class=\"btn btn-block btn-#{buttonClass}\" type=\"button\" placeUuid=\"#{place.uuid}\">Deploy units</button>"
		@infowindow.setContent content
		@infowindow.open @map, marker
		
	notify: (title, text, type) ->
		$.pnotify
			title: title
			text: text
			type: type
