class ArWars.MapInfoManager
	
	places: []
	placeMarkers: []
	
	@mapOptions = 
		center : new google.maps.LatLng 48.133, 11.566
		zoom : 11
		mapTypeId : google.maps.MapTypeId.ROADMAP
		mapTypeControl: false
		streetViewControl: false
		styles: [
			stylers: [
				invert_lightness: true
			,
				saturation: -80
			]
		,
			featureType: "road"
			elementType: "geometry"
			stylers: [color: "#646464"]
		,
			featureType: "road"
			elementType: "labels.icon"
			stylers: [visibility: "off"]
		,
			featureType: "poi"
			elementType: "labels"
			stylers: [
				color: "#faa732"
			,
				weight: 0.1
			]
		]

	constructor: (@mapNode) ->
		@map = new google.maps.Map @mapNode, ArWars.MapInfoManager.mapOptions

		$.getJSON '/getPlayer', (playerId: window.ArWars.playerId), (responseData) => 
			@player = responseData
			
	getMap: () ->
		@map
	
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
			@setInfowindow pid, place, marker
			
	setInfowindow: (pid, place, marker) =>
		buttonClass = ""
		switch place.faction
		  when "red"
		    buttonClass = "danger"
		  when "blue"
		    buttonClass = "primary"
		    
		content = "#{place.name}<br/>Type: #{place.type}<br/>Resources: #{place.resAmount} <img src=\"/assets/images/resources/#{place.resource.toLowerCase()}_#{place.faction}.png\"><br/>Units: #{place.units}<br/>Conquered by: #{place.team} (#{place.faction} faction)"
		
		if @player.team.name is place.team 
			content += "<br/><br/><button class=\"btn btn-block btn-#{buttonClass}\" type=\"button\" name=\"btnDeploy\" placeId=\"#{pid}\">Deploy units</button>"
		else if not (@player.faction.name is place.faction) 
			content += "<br/><br/><button class=\"btn btn-block btn-#{buttonClass}\" type=\"button\" name=\"btnConquer\" placeId=\"#{pid}\" reference=\"#{place.reference}\">Conquer</button>"
		
		@infowindow.setContent content
		@infowindow.open @map, marker
		$("button[placeId=#{pid}][name=\"btnDeploy\"]").click () -> 
				$("#deployAt").val pid 
				$("#deployUnitsModal").modal 'show'

		$("button[placeId='#{pid}'][name=\"btnConquer\"]").click () =>
			@conquerManager.conquer pid, place.reference
		
	notify: (title, text, type) ->
		$.pnotify
			title: title
			text: text
			type: type
